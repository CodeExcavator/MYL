package com.productiveengine.myl.ViewModels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.io.Serializable;

/**
 * Created by Nikolaos on 23/07/2016.
 */

public class PlayVM extends BaseObservable implements Serializable {

    private String currentSong;

    public PlayVM(){

    }

    @Bindable
    public String getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(String currentSong) {
        this.currentSong = currentSong;
    }
}
