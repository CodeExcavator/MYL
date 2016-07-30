package com.productiveengine.myl.DomainClasses;

import com.orm.SugarRecord;
import com.orm.dsl.Table;
import com.productiveengine.myl.Common.LoveCriteria;

/**
 * Created by nifra on 29/07/2016.
 */

public class Settings extends SugarRecord{

    private String rootFolderPath;
    private String targetFolderPath;
    private String rootFolder;
    private String targetFolder;

    private int loveCriteria;
    private int timeLimit;
    private int timePercentage;

    private boolean screenOn;

    public Settings(){
    }

    public String getRootFolderPath() {
        return rootFolderPath;
    }

    public void setRootFolderPath(String rootFolderPath) {
        this.rootFolderPath = rootFolderPath;
    }

    public String getTargetFolderPath() {
        return targetFolderPath;
    }

    public void setTargetFolderPath(String targetFolderPath) {
        this.targetFolderPath = targetFolderPath;
    }

    public String getRootFolder() {
        return rootFolder;
    }

    public void setRootFolder(String rootFolder) {
        this.rootFolder = rootFolder;
    }

    public String getTargetFolder() {
        return targetFolder;
    }

    public void setTargetFolder(String targetFolder) {
        this.targetFolder = targetFolder;
    }

    public int getLoveCriteria() {
        return loveCriteria;
    }

    public void setLoveCriteria(int loveCriteria) {
        this.loveCriteria = loveCriteria;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getTimePercentage() {
        return timePercentage;
    }

    public void setTimePercentage(int timePercentage) {
        this.timePercentage = timePercentage;
    }

    public boolean isScreenOn() {
        return screenOn;
    }

    public void setScreenOn(boolean screenOn) {
        this.screenOn = screenOn;
    }
}
