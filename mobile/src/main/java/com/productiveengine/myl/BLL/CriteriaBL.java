package com.productiveengine.myl.BLL;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.Log;

import com.productiveengine.myl.Common.CriteriaEnum;
import com.productiveengine.myl.Common.FileActions;
import com.productiveengine.myl.Common.HateCriteria;
import com.productiveengine.myl.Common.LoveCriteria;
import com.productiveengine.myl.Common.SongStatusEnum;
import com.productiveengine.myl.Common.Util;
import com.productiveengine.myl.DomainClasses.ErrorLog;
import com.productiveengine.myl.DomainClasses.Settings;

import java.io.File;
import java.util.List;

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
            //Delete from DB (logical delete)
            songBL.deleteByPath(currentSongPath);
            //--------------------------------------------------------------------------
            loadInMemoryCriteria();

            //TEST
            if(currentPosition < 1){

                //ErrorLogBL errorLogBL = new ErrorLogBL();
                //ErrorLog er = new ErrorLog("TEST");
               //er.save();
               // List<ErrorLog> errorLogs = errorLogBL.getAll();

                throw new RuntimeException("duration "+duration+ " ");
            }

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
                            fileActions.moveFile(songFile.getParent(), songFile.getName(), Util.targetPath);
                            songBL.updateByPath(currentSongPath, SongStatusEnum.PROCESSED, CriteriaEnum.LOVE);
                        }
                        else{
                            songBL.updateByPath(currentSongPath, SongStatusEnum.PROCESSED, CriteriaEnum.NEUTRAL);
                        }
                        break;
                    case PERCENTAGE:
                        if (settings.loveTimePercentage < completionPercentage) {
                            fileActions.moveFile(songFile.getParent(), songFile.getName(), Util.targetPath);
                            songBL.updateByPath(currentSongPath, SongStatusEnum.PROCESSED, CriteriaEnum.LOVE);
                        }
                        else{
                            songBL.updateByPath(currentSongPath, SongStatusEnum.PROCESSED, CriteriaEnum.NEUTRAL);
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
        //Get settings from DB
        //(keep staticly in memory for UI access)
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

    public static boolean chkSettingsFolders(){

        return !(CriteriaBL.settings == null ||
                CriteriaBL.settings.targetFolderPath == null ||
                CriteriaBL.settings.rootFolderPath == null);
    }

    public static boolean chkSettingsHateLove(){

        return !(CriteriaBL.settings == null ||
                (CriteriaBL.settings.hateTimeLimit == 0 &&
                        CriteriaBL.settings.hateTimePercentage == 0 &&
                        CriteriaBL.settings.loveTimeLimit == 0 &&
                        CriteriaBL.settings.loveTimePercentage == 0
                ));
    }
}
