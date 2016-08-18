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
