package com.productiveengine.myl.DomainClasses;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;


@Table(name = "ErrorLogs")
public class ErrorLog extends Model {

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