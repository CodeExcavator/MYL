package com.productiveengine.myl.domainclasses;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;

@Table(database = AppDatabase.class)
public class ErrorLog extends Model {

    @PrimaryKey
    private Long id;
    @Column(name = "Log")
    public String log;

    public ErrorLog(){
        super();
    }

    public ErrorLog(String log){
        super();

        this.log = log;
    }
}