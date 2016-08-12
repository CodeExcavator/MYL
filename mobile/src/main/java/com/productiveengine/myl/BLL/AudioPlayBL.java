package com.productiveengine.myl.BLL;

import java.io.File;
import java.util.concurrent.TimeUnit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.Rating;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.productiveengine.myl.Common.FileActions;
import com.productiveengine.myl.Common.HateCriteria;
import com.productiveengine.myl.Common.LoveCriteria;
import com.productiveengine.myl.DomainClasses.Settings;
import com.productiveengine.myl.UIL.R;

import static android.media.session.PlaybackState.ACTION_FAST_FORWARD;
import static android.media.session.PlaybackState.ACTION_PAUSE;
import static android.media.session.PlaybackState.ACTION_PLAY;
import static android.media.session.PlaybackState.ACTION_REWIND;


public class AudioPlayBL extends Service implements Runnable{

    @SuppressWarnings("unused")
    private static final String TAG = "AudioPlayService";

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_REWIND = "action_rewind";
    public static final String ACTION_FAST_FORWARD = "action_fast_foward";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_STOP = "action_stop";

    private final IBinder mBinder = new MyBinder();
    private String songPath;

    private MediaPlayer mMediaPlayer;
    private MediaSessionManager mManager;
    private MediaSession mSession;
    private MediaController mController;

    private boolean playOrPause = true;
    private boolean stopPlay = false;
    private boolean notifySeekBar = false;
    private int seekToPoint = 0;

    public static boolean shuffle = false;
    public static boolean loop = false;

    private int randomHotFix = 0;

    private boolean applyLoveCriteria = true;

    private SongBL songBL;
    private SettingsBL settingsBL;

