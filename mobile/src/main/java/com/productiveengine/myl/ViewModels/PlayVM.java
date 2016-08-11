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
import com.productiveengine.myl.BLL.SongBL;
import com.productiveengine.myl.DomainClasses.Settings;
import com.productiveengine.myl.DomainClasses.Song;
import com.productiveengine.myl.UIL.BR;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nikolaos on 23/07/2016.
 */

public class PlayVM extends BaseObservable implements Serializable {

    private String currentSongName;
    private Settings settings;
    private SongBL songBL;
    private List<Song> songList;
    private Song currentSong;
    private boolean traversable = true;

    public PlayVM(){
        songBL = new SongBL();
    }

    public Song getNextSong(){
        currentSong = songBL.fetchNextSong();

        if(currentSong != null){
            setCurrentSongName(currentSong.name);
        }
        return currentSong;
    }

    public void refreshSongList(){
        settings = (new SettingsBL()).initializeSettingsFromDB();

        if(traversable && settings.rootFolderPath != null && settings.rootFolderPath.trim().length() > 0){
            songList = new ArrayList<Song>();
            traverse(new File(settings.rootFolderPath));
            songBL.saveAll(songList);
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
                    songList.add(new Song(file.getName(),filePath));
                }
            }
        }
    }
    //--------------------------------------
    @Bindable
    public String getCurrentSongName() {
        return currentSongName;
    }

    public void setCurrentSongName(String currentSongName) {
        this.currentSongName = currentSongName;
        notifyPropertyChanged(BR.currentSongName);
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}
