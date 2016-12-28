package com.moumou.locate;

import android.content.SharedPreferences;

/**
 * Created by MouMou on 28-12-16.
 */

public class Constants {

    private static int activitycounter = 0;

    public static final int RC_LOCATION = 9002;
    public static final int RC_NEW_LOC = 9003;
    public static final int RC_NEW_PLACE = 9004;

    public static final String TAG_LAT = "lat";
    public static final String TAG_LONG = "long";
    public static final String NEW_LAT = "newlat";
    public static final String NEW_LONG = "newlong";
    public static final String NEW_PLACE = "newplace";
    public static final String NEW_LABEL = "newlabel";




    public static SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    public static double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    public static int getActivitycounter() {
        activitycounter++;
        return activitycounter-1;
    }
}
