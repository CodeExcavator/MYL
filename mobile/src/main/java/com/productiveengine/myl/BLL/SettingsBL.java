package com.productiveengine.myl.BLL;

import android.util.Log;

import com.activeandroid.query.Select;
import com.productiveengine.myl.DomainClasses.Settings;

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
            //Settings.deleteAll(Settings.class);
            settings = new Settings();
        }
        return settings;
    }

    public void saveData(Settings settings){
        //TODO: Try catch, error handling
        settings.save();
    }

    public List<Settings> getAll() {
        return new Select()
                .from(Settings.class)
                .execute();
    }
}
