package com.productiveengine.myl.UIL.Services;

/**
 * Created by PaulTR
 * https://github.com/PaulTR/AndroidDemoProjects/blob/master/MediaSessionwithMediaStyleNotification/app/src/main/java/com/ptrprograms/mediasessionwithmediastylenotification/MediaPlayerService.java
 */


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Rating;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.productiveengine.myl.BLL.SettingsBL;
import com.productiveengine.myl.BLL.SongBL;
import com.productiveengine.myl.Common.FileActions;
import com.productiveengine.myl.Common.HateCriteria;
import com.productiveengine.myl.Common.LoveCriteria;
import com.productiveengine.myl.DomainClasses.Settings;
import com.productiveengine.myl.DomainClasses.Song;
import com.productiveengine.myl.UIL.R;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by paulruiz on 10/28/14.
 */
public class MediaPlayerService extends Service {

    private static final String TAG = MediaPlayerService.class.getName();

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_REWIND = "action_rewind";
    public static final String ACTION_FAST_FORWARD = "action_fast_foward";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_STOP = "action_stop";

    private MediaPlayer mMediaPlayer;
    private MediaSessionManager mManager;
    private MediaSession mSession;
    private MediaController mController;

    private SettingsBL settingsBL;
    private SongBL songBL;
    private String currentSongPath;

    LocalBroadcastManager broadcaster;
    static final public String MEDIA_PLAYER_RESULT = "com.productiveengine.myl.UIL.Services.MediaPlayerService.REQUEST_PROCESSED";
    static final public String MEDIA_PLAYER_MSG = "com.productiveengine.myl.UIL.Services.MediaPlayerService.MEDIA_PLAYER_MSG";

    public void sendResult(String message) {
        Intent intent = new Intent(MEDIA_PLAYER_RESULT);
        if(message != null)
            intent.putExtra(MEDIA_PLAYER_MSG, message);
        broadcaster.sendBroadcast(intent);
    }

    @Override
    public void onCreate() {

        broadcaster = LocalBroadcastManager.getInstance(this);
        settingsBL = new SettingsBL();
        songBL = new SongBL();
    }

