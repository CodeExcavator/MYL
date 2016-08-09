package com.productiveengine.myl.BLL;

import java.io.File;
import java.io.IOException;
import java.util.Random;

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

    }

    /**
     * Destroy Service and the worker thread
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d(TAG, "onDestroy");
        //APTIONActivity.initialPlay = true;
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
       //Get next song
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