package com.productiveengine.myl.DomainClasses;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Settings")
public class Settings extends Model{

    @Column(name = "RootFolderPath")
    public String rootFolderPath;
    @Column(name = "TargetFolderPath")
    public String targetFolderPath;
    @Column(name = "RootFolder")
    public String rootFolder;
    @Column(name = "TargetFolder")
    public String targetFolder;
    //------------------------------
    @Column(name = "HateCriteria")
    public int hateCriteria;
    @Column(name = "HateTimeLimit")
    public int hateTimeLimit;
    @Column(name = "HateTimePercentage")
    public int hateTimePercentage;
    //-----------------------------
    @Column(name = "LoveCriteria")
    public int loveCriteria;
    @Column(name = "LoveTimeLimit")
    public int loveTimeLimit;
    @Column(name = "LoveTimePercentage")
    public int loveTimePercentage;
    //------------------------------
    @Column(name = "ScreenOn")
    public boolean screenOn;

    public Settings(){
        super();
    }

}
