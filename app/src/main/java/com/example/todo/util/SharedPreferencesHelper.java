package com.example.todo.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class SharedPreferencesHelper {

    private static final String PREF_TIME = "Pref_time";
    private static final String PREV_PAGER_TAB_INDEX = "prev_pager_tab_index";

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

    public void saveLastVisitedPagerTabIndex(int tabIndex){
        preferences.edit().putInt(PREV_PAGER_TAB_INDEX, tabIndex).apply();
    }

    public int getLastVisitedPagerTabIndex(){
        return preferences.getInt(PREV_PAGER_TAB_INDEX, 0);
    }

}
