package com.example.slacks_lottoevent.model;

import android.content.Context;
import android.provider.Settings;

public class User {
    private static User instance;
    private final String deviceId;

    private User(Context context) {
        // Initialize deviceId using the provided Context
        deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    // Use a synchronized singleton pattern with context
    public static synchronized User getInstance(Context context) {
        if (instance == null) {
            instance = new User(context.getApplicationContext());
        }
        return instance;
    }

    public String getDeviceId() {
        return deviceId;
    }
}
