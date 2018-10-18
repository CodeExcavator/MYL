package com.productiveengine.myl.common;


public enum CriteriaEnum {
    NONE, LOVE, NEUTRAL, HATE;

    public static CriteriaEnum fromInt(int x) {
        switch(x) {
            case 0:
                return NONE;
            case 1:
                return LOVE;
            case 2:
                return NEUTRAL;
            case 3:
                return HATE;
        }
        return null;
    }
}
