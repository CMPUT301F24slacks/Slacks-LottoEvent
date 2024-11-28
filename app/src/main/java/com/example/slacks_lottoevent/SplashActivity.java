package com.example.slacks_lottoevent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.slacks_lottoevent.model.User;
import com.example.slacks_lottoevent.viewmodel.EntrantViewModel;
import com.example.slacks_lottoevent.viewmodel.EventViewModel;
import com.google.android.libraries.places.api.Places;

/**
 * SplashActivity displays the app logo for a few seconds when the app is launched.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        // Initialize shared preferences for user info
        SharedPreferences sharedPreferences = getSharedPreferences("SlacksLottoEventUserInfo",
                                                                   MODE_PRIVATE);
        if (!sharedPreferences.contains("isSignedUp")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isSignedUp", false);
            editor.apply();
        }

        User.initialize(this);
        User user = User.getInstance();

        EntrantViewModel entrantViewModel = new ViewModelProvider(this).get(EntrantViewModel.class);
        EventViewModel eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        // Observe the current entrant
        entrantViewModel.observeEntrant(user.getDeviceId());

        entrantViewModel.getCurrentEntrant().observe(this, currentEntrant -> {
            if (currentEntrant != null) {
                eventViewModel.setWaitlistedEvents(currentEntrant.getWaitlistedEvents());
            }
        });


        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(),
                              com.example.slacks_lottoevent.BuildConfig.MAPS_API_KEY);
        }

        // Delay the transition to MainActivity by 2 seconds
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close SplashActivity
        }, 2000);
    }
}
