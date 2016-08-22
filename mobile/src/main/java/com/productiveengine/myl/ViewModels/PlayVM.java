package com.productiveengine.myl.ViewModels;

import android.databinding.BaseObservable;

import com.productiveengine.myl.BLL.SongBL;
import com.productiveengine.myl.DomainClasses.Settings;
import com.productiveengine.myl.DomainClasses.Song;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Nikolaos on 23/07/2016.
 */

public class PlayVM extends BaseObservable implements Serializable {

    private Settings settings;
    private SongBL songBL;
    private List<Song> songList;
    private Song currentSong;

    public PlayVM(){
        songBL = new SongBL();
    }
    //--------------------------------------
    public Settings getSettings() {
        return settings;
    }
    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}
