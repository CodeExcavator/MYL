package com.productiveengine.myl.ViewModels;

import android.databinding.BaseObservable;

import com.productiveengine.myl.DomainClasses.Settings;

import java.io.Serializable;

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
