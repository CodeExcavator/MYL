package com.productiveengine.myl.BLL;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.Log;

import com.productiveengine.myl.Common.FileActions;
import com.productiveengine.myl.Common.HateCriteria;
import com.productiveengine.myl.Common.LoveCriteria;
import com.productiveengine.myl.Common.Util;
import com.productiveengine.myl.DomainClasses.Settings;
import com.productiveengine.myl.UIL.Services.MediaPlayerService;

import java.io.File;

/**
 * Created by Nikolaos on 20/08/2016.
 */

public class CriteriaBL {

    private static final String TAG = CriteriaBL.class.getName();
    public static Settings settings;
    private static SettingsBL settingsBL = new SettingsBL();

    public static boolean applyCriteriaDB(MediaPlayer player, String currentSongPath){

        SongBL songBL = new SongBL();
        boolean ok = false;
        boolean songDeleted = false;

        if(player == null || currentSongPath == null){
            return ok;
        }

        FileActions fileActions = new FileActions();

        try {
            //Get track info
            int duration = Util.convertTrackTimeToSeconds(player.getDuration());
            int currentPosition = Util.convertTrackTimeToSeconds(player.getCurrentPosition());

            File songFile = new File(currentSongPath);
            //Delete from DB
            songBL.deleteByPath(currentSongPath);
            //Get settings from DB
            //(keep staticly in memory for UI access)
            settings = settingsBL.initializeSettingsFromDB();

            double completionPercentage = (((double) currentPosition) / duration) * 100;

            //Apply hate
            switch (HateCriteria.fromInt(settings.hateCriteria)) {
                case TIME_LIMIT:
                    if (settings.hateTimeLimit > currentPosition) {
                        fileActions.deleteFile(songFile.getParent(), songFile.getName());
                        songDeleted = true;
                    }
                    break;
                case PERCENTAGE:
                    if (settings.hateTimePercentage > completionPercentage) {
                        fileActions.deleteFile(songFile.getParent(), songFile.getName());
                        songDeleted = true;
                    }
                    break;
            }
            //Apply love
            if(!songDeleted) {
                switch (LoveCriteria.fromInt(settings.loveCriteria)) {
                    case TIME_LIMIT:
                        if (settings.loveTimeLimit < currentPosition) {
                            fileActions.moveFile(songFile.getParent(), songFile.getName(), settings.targetFolderPath);
                        }
                        break;
                    case PERCENTAGE:
                        if (settings.loveTimePercentage < completionPercentage) {
                            fileActions.moveFile(songFile.getParent(), songFile.getName(), settings.targetFolderPath);
                        }
                        break;
                }
            }
            ok = true;
        }
        catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }
        return ok;
    }

    public static void loadInMemoryCriteria(){
        settings = settingsBL.initializeSettingsFromDB();
    }

    public static int applyCriteriaInMemory_SeekbarColor(int currentPosition, double percentage ){
        int color;

        color = Color.YELLOW;

        if(settings == null){
            return color;
        }

        //Apply hate
        switch (HateCriteria.fromInt(settings.hateCriteria)) {
            case TIME_LIMIT:
                if (settings.hateTimeLimit > currentPosition) {
                    color = Color.RED;
                }
                break;
            case PERCENTAGE:
                if (settings.hateTimePercentage > percentage) {
                    color = Color.RED;
                }
                break;
        }
        //Apply love
        switch (LoveCriteria.fromInt(settings.loveCriteria)) {
            case TIME_LIMIT:
                if (settings.loveTimeLimit < currentPosition) {
                    color = Color.GREEN;
                }
                break;
            case PERCENTAGE:

                if (settings.loveTimePercentage < percentage) {
                    color = Color.GREEN;
                }
                break;
        }

        return color;
    }
}
