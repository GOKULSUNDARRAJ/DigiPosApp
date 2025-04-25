package com.app.digiposfinalapp;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private static final String PREFS_NAME = "MyPrefs";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Save the state of a switch
    public void saveSwitchState(String key, boolean state) {
        editor.putBoolean(key, state);
        editor.apply();
    }

    // Retrieve the state of a switch
    public boolean getSwitchState(String key) {
        return sharedPreferences.getBoolean(key, false);
    }
}
