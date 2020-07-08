package com.example.todo.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class SharedPreferencesHelper {

    private static final String PREF_TIME = "Pref_time";

    private SharedPreferences preferences;
    private static SharedPreferencesHelper sharedPreferencesHelper;

    private SharedPreferencesHelper(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPreferencesHelper getInstance(Context context){
        if(sharedPreferencesHelper == null){
            sharedPreferencesHelper = new SharedPreferencesHelper(context);
        }
        return sharedPreferencesHelper;
    }

    public void saveUpdateTime(long time){
        preferences.edit().putLong(PREF_TIME, time).apply();
    }

    public long getUpdateTime(){
        return preferences.getLong(PREF_TIME, 0);
    }

}
