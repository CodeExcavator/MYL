package com.productiveengine.myl.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.Rating;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import com.productiveengine.myl.BLL.CriteriaBL;
import com.productiveengine.myl.BLL.ErrorLogBL;
import com.productiveengine.myl.BLL.SongBL;
import com.productiveengine.myl.Common.RequestCodes;
import com.productiveengine.myl.Common.Util;
import com.productiveengine.myl.DomainClasses.ErrorLog;
import com.productiveengine.myl.DomainClasses.Song;
import com.productiveengine.myl.UIL.R;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import static com.productiveengine.myl.Common.RequestCodes.*;

public class MediaPlayerService extends Service {

    private static final String TAG = MediaPlayerService.class.getName();

    private MediaPlayer mMediaPlayer;
    private MediaSession mSession;
    private MediaController mController;
    private MediaMetadataRetriever mmr;
    private SongBL songBL;
    private String currentSongPath;
    private String currentSongName;

    private ErrorLogBL errorLogBL = new ErrorLogBL();

    LocalBroadcastManager broadcaster;

    Timer timer = new Timer();

    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);
        songBL = new SongBL();
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
        }else if( action.equalsIgnoreCase( ACTION_INSTANT_HATE ) ) {
            instantHate();
        }else if( action.equalsIgnoreCase( ACTION_INSTANT_LOVE ) ) {
            instantLove();
        }else if( action.equalsIgnoreCase( ACTION_GO40 ) ) {
            go40();
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

        if(currentSongPath == null){
            return;
        }
        File songFile = new File(currentSongPath);
        String songTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String songArtist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

        Notification.Builder builder = new Notification.Builder( this )
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle( songFile.getName() )
                .setContentText( songArtist )
                .setDeleteIntent( pendingIntent )
                .setStyle(style);

        builder.addAction( generateAction( android.R.drawable.ic_media_previous, "Previous", ACTION_PREVIOUS ) );
        builder.addAction( generateAction( android.R.drawable.ic_media_rew, "Rewind", ACTION_REWIND ) );
        builder.addAction( action );
        builder.addAction( generateAction( android.R.drawable.ic_media_ff, "Fast Forward", ACTION_FAST_FORWARD ) );
        builder.addAction( generateAction( android.R.drawable.ic_media_next, "Next", ACTION_NEXT ) );
        style.setShowActionsInCompactView(0,1,2,3,4);

        NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        notificationManager.notify( 1, builder.build() );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initMediaSessions();
        handleIntent( intent );
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mSession.release();
        timer.cancel();
        return super.onUnbind(intent);
    }

    private void initMediaSessions() {
        mSession = new MediaSession(getApplicationContext(), "simple player session");
        mSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mSession.setActive(true);

        PlaybackState state = new PlaybackState.Builder()
                .setActions(
                        PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PLAY_PAUSE |
                                PlaybackState.ACTION_PLAY_FROM_MEDIA_ID | PlaybackState.ACTION_PAUSE |
                                PlaybackState.ACTION_SKIP_TO_NEXT | PlaybackState.ACTION_SKIP_TO_PREVIOUS)
                .setState(PlaybackState.STATE_PLAYING, 0, 1, SystemClock.elapsedRealtime())
                .build();
        mSession.setPlaybackState(state);
        mController = new MediaController(getApplicationContext(), mSession.getSessionToken());
        mSession.setCallback(new MediaSession.Callback(){
            @Override
            public boolean onMediaButtonEvent(final Intent mediaButtonIntent) {
                return super.onMediaButtonEvent(mediaButtonIntent);
            }

             @Override
             public void onPlay() {
                 super.onPlay();
                 play();
             }

             @Override
             public void onPause() {
                 super.onPause();
                 pause();
             }

             @Override
             public void onSkipToNext() {
                 super.onSkipToNext();
                 skipToNext();
             }

             @Override
             public void onSkipToPrevious() {
                 super.onSkipToPrevious();
                 skipToPrevious();
             }

             @Override
             public void onFastForward() {
                 super.onFastForward();
                 fastForward();
             }

             @Override
             public void onRewind() {
                 super.onRewind();
                 rewind();
             }

             @Override
             public void onStop() {
                 super.onStop();
                 stop();
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

    //region Media functions
    private void play(){

        if(!chkSettings()){ return; }
        if(currentSongPath == null){
            skipToNext();
            return;
        }
        if(mMediaPlayer != null && !mMediaPlayer.isPlaying()){
            startPlayback();
        }
        buildNotification( generateAction( android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE ) );
    }
    private void pause(){

        AudioManager audio=(AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if(audio.isWiredHeadsetOn()){
            skipToNext();
        }else{
            if(mMediaPlayer != null) {
                mMediaPlayer.pause();
            }
            buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY));
        }
    }
    private void stop(){

        if(mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel( 1 );
        timer.cancel();
        Intent intent = new Intent( getApplicationContext(), MediaPlayerService.class );
        stopService( intent );
    }
    private void skipToNext(){

        if(!chkSettings()){ return; }

        if(currentSongPath != null){
            CriteriaBL.applyCriteriaDB(mMediaPlayer, currentSongPath);
        }
        Song song = songBL.fetchNextSong();
        prepareNextSong(song);
        buildNotification( generateAction( android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE  ) );
    }
    private void instantHate(){

        if(currentSongPath == null){
            return;
        }
        CriteriaBL.instantHate(currentSongPath);

        Song song = songBL.fetchNextSong();
        prepareNextSong(song);
    }
    private void instantLove(){

        if(currentSongPath == null){
            return;
        }
        CriteriaBL.instantLove(currentSongPath);

        Song song = songBL.fetchNextSong();
        prepareNextSong(song);
    }
    private void go40(){

        if(mMediaPlayer != null){
            int duration = Util.convertTrackTimeToSeconds(mMediaPlayer.getDuration());
            int currentPosition = Util.convertTrackTimeToSeconds(mMediaPlayer.getCurrentPosition());

            double completionPercentage = (((double) currentPosition) / duration) * 100;

            if(completionPercentage < 40){
                long seekToPosition = Math.round(mMediaPlayer.getDuration() * 0.4);
                mMediaPlayer.seekTo((int)seekToPosition);
            }
        }

    }
    private void prepareNextSong(Song song){
        if(song != null && song.name != null && song.name.trim().length() > 0){
            currentSongPath = song.path;
            currentSongName = song.name;

            if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(currentSongPath));
            mmr = new MediaMetadataRetriever();

            try {
                mmr.setDataSource(getApplicationContext(), Uri.parse(currentSongPath));
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mplayer) {
                        try {
                            skipToNext();
                        } catch (Exception e) {
                            //errorLogBL.save(e);
                            e.printStackTrace();
                        }
                    }
                });
                updateUI();
                startPlayback();
            }
            catch(Exception e){
                if(currentSongPath != null){
                    songBL.deleteByPath(currentSongPath);
                }
                e.printStackTrace();
            }
        }
        else{
            if(songBL.countAvailable() == 0 && mMediaPlayer != null){
                mMediaPlayer.pause();
                mMediaPlayer = null;
            }
            sendResult(getString(R.string.pressRefresh));
        }
    }
    private void skipToPrevious(){

        if(mMediaPlayer != null) {
            mMediaPlayer.stop();

            try {
                mMediaPlayer.prepare();
                mMediaPlayer.seekTo(0);
                play();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        buildNotification( generateAction( android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE ) );
    }
    private void fastForward(){

    }
    private void rewind(){

    }
    //endregion
    private boolean chkSettings(){
        if(!CriteriaBL.chkSettingsFolders()){
            sendResult(getString(R.string.setRootAndTarget));
            return false;
        }
        if(!CriteriaBL.chkSettingsHateLove()){
            sendResult(getString(R.string.setCriteria));
            return false;
        }
        return true;
    }
    private void startPlayback(){
        CriteriaBL.loadInMemoryCriteria();

        mMediaPlayer.start();
        timerFunction();
    }
    //region UI Broadcast
    public void sendResult(String msg){
        RequestCodes.broadcastInfo(broadcaster, MEDIA_PLAYER_RESULT , MEDIA_PLAYER_MSG, msg);
    }
    public void updateUI() {
        Intent intent = new Intent(MEDIA_PLAYER_INFO);

        intent.putExtra(MP_NAME, currentSongName );

        if(mMediaPlayer != null) {
            try {
                intent.putExtra(MP_DURATION, mMediaPlayer.getDuration() + "");
                intent.putExtra(MP_CURRENT_POSITION, mMediaPlayer.getCurrentPosition() + "");
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        broadcaster.sendBroadcast(intent);
    }

    public void timerFunction() {
        final Handler handler = new Handler();

        TimerTask timertask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            if (mMediaPlayer != null) {
                                updateUI();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer = new Timer(); //This is new
        timer.schedule(timertask, 0, 1000); // execute in every 15sec
    }
    //endregion
}