package com.example.slacks_lottoevent;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;


//testing purposes
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.libraries.places.api.Places;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

/**
 *
 *
 *
 *
 *
 *
 * Relevant Documentation and Resources:
 * https://developer.android.com/reference/android/content/SharedPreferences
 * https://stackoverflow.com/questions/48740056/splash-screen-using-handler
 * */

/**
 * MainActivity is the main activity for the app.
 * It is the first activity that is launched when the app is opened.
 */
public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    CollectionReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();

        usersRef = db.collection("profiles");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

//
//        createNotificationChannel();
//        notificationHelper = new NotificationHelper(this);
//        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//
//        grabbingNotifications(deviceId);
//        Notifications notification = new Notifications();
//        notification.removeNotifications(deviceId);


        new Handler().postDelayed(() ->{
            Intent homeIntent = new Intent(MainActivity.this, EventsHomeActivity.class);
            startActivity(homeIntent);

            finish(); // Closing MainActivity to prevent going back to it.
        },2000);
        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(),com.example.slacks_lottoevent.BuildConfig.MAPS_API_KEY);

        }
    }

}