package com.productiveengine.myl.ViewModels;

import android.databinding.BaseObservable;

import com.productiveengine.myl.BLL.SongBL;
import com.productiveengine.myl.DomainClasses.Settings;
import com.productiveengine.myl.DomainClasses.Song;

import java.io.Serializable;
import java.util.List;

public class PlayVM extends BaseObservable implements Serializable {

    private Settings settings;

    public PlayVM(){
    }
    //--------------------------------------
    public Settings getSettings() {
        return settings;
    }
    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}
