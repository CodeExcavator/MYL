package com.productiveengine.myl.Common;

import java.util.concurrent.TimeUnit;

/**
 * Created by Nikolaos on 20/08/2016.
 */

public class Util {

    public static int convertTrackTimeToSeconds(int duration){
        return (int) TimeUnit.MILLISECONDS.toMinutes(duration) * 60 +
                (int)(
                        TimeUnit.MILLISECONDS.toSeconds(duration) -
                                (int)TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
                );
    }

    public static String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        // Add hours if there
        if(hours > 0){
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }
}
