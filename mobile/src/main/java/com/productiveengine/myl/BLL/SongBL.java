package com.productiveengine.myl.BLL;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.productiveengine.myl.Common.CriteriaEnum;
import com.productiveengine.myl.Common.SongStatusEnum;
import com.productiveengine.myl.DomainClasses.Song;

import java.io.Serializable;
import java.util.List;

public class SongBL implements Serializable{

    public Song fetchNextSong(){

        Song song = new Select()
                        .from(Song.class)
                        .where("SongStatus = ?", SongStatusEnum.NEW.ordinal())
                        .orderBy("RANDOM()")
                        .executeSingle();

        return song;
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
}
