package com.productiveengine.myl.BLL;

import com.activeandroid.query.Select;
import com.productiveengine.myl.Common.CriteriaEnum;
import com.productiveengine.myl.Common.SongStatusEnum;
import com.productiveengine.myl.DomainClasses.Song;

public class StatsBL {

    public int countAll(){

        int result = 0;

        try{
            new Select()
                    .from(Song.class)
                    .count();
        } catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public int byCriteriaStatus(CriteriaEnum criteriaEnum){

        int result = 0;

        try{
            result = new Select()
                .from(Song.class)
                .where("CriteriaStatus = ?", criteriaEnum.ordinal())
                .count();
        } catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public int bySongStatus(SongStatusEnum songStatusEnum){

        int result = 0;

        try{
            result = new Select()
                .from(Song.class)
                .where("SongStatus = ?", songStatusEnum.ordinal())
                .count();
        } catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
