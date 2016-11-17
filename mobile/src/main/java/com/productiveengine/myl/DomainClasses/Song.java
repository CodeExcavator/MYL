package com.productiveengine.myl.DomainClasses;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.productiveengine.myl.Common.CriteriaEnum;
import com.productiveengine.myl.Common.SongStatusEnum;

@Table(name = "Songs")
public class Song extends Model {

    @Column(name = "Name")
    public String name;
    @Column(name = "Path")
    public String path;
    @Column(name = "SongStatus")
    public int songStatus = SongStatusEnum.NEW.ordinal();
    @Column(name = "CriteriaStatus")
    public int criteriaStatus;

    public Song(){
        super();
    }

    public Song(String name, String path){
        super();

        this.name = name;
        this.path = path;
    }
}