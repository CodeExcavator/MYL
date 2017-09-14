package com.productiveengine.myl.Common;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class RequestCodes {
    public static final int CHOOSE_ROOT_FOLDER = 1;
    public static final int CHOOSE_TARGET_FOLDER = 2;

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_REWIND = "action_rewind";
    public static final String ACTION_FAST_FORWARD = "action_fast_forward";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_STOP = "action_stop";
    public static final String ACTION_INSTANT_LOVE = "action_instant_love";
    public static final String ACTION_INSTANT_HATE = "action_instant_hate";

    static final public String MEDIA_PLAYER_RESULT = "com.productiveengine.myl.Services.MediaPlayerService.MEDIA_PLAYER_RESULT";
    static final public String MEDIA_PLAYER_MSG = "com.productiveengine.myl.Services.MediaPlayerService.MEDIA_PLAYER_MSG";
    static final public String MEDIA_PLAYER_INFO = "com.productiveengine.myl.Services.MediaPlayerService.MEDIA_PLAYER_INFO";
    static final public String MP_NAME = "com.productiveengine.myl.Services.MediaPlayerService.MP_NAME";
    static final public String MP_DURATION = "com.productiveengine.myl.Services.MediaPlayerService.MP_DURATION";
    static final public String MP_CURRENT_POSITION = "com.productiveengine.myl.Services.MediaPlayerService.MP_CURRENT_POSITION";

    public static void broadcastInfo(LocalBroadcastManager broadcaster, String intentS, String extraS, String message) {
        Intent intent = new Intent(intentS);

        if(message != null) {
            intent.putExtra(extraS, message);
        }
        broadcaster.sendBroadcast(intent);
    }
}
