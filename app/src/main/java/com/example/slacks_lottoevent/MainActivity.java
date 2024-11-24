package com.example.slacks_lottoevent;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;


//testing purposes
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.libraries.places.api.Places;
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

    private NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        createNotificationChannel();


        SharedPreferences sharedPreferences = getSharedPreferences("SlacksLottoEventUserInfo", MODE_PRIVATE);


        if (!sharedPreferences.contains("isSignedUp")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isSignedUp", false);
            editor.apply();
        }


        new Handler().postDelayed(() ->{
            Map<String, ?> userInfo = sharedPreferences.getAll();

            Intent homeIntent = new Intent(MainActivity.this, EventsHomeActivity.class);
            startActivity(homeIntent);

            finish(); // Closing MainActivity to prevent going back to it.
        },2000);
        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(),com.example.slacks_lottoevent.BuildConfig.MAPS_API_KEY);

        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Use the appropriate context method
            String channelId = getString(R.string.channel_id); // ensure channel ID is properly defined in strings.xml
            CharSequence name = getString(R.string.channel_name); // name of the channel
            String description = getString(R.string.channel_description); // description of the channel
            int importance = NotificationManager.IMPORTANCE_DEFAULT; // adjust as necessary

            // Create the NotificationChannel
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

}