    private boolean applyCriteria(MediaPlayer player, String currentSongPath){

        boolean ok = false;
        boolean songDeleted = false;

        if(player == null || currentSongPath == null){
            return ok;
        }

        FileActions fileActions = new FileActions();

        try {
            //Get track info
            int duration = convertTrackTimeToSeconds(player.getDuration());
            int currentPosition = convertTrackTimeToSeconds(player.getCurrentPosition());

            File songFile = new File(currentSongPath);
            //Delete from DB
            songBL.deleteByPath(currentSongPath);
            //Get settings from DB
            Settings settings = settingsBL.initializeSettingsFromDB();
            //Apply hate
            switch (HateCriteria.fromInt(settings.hateCriteria)) {
                case TIME_LIMIT:
                    if (settings.hateTimeLimit > currentPosition) {
                        fileActions.deleteFile(songFile.getParent(), songFile.getName());
                        songDeleted = true;
                    }
                    break;
                case PERCENTAGE:
                    double completionPercentage = (((double) currentPosition) / duration) * 100;

                    if (settings.hateTimePercentage > completionPercentage) {
                        fileActions.deleteFile(songFile.getParent(), songFile.getName());
                        songDeleted = true;
                    }
                    break;
            }
            //Apply love
            if(!songDeleted) {
                switch (LoveCriteria.fromInt(settings.loveCriteria)) {
                    case TIME_LIMIT:
                        if (settings.loveTimeLimit < currentPosition) {
                            fileActions.moveFile(songFile.getParent(), songFile.getName(), settings.targetFolderPath);
                        }
                        break;
                    case PERCENTAGE:
                        double completionPercentage = (((double) currentPosition) / duration) * 100;

                        if (settings.loveTimePercentage < currentPosition) {
                            fileActions.moveFile(songFile.getParent(), songFile.getName(), settings.targetFolderPath);
                        }
                        break;
                }
            }
            ok = true;
        }
        catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }
        return ok;
    }

    private int convertTrackTimeToSeconds(int duration){
        return (int) TimeUnit.MILLISECONDS.toMinutes(duration) * 60 +
                (int)(
                        TimeUnit.MILLISECONDS.toSeconds(duration) -
                                (int)TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
                );
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    private void handleIntent( Intent intent ) {
        if( intent == null || intent.getAction() == null )
            return;

        String action = intent.getAction();

        if( action.equalsIgnoreCase( ACTION_PLAY ) ) {
            mController.getTransportControls().play();
        } else if( action.equalsIgnoreCase( ACTION_PAUSE ) ) {
            mController.getTransportControls().pause();
        } else if( action.equalsIgnoreCase( ACTION_FAST_FORWARD ) ) {
            mController.getTransportControls().fastForward();
        } else if( action.equalsIgnoreCase( ACTION_REWIND ) ) {
            mController.getTransportControls().rewind();
        } else if( action.equalsIgnoreCase( ACTION_PREVIOUS ) ) {
            mController.getTransportControls().skipToPrevious();
        } else if( action.equalsIgnoreCase( ACTION_NEXT ) ) {
            mController.getTransportControls().skipToNext();
        } else if( action.equalsIgnoreCase( ACTION_STOP ) ) {
            mController.getTransportControls().stop();
        }
    }

    private Notification.Action generateAction( int icon, String title, String intentAction ) {
        Intent intent = new Intent( getApplicationContext(), MediaPlayerService.class );
        intent.setAction( intentAction );
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new Notification.Action.Builder( icon, title, pendingIntent ).build();
    }

    private void buildNotification( Notification.Action action ) {
        Notification.MediaStyle style = new Notification.MediaStyle();

        Intent intent = new Intent( getApplicationContext(), MediaPlayerService.class );
        intent.setAction( ACTION_STOP );
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        Notification.Builder builder = new Notification.Builder( this )
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle( "Media Title" )
                .setContentText( "Media Artist" )
                .setDeleteIntent( pendingIntent )
                .setStyle(style);

        builder.addAction( generateAction( android.R.drawable.ic_media_previous, "Previous", ACTION_PREVIOUS ) );
        builder.addAction( generateAction( android.R.drawable.ic_media_rew, "Rewind", ACTION_REWIND ) );
        builder.addAction( action );
        builder.addAction( generateAction( android.R.drawable.ic_media_ff, "Fast Foward", ACTION_FAST_FORWARD ) );
        builder.addAction( generateAction( android.R.drawable.ic_media_next, "Next", ACTION_NEXT ) );
        style.setShowActionsInCompactView(0,1,2,3,4);

        NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        notificationManager.notify( 1, builder.build() );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if( mManager == null ) {
            initMediaSessions();
        }

        handleIntent( intent );
        return super.onStartCommand(intent, flags, startId);
    }

    private void initMediaSessions() {
        mSession = new MediaSession(getApplicationContext(), "simple player session");
        mController = new MediaController(getApplicationContext(), mSession.getSessionToken());

        mSession.setCallback(new MediaSession.Callback(){
             @Override
             public void onPlay() {
                 super.onPlay();
                 Log.e( "MediaPlayerService", "onPlay");

                 if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
                     mMediaPlayer.stop();
                 }
                 mMediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(currentSongPath));
                 mMediaPlayer.start();

                 buildNotification( generateAction( android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE ) );
             }

             @Override
             public void onPause() {
                 super.onPause();
                 Log.e( "MediaPlayerService", "onPause");
                 mMediaPlayer.pause();
                 buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY));
             }

             @Override
             public void onSkipToNext() {
                 super.onSkipToNext();
                 Log.e( "MediaPlayerService", "onSkipToNext");


                 applyCriteria(mMediaPlayer, currentSongPath);
                 Song song = songBL.fetchNextSong();

                 if(song != null && song.name != null && song.name.trim().length() > 0){
                     currentSongPath = song.path;
                     sendResult(song.name);
                     onPlay();
                 }
                 else{
                     sendResult("Song list is empty!");
                 }
                 buildNotification( generateAction( android.R.drawable.ic_media_next, "Next", ACTION_NEXT ) );
             }

             @Override
             public void onSkipToPrevious() {
                 super.onSkipToPrevious();
                 Log.e( "MediaPlayerService", "onSkipToPrevious");
                 //Change media here
                 buildNotification( generateAction( android.R.drawable.ic_media_previous, "Previous", ACTION_PAUSE ) );
             }

             @Override
             public void onFastForward() {
                 super.onFastForward();
                 Log.e( "MediaPlayerService", "onFastForward");
                 //Manipulate current media here
             }

             @Override
             public void onRewind() {
                 super.onRewind();
                 Log.e( "MediaPlayerService", "onRewind");
                 //Manipulate current media here
             }

             @Override
             public void onStop() {
                 super.onStop();
                 Log.e( "MediaPlayerService", "onStop");
                 mMediaPlayer.stop();

                 NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                 notificationManager.cancel( 1 );
                 Intent intent = new Intent( getApplicationContext(), MediaPlayerService.class );
                 stopService( intent );
             }

             @Override
             public void onSeekTo(long pos) {
                 super.onSeekTo(pos);
             }

             @Override
             public void onSetRating(Rating rating) {
                 super.onSetRating(rating);
             }
         }
        );
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mSession.release();
        return super.onUnbind(intent);
    }

    public String getCurrentSongPath() {
        return currentSongPath;
    }

    public void setCurrentSongPath(String currentSongPath) {
        this.currentSongPath = currentSongPath;
    }
}