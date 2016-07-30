package com.productiveengine.myl.BL;

import com.productiveengine.myl.DomainClasses.Settings;

import java.util.List;

/**
 * Created by nifra on 30/07/2016.
 */

public class SettingsBL {

    public Settings initializeSettingsOnDB(){
        Settings settings;
        List<Settings> allSettings = Settings.listAll(Settings.class);

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
            Settings.deleteAll(Settings.class);
            settings = new Settings();
        }
        return settings;
    }

    public void saveData(Settings settings){
        //TODO: Try catch, error handling
        settings.save();
    }
}