    /**
     * Service binder
     *
     * @param intent
     *
     * @return mBinder
     */
    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }
    /**
     * Create service
     */
    @Override
    public void onCreate() {
        super.onCreate();

        songBL = new SongBL();
        settingsBL = new SettingsBL();
    }

    /**
     * Destroy Service and the worker thread
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d(TAG, "onDestroy");
        //APTIONActivity.initialPlay = true;

        if(applyLoveCriteria){
            applyLoveCriteria = false;
        }
        stopThread();
    }

    /**
     * Start the service
     *
     * @param intent
     * @param startid
     */
    @Override
    public void onStart(Intent intent, int startid)
    {
        super.onStart(intent, startid);

        if(intent!=null)
        {
            Bundle b = intent.getExtras();
            songPath = b != null ? b.getString("songPath") : null;

            File file = new File(songPath);
            if(!file.exists()){return;}

            startThread();// Start the worker thread that will play the music
        }
    }

    /**
     * Play a new song
     * Kill the worker thread and create an new one
     *
     * @param intent
     */
    public void playNewSong(Intent intent)
    {
        Bundle b = intent.getExtras();
        songPath = b != null ? b.getString("songPath") : null;

        File file = new File(songPath);
        if(!file.exists()){return;}

        stopThread();
        startThread();
    }

    //Binder class ---------------------------------------------------------------
    public class MyBinder extends Binder {
        public AudioPlayBL getService() {
            return AudioPlayBL.this;
        }
    }
    //-----------------------------------------------------------------------------

    //Thread-----------------------------------------------------------------------
    private volatile Thread runner;

    /**
     * Start the worker thread
     */
    public synchronized void startThread(){
        if(runner == null){
            runner = new Thread(this);
            runner.start();
        }
    }

    /**
     * Kill the worker thread and the mMediaPlayer
     */
    public synchronized void stopThread(){
        if(runner != null && mMediaPlayer !=null){
            Thread moribund = runner;
            runner = null;
            moribund.interrupt();
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }

    }

    /**
     * Run the worker thread
     */
    public void run(){

        mMediaPlayer = MediaPlayer.create(this, Uri.parse(songPath));

        if(mMediaPlayer == null)
        {
            stopThread();
            return;
        }

        mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mplayer) {

                try {
                    updateOnCompletion(mplayer);
                } catch (Exception e) {

                    e.printStackTrace();
                }

            }
        });
        mMediaPlayer.start();

        while(Thread.currentThread() == runner)
        {
            if(!playOrPause)
            {
                mMediaPlayer.pause();
                playOrPause = true;
                notifySeekBar = false;
            }
        }
    }

    /**
     * Actions to take place upon completion of a song
     *
     * @param mplayer
     */
    public void updateOnCompletion(MediaPlayer mplayer)throws Exception
    {
        //Delete song from the DB


        if(applyLoveCriteria = true) {
            applyLoveCriteria = false;
        }
    }

    public void applyCriteria(){

        if(mMediaPlayer == null){ return; }

        FileActions fileActions = new FileActions();

        //Get track info
        int duration = convertTrackTimeToSeconds(mMediaPlayer.getDuration());
        int currentPosition = convertTrackTimeToSeconds(mMediaPlayer.getCurrentPosition());
        //Delete from DB
        songBL.deleteByPath(songPath);
        //Get settings from DB
        Settings settings = settingsBL.initializeSettingsFromDB();
        //Apply hate
        switch (HateCriteria.fromInt(settings.hateCriteria)){
            case TIME_LIMIT:
                if(settings.hateTimeLimit > currentPosition){
                    fileActions.deleteFile(songPath,"");
                }
                break;
            case PERCENTAGE:
                double completionPercentage = (((double) currentPosition) / duration)  * 100 ;

                if(settings.hateTimePercentage > completionPercentage ){
                    fileActions.deleteFile(songPath,"");
                }
                break;
        }
        //Apply love
        File songFile = new File(songPath);

        switch (LoveCriteria.fromInt(settings.loveCriteria)){
            case TIME_LIMIT:
                if(settings.loveTimeLimit < currentPosition){
                    fileActions.moveFile(songFile.getPath(),songFile.getName(),settings.targetFolderPath);
                }
                break;
            case PERCENTAGE:
                double completionPercentage = (((double) currentPosition) / duration)  * 100 ;

                if(settings.loveTimePercentage > currentPosition){
                    fileActions.moveFile(songFile.getPath(),songFile.getName(),settings.targetFolderPath);
                }
                break;
        }
    }

    private int convertTrackTimeToSeconds(int duration){
        return (int) TimeUnit.MILLISECONDS.toMinutes(duration) * 60 +
                (int)(
                        TimeUnit.MILLISECONDS.toSeconds(duration) -
                        (int)TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
                );
    }

    //PaulTR/AndroidDemoProjects
    //https://github.com/PaulTR/AndroidDemoProjects/blob/master/MediaSessionwithMediaStyleNotification/app/src/main/java/com/ptrprograms/mediasessionwithmediastylenotification/MediaPlayerService.java
    private Notification.Action generateAction( int icon, String title, String intentAction ) {
        Intent intent = new Intent( getApplicationContext(), AudioPlayBL.class );
        intent.setAction( intentAction );
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new Notification.Action.Builder( icon, title, pendingIntent ).build();
    }
    private void buildNotification( Notification.Action action ) {
        Notification.MediaStyle style = new Notification.MediaStyle();

        Intent intent = new Intent( getApplicationContext(), AudioPlayBL.class );
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

    private void initMediaSessions() {
        mMediaPlayer = new MediaPlayer();

        mSession = new MediaSession(getApplicationContext(), "simple player session");
        mController =new MediaController(getApplicationContext(), mSession.getSessionToken());

        mSession.setCallback(new MediaSession.Callback(){
                                 @Override
                                 public void onPlay() {
                                     super.onPlay();
                                     Log.e( "MediaPlayerService", "onPlay");
                                     buildNotification( generateAction( android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE ) );
                                 }

                                 @Override
                                 public void onPause() {
                                     super.onPause();
                                     Log.e( "MediaPlayerService", "onPause");
                                     buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY));
                                 }

                                 @Override
                                 public void onSkipToNext() {
                                     super.onSkipToNext();
                                     Log.e( "MediaPlayerService", "onSkipToNext");

                                     buildNotification( generateAction( android.R.drawable.ic_media_next, "Next", ACTION_NEXT ) );
                                     applyCriteria();
                                 }

                                 @Override
                                 public void onSkipToPrevious() {
                                     super.onSkipToPrevious();
                                     Log.e( "MediaPlayerService", "onSkipToPrevious");
                                     //Change media here
                                     buildNotification( generateAction( android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE ) );
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
                                     //Stop media player here
                                     NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                     notificationManager.cancel( 1 );
                                     Intent intent = new Intent( getApplicationContext(), AudioPlayBL.class );
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
    //Setters - Getters ------------------------------------------------------------------------
    public String getSongPath() {
        return songPath;
    }
    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }
    public boolean isPlayOrPause() {
        return playOrPause;
    }
    public void setPlayOrPause(boolean playOrPause) {
        this.playOrPause = playOrPause;
    }
    public MediaPlayer getmMediaPlayer() {
        return mMediaPlayer;
    }
    public void setmMediaPlayer(MediaPlayer mMediaPlayer) {
        this.mMediaPlayer = mMediaPlayer;
    }
    public boolean isNotifySeekBar() {
        return notifySeekBar;
    }
    public void setNotifySeekBar(boolean notifySeekBar) {
        this.notifySeekBar = notifySeekBar;
    }
    public int getSeekToPoint() {
        return seekToPoint;
    }
    public void setSeekToPoint(int seekToPoint) {
        this.seekToPoint = seekToPoint;
    }
    public boolean isStopPlay() {
        return stopPlay;
    }
    public void setStopPlay(boolean stopPlay) {
        this.stopPlay = stopPlay;
    }
}