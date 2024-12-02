package com.example.slacks_lottoevent.model;

import android.content.Context;
import android.provider.Settings;
/**
 * Singleton class representing a User.
 * This class provides a unique user identifier based on the device's ID.
 * */
public class User {
    private static User instance;
    private final String deviceId;
    /**
     * Private constructor to prevent external instantiation.
     * Initializes the device ID using the provided {@link Context}.
     *
     * @param context the application context used to retrieve the Android ID
     */
    private User(Context context) {
        // Initialize deviceId using the provided Context
        deviceId = Settings.Secure.getString(context.getContentResolver(),
                                             Settings.Secure.ANDROID_ID);
    }

    /**
     * Initializes the singleton User instance with the provided context.
     * @param context the application context used to initialize the User instance
     * @throws IllegalStateException if the method is called after the instance is already initialized
     */
    public static synchronized void initialize(Context context) {
        if (instance == null) {
            instance = new User(context.getApplicationContext());
        }
    }

    /**
     * Returns the singleton User instance.
     *
     * @return the singleton User instance
     * @throws IllegalStateException if the User instance has not been initialized
     */
    public static synchronized User getInstance() {
        if (instance == null) {
            throw new IllegalStateException("User not initialized. Call initialize() first.");
        }
        return instance;
    }
    /**
     * Returns the device ID associated with the User.
     *
     * @return the device ID as a {@link String}
     */
    public String getDeviceId() {
        return deviceId;
    }
}
