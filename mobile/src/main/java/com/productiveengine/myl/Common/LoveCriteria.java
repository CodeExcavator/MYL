package com.productiveengine.myl.Common;

/**
 * Created by Nikolaos on 19/07/2016.
 */
public enum LoveCriteria {
    TIME_LIMIT, PERCENTAGE;

    public static LoveCriteria fromInt(int x) {
        switch(x) {
            case 0:
                return TIME_LIMIT;
            case 1:
                return PERCENTAGE;
        }
        return null;
    }

}
