package com.example.slacks_lottoevent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


//testing purposes
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;
import java.util.Objects;

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

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        new Handler().postDelayed(() ->{
            SharedPreferences sharedPreferences = getSharedPreferences("SlacksLottoEventUserInfo", MODE_PRIVATE);
            Map<String, ?> userInfo = sharedPreferences.getAll();

            if (userInfo.containsKey("isSignedUp") && Objects.equals(userInfo.get("isSignedUp"), true)) {
                Intent homeIntent = new Intent(MainActivity.this, EventsHomeActivity.class);
                startActivity(homeIntent);
            } else {
                System.out.println("We made it here!");
                // User is not signed up so
                Intent signUpIntent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
            }
            finish(); // Closing MainActivity to prevent going back to it.
        },2000);
    }
}
