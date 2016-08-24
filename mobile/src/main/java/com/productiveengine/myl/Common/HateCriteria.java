package com.productiveengine.myl.Common;

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
