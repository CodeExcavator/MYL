package com.productiveengine.myl.BLL;

import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.productiveengine.myl.Common.CriteriaEnum;
import com.productiveengine.myl.Common.FileActions;
import com.productiveengine.myl.Common.HateCriteria;
import com.productiveengine.myl.Common.SongStatusEnum;
import com.productiveengine.myl.DomainClasses.Song;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public class SongBL implements Serializable{

    private static final String TAG = SongBL.class.getName();

    public Song fetchNextSong(){

        Song song = new Select()
                        .from(Song.class)
                        .where("SongStatus = ?", SongStatusEnum.NEW.ordinal())
                        .orderBy("RANDOM()")
                        .executeSingle();

        return song;
    }

    public List<Song> selectWithCriteria(){

        return new Select()
                .from(Song.class)
                .where("SongStatus = ? AND criteriaStatus in (?,?)",
                        SongStatusEnum.PROCESSED.ordinal(),
                        CriteriaEnum.HATE.ordinal(),
                        CriteriaEnum.NEUTRAL.ordinal())
                .orderBy("RANDOM()")
                .execute();
    }

    public void saveAll(List<Song> songList){

        ActiveAndroid.beginTransaction();

        try {
            for (Song song: songList) {
                song.songStatus = SongStatusEnum.NEW.ordinal();
                song.criteriaStatus = CriteriaEnum.NONE.ordinal();
                song.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            ActiveAndroid.endTransaction();
        }
    }

    //Logical delete
    public void deleteByPath(String path){

        if(path != null){
            new Update(Song.class)
                    .set("SongStatus = ?, criteriaStatus = ?", + SongStatusEnum.PROCESSED.ordinal(), CriteriaEnum.HATE.ordinal())
                    .where("Path = ?", path)
                    .execute();
        }
    }

    public void updateByPath(String path, SongStatusEnum sse, CriteriaEnum ce){

        if(path != null){
            new Update(Song.class)
                    .set("SongStatus = ?, criteriaStatus = ?", sse.ordinal(), ce.ordinal())
                    .where("Path = ?", path)
                    .execute();


            new Update(Song.class)
                    .set("SongStatus = ?", sse.ordinal())
                    .where("Path = ?", path)
                    .execute();
        }
    }

    public void deleteNeutralSongs(){

        FileActions fileActions = new FileActions();
        ActiveAndroid.beginTransaction();

        try {
            try {
                List<Song> neutralSongs = new Select()
                        .from(Song.class)
                        .where("criteriaStatus = ?", CriteriaEnum.NEUTRAL.ordinal())
                        .execute();

                for (Song song : neutralSongs) {
                    fileActions.deleteFile(song.path);
                }
                new Delete().from(Song.class).where("SongStatus = ?", CriteriaEnum.NEUTRAL).execute();

                ActiveAndroid.setTransactionSuccessful();
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            } finally {
                ActiveAndroid.endTransaction();
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    public List<Song> getAll() {
        return new Select()
                .from(Song.class)
                .execute();
    }

    public int countAll() {
        return new Select()
                .from(Song.class)
                .count();
    }

    public void clearAll() {

        ActiveAndroid.beginTransaction();

        try {
            new Delete().from(Song.class).execute();
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }
    }

    public void printSongs(String path){
        FileActions fileActions = new FileActions();

        fileActions.printSongs(path, selectWithCriteria());
    }
}
