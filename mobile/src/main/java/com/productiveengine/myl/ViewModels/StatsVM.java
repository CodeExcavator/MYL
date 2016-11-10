package com.productiveengine.myl.ViewModels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.productiveengine.myl.BLL.StatsBL;
import com.productiveengine.myl.UIL.BR;

import java.io.Serializable;

public class StatsVM extends BaseObservable implements Serializable {

    private String version = "a";
    private int remainingSongs;
    private int processedSongs;
    private int totalSongs;

    private StatsBL statsBL;

    public StatsVM() {
        initialize();
    }

    private void initialize(){
        statsBL = new StatsBL();
    }
    //--------------------------------------------------------
    @Bindable
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Bindable
    public int getRemainingSongs() {
        return remainingSongs;
    }

    public void setRemainingSongs(int remainingSongs) {
        this.remainingSongs = remainingSongs;
        notifyPropertyChanged(BR.remainingSongs);
    }

    @Bindable
    public int getProcessedSongs() {
        return processedSongs;
    }

    public void setProcessedSongs(int processedSongs) {
        this.processedSongs = processedSongs;
        notifyPropertyChanged(BR.processedSongs);
    }

    @Bindable
    public int getTotalSongs() {
        return totalSongs;
    }

    public void setTotalSongs(int totalSongs) {
        this.totalSongs = totalSongs;
        notifyPropertyChanged(BR.processedSongs);
    }
}
