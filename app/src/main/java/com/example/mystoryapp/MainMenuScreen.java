package com.example.mystoryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainMenuScreen extends AppCompatActivity {

    // Firebase declaration
    private FirebaseAuth mAuth;

    // Access views in layout
    TextView textViewWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_screen);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        // KNOWN ISSUE: username does not get displayed in MainMenu the first time it's loaded, it does after reloading.

        // Set welcome view with username
        textViewWelcome = findViewById(R.id.textViewWelcome);
        textViewWelcome.setText("Welcome back, " + user.getDisplayName());
    }

    // NEW ENTRY BTN
    public void clickNewEntry(View view) {
        // Open NewEntryScreen activity
        Intent intent = new Intent(this, NewEntryScreen.class);
        startActivity(intent);
    }

    // VIEW ENTRIES BTN
    public void clickViewEntries(View view) {
        // Open ViewEntriesScreen activity
        Intent intent = new Intent(this, ViewEntriesScreen.class);
        startActivity(intent);
    }

    // VIEW CALENDAR BTN
    public void clickViewCalendar(View view) {
        // Open ViewCalendarScreen activity
        Intent intent = new Intent(this, ViewCalendarScreen.class);
        startActivity(intent);
    }

    // VIEW STATS BTN
    public void clickViewStats(View view) {
        // Open StatsScreen activity
        Intent intent = new Intent(this, StatsScreen.class);
        startActivity(intent);
    }

    // PROFILE BTN
    public void clickProfile(View view) {
        // Open ProfileScreen activity
        Intent intent = new Intent(this, ProfileScreen.class);
        startActivity(intent);
    }

    // SETTINGS BTN
    public void clickSettings(View view) {
        // Open SettingsScreen activity
        Intent intent = new Intent(this, SettingsScreen.class);
        startActivity(intent);
    }

    // LOGOUT BTN
    public void clickLogout(View view) {
        // Sign out with Firebase and return to login screen
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginScreen.class);
        startActivity(intent);
    }
}