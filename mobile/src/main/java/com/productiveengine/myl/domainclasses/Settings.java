package com.productiveengine.myl.domainclasses;


import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;

@Table(database = AppDatabase.class)
public class Settings extends Model {

    @PrimaryKey
    private Long id;
    @Column(name = "RootFolderPath")
    public String rootFolderPath;
    @Column(name = "TargetFolderPath")
    public String targetFolderPath;
    @Column(name = "RootFolder")
    public String rootFolder;
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
