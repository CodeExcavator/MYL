package com.productiveengine.myl.bll;

import com.productiveengine.myl.domainclasses.ErrorLog;
import com.reactiveandroid.query.Select;

import java.io.Serializable;
import java.util.List;

public class ErrorLogBL implements Serializable {

    private static final String TAG = ErrorLogBL.class.getName();


    public void save(Exception e){

        try {
            ErrorLog errorLog = new ErrorLog(""+e);
            errorLog.save();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public List<ErrorLog> getAll() {
        return Select
                .from(ErrorLog.class)
                .fetch();
    }

    public int countAll() {
        return Select
                .from(ErrorLog.class)
                .count();
    }

}
