package com.example.minicapstone390.Controllers;

import android.content.Context;
import android.content.Intent;
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

    public Boolean getTheme() { return sharedPreferences.getBoolean("themeMode", false); }

    // Set graph length
    public void setGraphLength(int length) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        assert editor != null;
        editor.putInt("graphLength", length);
        editor.apply();
    }

    public Integer getGraphLength() { return sharedPreferences.getInt("graphLength", 0); }
}
