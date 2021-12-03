package com.example.minicapstone390.Controllers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceHelper {
    private final SharedPreferences sharedPreferences;

    public SharedPreferenceHelper(Context context) {
        sharedPreferences = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
    }

    // True for Night Mode, False for Light Mode
    public void setTheme(Boolean mode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        assert editor != null;
        editor.putBoolean("themeMode", mode);
        editor.apply();
    }

    // Get theme mode
    public Boolean getTheme() { return sharedPreferences.getBoolean("themeMode", false); }

    // Set score for a device
    public void setScore(float score, String id) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        assert editor != null;
        editor.putFloat(id, score);
        editor.apply();
    }

    // Get score for a device
    public float getScore(String id) { return sharedPreferences.getFloat(id, 1); }
}
