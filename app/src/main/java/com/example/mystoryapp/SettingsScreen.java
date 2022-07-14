package com.example.mystoryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class SettingsScreen extends AppCompatActivity {

    // Declarations for app theme
    SharedPreferences appSettingsPrefs;
    SharedPreferences.Editor sharedPrefsEdit;
    Boolean isNightModeOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_screen);

        // KNOWN ISSUE: sometimes dark mode is automatically turned on when loading this activity for the first time

        // Remember whether the user set light or dark theme
        appSettingsPrefs = getSharedPreferences("AppSettingsPrefs", 0);
        sharedPrefsEdit = appSettingsPrefs.edit();
        isNightModeOn = appSettingsPrefs.getBoolean("NightMode", false);

        // If flag is true, set theme to night
        if(isNightModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    // Change app theme from light to dark or vice versa
    public void clickUpdateTheme(View view){
        if(isNightModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            sharedPrefsEdit.putBoolean("NightMode", false);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            sharedPrefsEdit.putBoolean("NightMode", true);
        }
        sharedPrefsEdit.apply();
    }
}