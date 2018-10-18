package com.productiveengine.myl.bll;

import com.productiveengine.myl.common.CriteriaEnum;
import com.productiveengine.myl.common.SongStatusEnum;
import com.productiveengine.myl.domainclasses.Song;
import com.reactiveandroid.query.Select;

public class StatsBL {

    public int countAll(){

        int result = 0;

        try{
            result = Select
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
            result = Select
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
            result = Select
                .from(Song.class)
                .where("SongStatus = ?", songStatusEnum.ordinal())
                .count();
        } catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
