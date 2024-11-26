package com.azhar.hitungpengeluaran;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Inisialisasi Firebase
        FirebaseApp.initializeApp(this);

        // Set Dark Mode berdasarkan pengaturan yang disimpan
        applyDarkMode();
    }

    private void applyDarkMode() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        boolean isDarkModeEnabled = sharedPreferences.getBoolean("darkMode", false);

        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
