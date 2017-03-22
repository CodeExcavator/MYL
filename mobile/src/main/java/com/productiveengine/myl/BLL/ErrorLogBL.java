package com.productiveengine.myl.BLL;

import com.activeandroid.query.Select;
import com.productiveengine.myl.DomainClasses.ErrorLog;

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
        return new Select()
                .from(ErrorLog.class)
                .execute();
    }

    public int countAll() {
        return new Select()
                .from(ErrorLog.class)
                .count();
    }

}
