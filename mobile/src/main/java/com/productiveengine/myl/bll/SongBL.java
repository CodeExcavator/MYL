package com.productiveengine.myl.bll;

import android.util.Log;

import com.productiveengine.myl.common.CriteriaEnum;
import com.productiveengine.myl.common.FileActions;
import com.productiveengine.myl.common.SongStatusEnum;
import com.productiveengine.myl.domainclasses.AppDatabase;
import com.productiveengine.myl.domainclasses.Song;
import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.query.Delete;
import com.reactiveandroid.query.Select;
import com.reactiveandroid.query.Update;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

public class SongBL implements Serializable{

    private static final String TAG = SongBL.class.getName();

    public Song fetchNextSong(){

        int min = 0;
        int max = countAvailable();

        if(max == 0 ){ return null; }

        Random r = new Random();
        int nthSong = r.nextInt(max - min) + min;

        if(max == 1 ){ nthSong = 0; }

        Song song = Select
                        .from(Song.class)
                        .where("SongStatus = ?", SongStatusEnum.NEW.ordinal())
                        .limit(1).offset(nthSong)
                        .fetchSingle();
        return song;
    }

    public List<Song> selectWithCriteria(){

        return Select
                .from(Song.class)
                .where("SongStatus = ? AND criteriaStatus in (?,?)",
                        SongStatusEnum.PROCESSED.ordinal(),
                        CriteriaEnum.HATE.ordinal(),
                        CriteriaEnum.NEUTRAL.ordinal())
                .orderBy("RANDOM()")
                .fetch();
    }

    public void saveAll(List<Song> songList){

        //ReActiveAndroid.getDatabase(AppDatabase.class).beginTransaction();
        try {
            for (Song song: songList) {
                song.songStatus = SongStatusEnum.NEW.ordinal();
                song.criteriaStatus = CriteriaEnum.NONE.ordinal();
                song.save();
            }
            //ActiveAndroid.setTransactionSuccessful();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            //ReActiveAndroid.getDatabase(AppDatabase.class).endTransaction();
        }
    }

    //Logical delete
    public void deleteByPath(String path){

        if(path != null){
            Update.table(Song.class)
                    .set("SongStatus = ?, criteriaStatus = ?", + SongStatusEnum.PROCESSED.ordinal(), CriteriaEnum.HATE.ordinal())
                    .where("Path = ?", path)
                    .execute();
        }
    }

    public void updateByPath(String path, SongStatusEnum sse, CriteriaEnum ce){

        if(path != null){
            Update.table(Song.class)
                    .set("SongStatus = ?, criteriaStatus = ?", sse.ordinal(), ce.ordinal())
                    .where("Path = ?", path)
                    .execute();


            Update.table(Song.class)
                    .set("SongStatus = ?", sse.ordinal())
                    .where("Path = ?", path)
                    .execute();
        }
    }

    public void deleteNeutralSongs(){

        FileActions fileActions = new FileActions();
        //ReActiveAndroid.getDatabase(AppDatabase.class).beginTransaction();

        try {
            try {
                List<Song> neutralSongs = Select
                        .from(Song.class)
                        .where("criteriaStatus = ?", CriteriaEnum.NEUTRAL.ordinal())
                        .fetch();

                for (Song song : neutralSongs) {
                    fileActions.deleteFile(song.path);
                }
                Delete.from(Song.class).where("SongStatus = ?", CriteriaEnum.NEUTRAL).execute();

                //ReActiveAndroid.getDatabase(AppDatabase.class).setTransactionSuccessful();
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            } finally {
                //ReActiveAndroid.getDatabase(AppDatabase.class).endTransaction();
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    public List<Song> getAll() {
        return Select
                .from(Song.class)
                .fetch();
    }

    public int countAll() {
        return Select
                .from(Song.class)
                .count();
    }

    public int countAvailable(){
        return Select
                .from(Song.class)
                .where("SongStatus = ?", SongStatusEnum.NEW.ordinal())
                .count();
    }

    public void clearAll() {

        //ReActiveAndroid.getDatabase(AppDatabase.class).beginTransaction();

        try {
            Delete.from(Song.class).execute();
            //ReActiveAndroid.getDatabase(AppDatabase.class).setTransactionSuccessful();
        }
        finally {
            //ReActiveAndroid.getDatabase(AppDatabase.class).endTransaction();
        }
    }

    public void printSongs(String path){
        FileActions fileActions = new FileActions();

        fileActions.printSongs(path, selectWithCriteria());
    }
}
