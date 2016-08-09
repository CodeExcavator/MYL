package com.productiveengine.myl.ViewModels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.productiveengine.myl.BLL.SettingsBL;
import com.productiveengine.myl.Common.LoveCriteria;
import com.productiveengine.myl.DomainClasses.Settings;
import com.productiveengine.myl.UIL.BR;

import java.io.Serializable;

public class SettingsVM  extends BaseObservable implements Serializable{

    private Settings settings;
    private String rootFolderPath;
    private String targetFolderPath;
    private String rootFolder;
    private String targetFolder;

    private LoveCriteria loveCriteria;
    private int timeLimit;
    private int timePercentage;

    private boolean screenOn;

    private SettingsBL settingsBL;

    //-------------------------------
    private boolean timeLimitChk;

    public SettingsVM() {
        settingsBL = new SettingsBL();
        settings = settingsBL.initializeSettingsFromDB();

        //----------------------------------------------
        rootFolderPath = settings.rootFolderPath;
        targetFolderPath = settings.targetFolderPath;
        rootFolder = settings.rootFolder;
        targetFolder = settings.targetFolder;

        loveCriteria = LoveCriteria.fromInt(settings.loveCriteria);

        switch (loveCriteria){
            case TIME_LIMIT:
                timeLimitChk = true;
                break;
            case PERCENTAGE:
                timeLimitChk = false;
                break;
            default:
                timeLimitChk = true;
        }

        timeLimit = settings.timeLimit;
        timePercentage = settings.timePercentage;

        screenOn = settings.screenOn;
        //----------------------------------------------
    }

    private void notifyAndSave(int fieldId){
        //--------------------------------------------
        settings.rootFolderPath = rootFolderPath;
        settings.targetFolderPath = targetFolderPath;
        settings.rootFolder = rootFolder;
        settings.targetFolder = targetFolder;

        if(loveCriteria != null) {
            settings.loveCriteria = loveCriteria.ordinal();
        }
        settings.timeLimit = timeLimit;
        settings.timePercentage = timePercentage;

        settings.screenOn = screenOn;
        //--------------------------------------------

        settingsBL.saveData(settings);
        notifyPropertyChanged(fieldId);
    }

    //Setters getters
    @Bindable
    public String getRootFolderPath() {
        return rootFolderPath;
    }

    public void setRootFolderPath(String rootFolderPath) {
        this.rootFolderPath = rootFolderPath;
        notifyAndSave(BR.rootFolderPath);
    }

    @Bindable
    public String getTargetFolderPath() {
        return targetFolderPath;
    }

    public void setTargetFolderPath(String targetFolderPath) {
        this.targetFolderPath = targetFolderPath;
        notifyAndSave(BR.targetFolderPath);
    }

    @Bindable
    public String getRootFolder() {
        return rootFolder;
    }

    public void setRootFolder(String rootFolder) {
        this.rootFolder = rootFolder;
        notifyAndSave(BR.rootFolder);
    }

    @Bindable
    public String getTargetFolder() {
        return targetFolder;
    }

    public void setTargetFolder(String targetFolder) {
        this.targetFolder = targetFolder;
        notifyAndSave(BR.targetFolder);
    }

    @Bindable
    public LoveCriteria getLoveCriteria() {
        return loveCriteria;
    }

    public void setLoveCriteria(LoveCriteria loveCriteria) {
        this.loveCriteria = loveCriteria;
        notifyAndSave(BR.loveCriteria);
    }

    @Bindable
    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
        notifyAndSave(BR.timeLimit);
    }

    @Bindable
    public int getTimePercentage() {
        return timePercentage;
    }

    public void setTimePercentage(int timePercentage) {
        this.timePercentage = timePercentage;
        notifyAndSave(BR.timePercentage);
    }

    @Bindable
    public boolean isScreenOn() {
        return screenOn;
    }

    public void setScreenOn(boolean screenOn) {
        this.screenOn = screenOn;
        notifyAndSave(BR.screenOn);
    }

    @Bindable
    public boolean isTimeLimitChk() {
        return timeLimitChk;
    }

    public void setTimeLimitChk(boolean timeLimitChk) {
        this.timeLimitChk = timeLimitChk;
    }
}
