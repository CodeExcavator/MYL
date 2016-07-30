package com.productiveengine.myl.ViewModels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.io.Serializable;

/**
 * Created by Nikolaos on 23/07/2016.
 */

public class PlayVM extends BaseObservable implements Serializable {

    private String currentSong;
    //private SettingsVM settingsVM;

    public PlayVM(){

    }

    public void playNextSong(){

        //Check love

    }

    @Bindable
    public String getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(String currentSong) {
        this.currentSong = currentSong;
    }

    /*
    @Bindable
    public SettingsVM getSettingsVM() {
        return settingsVM;
    }

    public void setSettingsVM(SettingsVM settingsVM) {
        this.settingsVM = settingsVM;
    }
    */
}
