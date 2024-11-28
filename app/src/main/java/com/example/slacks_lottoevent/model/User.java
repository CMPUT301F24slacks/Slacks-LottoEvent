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

    // Initialize the User instance once with context
    public static synchronized void initialize(Context context) {
        if (instance == null) {
            instance = new User(context.getApplicationContext());
        }
    }

    // Get the singleton instance without passing context
    public static synchronized User getInstance() {
        if (instance == null) {
            throw new IllegalStateException("User not initialized. Call initialize() first.");
        }
        return instance;
    }

    public String getDeviceId() {
        return deviceId;
    }
}
