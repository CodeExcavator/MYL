package com.productiveengine.myl.Common;

public enum SongStatusEnum {
    NEW, PROCESSED;

    public static SongStatusEnum fromInt(int x) {
        switch(x) {
            case 0:
                return NEW;
            case 1:
                return PROCESSED;
        }
        return null;
    }
}
