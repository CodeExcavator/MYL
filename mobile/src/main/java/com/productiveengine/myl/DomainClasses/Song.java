package com.productiveengine.myl.DomainClasses;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by Nikolaos on 11/08/2016.
 */

@Table(name = "Songs")
public class Song extends Model {

    @Column(name = "Name")
    public String name;
    @Column(name = "Path")
    public String path;

    public Song(){
        super();
    }

    public Song(String name, String path){
        super();

        this.name = name;
        this.path = path;
    }
}