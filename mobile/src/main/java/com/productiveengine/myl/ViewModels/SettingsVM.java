package com.productiveengine.myl.ViewModels;

import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.productiveengine.myl.BL.SettingsBL;
import com.productiveengine.myl.Common.LoveCriteria;
import com.productiveengine.myl.Common.RequestCodes;
import com.productiveengine.myl.DomainClasses.Settings;
import com.productiveengine.myl.UIL.BR;

import java.io.Serializable;
import java.util.List;

import ar.com.daidalos.afiledialog.FileChooserActivity;

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

    public SettingsVM() {
        settingsBL = new SettingsBL();
        settings = settingsBL.initializeSettingsOnDB();
    }

    private void notifyAndSave(int fieldId){
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
}
