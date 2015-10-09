package com.ampt.bluetooth.Util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by malith on 8/11/15.
 */
public class SharedPref {

    public static long getDefaultDogId(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("com.AMPT", Context.MODE_PRIVATE);
        long defaultDogId = sharedPref.getLong("com.ampt.defaultDogId", 1);
        return defaultDogId;
    }

    public static void setDefaultDogId(Context context, long value) {
        SharedPreferences sharedPref = context.getSharedPreferences("com.AMPT", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong("com.ampt.defaultDogId", value);
        editor.commit();
    }


    public static long getCurrentDogId(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("com.AMPT", Context.MODE_PRIVATE);
        long currentDogId = sharedPref.getLong("com.ampt.currentDogId", 0);
        return currentDogId;
    }

    public static void setCurrentDogId(Context context, long value) {
        SharedPreferences sharedPref = context.getSharedPreferences("com.AMPT", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong("com.ampt.currentDogId", value);
        editor.commit();
    }
}
