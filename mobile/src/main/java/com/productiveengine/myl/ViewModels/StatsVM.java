package com.productiveengine.myl.ViewModels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.productiveengine.myl.BLL.StatsBL;
import com.productiveengine.myl.Common.CriteriaEnum;
import com.productiveengine.myl.Common.SongStatusEnum;
import com.productiveengine.myl.UIL.BR;

import java.io.Serializable;

public class StatsVM extends BaseObservable implements Serializable {

    private String version = "a";
    private int remainingSongs;
    private int processedSongs;
    private int totalSongs;

    private int loved;
    private int neutral;
    private int hated;

    private StatsBL statsBL;

    public StatsVM() {
        initialize();
    }

    private void initialize(){
        statsBL = new StatsBL();
    }

    //--------------------------------------------------------
    public void refreshStats(){
        setRemainingSongs(statsBL.bySongStatus(SongStatusEnum.NEW));
        setProcessedSongs(statsBL.bySongStatus(SongStatusEnum.PROCESSED));
        setTotalSongs(statsBL.countAll());

        setLoved(statsBL.byCriteriaStatus(CriteriaEnum.LOVE));
        setNeutral(statsBL.byCriteriaStatus(CriteriaEnum.NEUTRAL));
        setHated(statsBL.byCriteriaStatus(CriteriaEnum.HATE));
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

    @Bindable
    public int getLoved() {
        return loved;
    }

    public void setLoved(int loved) {
        this.loved = loved;
        notifyPropertyChanged(BR.loved);
    }

    @Bindable
    public int getNeutral() {
        return neutral;
    }

    public void setNeutral(int neutral) {
        this.neutral = neutral;
        notifyPropertyChanged(BR.neutral);
    }

    @Bindable
    public int getHated() {
        return hated;
    }

    public void setHated(int hated) {
        this.hated = hated;
        notifyPropertyChanged(BR.hated);
    }
}
