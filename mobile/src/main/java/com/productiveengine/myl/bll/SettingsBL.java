package com.productiveengine.myl.bll;


import com.productiveengine.myl.domainclasses.Settings;
import com.reactiveandroid.query.Delete;
import com.reactiveandroid.query.Select;

import java.util.List;

public class SettingsBL {
    Settings settings;

    public Settings initializeSettingsFromDB(){
        List<Settings> allSettings = getAll();

        if(allSettings == null || allSettings.size() == 0){
            //Create new
            settings = new Settings();
        }
        else if(allSettings.size() == 1){
            settings = allSettings.get(0);
        }
        else{
            //Delete all settings and create a
            //new record.
            Delete.from(Settings.class).execute();
            settings = new Settings();
        }
        return settings;
    }

    public void saveData(Settings settings){
        settings.save();
    }

    public List<Settings> getAll() {
        List<Settings> settings = null;

        try{
            settings = Select
            .from(Settings.class)
            .fetch();
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return settings;
    }
}
