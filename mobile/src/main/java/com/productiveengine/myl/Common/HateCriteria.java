package com.productiveengine.myl.Common;

/**
 * Created by Nikolaos on 12/08/2016.
 */

public enum HateCriteria {
    TIME_LIMIT, PERCENTAGE;

    public static HateCriteria fromInt(int x) {
        switch(x) {
            case 0:
                return TIME_LIMIT;
            case 1:
                return PERCENTAGE;
        }
        return null;
    }

}
