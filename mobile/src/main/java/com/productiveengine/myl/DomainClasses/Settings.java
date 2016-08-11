package com.productiveengine.myl.DomainClasses;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.productiveengine.myl.Common.LoveCriteria;

import java.util.List;

/**
 * Created by nifra on 29/07/2016.
 */

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
    @Column(name = "LoveCriteria")
    public int loveCriteria;
    @Column(name = "TimeLimit")
    public int timeLimit;
    @Column(name = "TimePercentage")
    public int timePercentage;
    @Column(name = "ScreenOn")
    public boolean screenOn;

    public Settings(){
        super();
    }

}
