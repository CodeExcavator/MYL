package com.productiveengine.myl.BLL;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;

import com.productiveengine.myl.Common.FileActions;
import com.productiveengine.myl.Common.HateCriteria;
import com.productiveengine.myl.Common.LoveCriteria;
import com.productiveengine.myl.DomainClasses.Settings;

import static com.productiveengine.myl.Common.HateCriteria.PERCENTAGE;
import static com.productiveengine.myl.Common.HateCriteria.TIME_LIMIT;


public class AudioPlayBL extends Service implements Runnable{

    @SuppressWarnings("unused")
    private static final String TAG = "AudioPlayService";

    private final IBinder mBinder = new MyBinder();
    private String songPath;
    private MediaPlayer player;
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
     * Kill the worker thread and the player
     */
    public synchronized void stopThread(){
        if(runner != null && player!=null){
            Thread moribund = runner;
            runner = null;
            moribund.interrupt();
            player.stop();
            player.release();
        }

    }

    /**
     * Run the worker thread
     */
    public void run(){

        player = MediaPlayer.create(this, Uri.parse(songPath));
        if(player == null)
        {
            stopThread();
            return;
        }

        player.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mplayer) {

                try {
                    updateOnCompletion(mplayer);
                } catch (Exception e) {

                    e.printStackTrace();
                }

            }
        });
        player.start();

        while(Thread.currentThread() == runner)
        {
            if(!playOrPause)
            {
                player.pause();
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

        if(player == null){ return; }

        FileActions fileActions = new FileActions();

        //Get track info
        int duration = convertTrackTimeToSeconds(player.getDuration());
        int currentPosition = convertTrackTimeToSeconds(player.getCurrentPosition());
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
    public MediaPlayer getPlayer() {
        return player;
    }
    public void setPlayer(MediaPlayer player) {
        this.player = player;
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