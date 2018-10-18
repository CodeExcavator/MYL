package com.productiveengine.myl.bll;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.Log;

import com.productiveengine.myl.common.CriteriaEnum;
import com.productiveengine.myl.common.FileActions;
import com.productiveengine.myl.common.HateCriteria;
import com.productiveengine.myl.common.LoveCriteria;
import com.productiveengine.myl.common.SongStatusEnum;
import com.productiveengine.myl.common.Util;
import com.productiveengine.myl.domainclasses.Settings;

import java.io.File;

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
            //if(currentPosition < 1){

                //ErrorLogBL errorLogBL = new ErrorLogBL();
                //ErrorLog er = new ErrorLog("TEST");
               //er.save();
               // List<ErrorLog> errorLogs = errorLogBL.getAll();

                //throw new RuntimeException("duration "+duration+ " ");
            //}

            double completionPercentage = (((double) currentPosition) / duration) * 100;

            //Apply hate
            switch (HateCriteria.fromInt(settings.hateCriteria)) {
                case TIME_LIMIT:
                    if (settings.hateTimeLimit > currentPosition) {
                        fileActions.deleteFile(songFile.getParent(), songFile.getName());
                        songDeleted = true;
                        songBL.updateByPath(currentSongPath, SongStatusEnum.PROCESSED, CriteriaEnum.HATE);
                    }
                    break;
                case PERCENTAGE:
                    if (settings.hateTimePercentage > completionPercentage) {
                        fileActions.deleteFile(songFile.getParent(), songFile.getName());
                        songDeleted = true;
                        songBL.updateByPath(currentSongPath, SongStatusEnum.PROCESSED, CriteriaEnum.HATE);
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
    public static boolean instantHate(String currentSongPath){
        boolean ok = false;

        try {
            FileActions fileActions = new FileActions();
            File songFile = new File(currentSongPath);
            fileActions.deleteFile(songFile.getParent(), songFile.getName());

            SongBL songBL = new SongBL();
            songBL.updateByPath(currentSongPath, SongStatusEnum.PROCESSED, CriteriaEnum.HATE);

            ok = true;
        }catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }

        return ok;
    }
    public static boolean instantLove(String currentSongPath){
        boolean ok = false;

        try {
            FileActions fileActions = new FileActions();
            File songFile = new File(currentSongPath);
            fileActions.moveFile(songFile.getParent(), songFile.getName(), Util.targetPath);

            SongBL songBL = new SongBL();
            songBL.updateByPath(currentSongPath, SongStatusEnum.PROCESSED, CriteriaEnum.LOVE);

            ok = true;
        }catch (Exception ex){
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
