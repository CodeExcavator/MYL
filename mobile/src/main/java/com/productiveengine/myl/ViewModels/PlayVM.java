package com.productiveengine.myl.ViewModels;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.IBinder;

import com.productiveengine.myl.BLL.AudioPlayBL;
import com.productiveengine.myl.BLL.SettingsBL;
import com.productiveengine.myl.DomainClasses.Settings;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nikolaos on 23/07/2016.
 */

public class PlayVM extends BaseObservable implements Serializable {

    private String currentSong;
    private Settings settings;
    private List<String> songPaths;
    private int songIndex = 0;

    private boolean traversable = true;

    public PlayVM(){


    }

    public String getNextSong(){

        String songPath = "";

        if(songPaths != null && songPaths.size() > 0) {
            songPath = songPaths.get(songIndex++);

            setCurrentSong(songPath);
        }
        return songPath;
    }

    public void refreshSongList(){
        settings = (new SettingsBL()).initializeSettingsFromDB();
        songIndex = 0;

        if(traversable){
            songPaths = new ArrayList<>();
            traverse(new File(settings.rootFolderPath));
        }
    }

    public void traverse (File dir) {
        String filePath = "";

        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; ++i) {
                File file = files[i];
                filePath = file.getAbsolutePath();

                if (file.isDirectory()) {
                    traverse(file);
                } else if(filePath.endsWith(".mp3") || filePath.endsWith(".3gp") || filePath.endsWith(".flac") || filePath.endsWith(".ogg") || filePath.endsWith(".wav") ) {
                    songPaths.add(filePath);
                }
            }
        }
    }
    //--------------------------------------
    @Bindable
    public String getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(String currentSong) {
        this.currentSong = currentSong;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}
