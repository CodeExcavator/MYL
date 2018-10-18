package com.productiveengine.myl.domainclasses;

import com.productiveengine.myl.common.SongStatusEnum;
import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;

@Table(database = AppDatabase.class)
public class Song extends Model{

    @PrimaryKey
    private Long id;
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