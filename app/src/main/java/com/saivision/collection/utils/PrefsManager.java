package com.saivision.collection.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.saivision.collection.SaiVisionApplication;

/**
 * Created by vishwajitp on 23/11/15.
 */
public class PrefsManager {



    private static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(SaiVisionApplication.getInstance());
    }

    public static void setPref(String key, String value) {

        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.apply();

    }

    public static String getStringPref(String key) {

        return getSharedPreferences().getString(key, null);

    }

    public static void setPref(String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBooleanPref(String key) {
        return getSharedPreferences().getBoolean(key, false);
    }

    public static void setPref(String key, int value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getIntPref(String key) {
        return getSharedPreferences().getInt(key, -1);
    }

    public static void invalidatePrefs(){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.clear();
        editor.apply();
    }
}
