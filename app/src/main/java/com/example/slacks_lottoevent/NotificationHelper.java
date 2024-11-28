package com.example.slacks_lottoevent;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;



public class NotificationHelper {

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1;


    private Activity activity;

    public NotificationHelper(Activity activity) {
        this.activity = activity;
    }

    public void sendNotifications(String deviceId, String title, String message) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("SlacksLottoEventUserInfo", MODE_PRIVATE);
        Boolean hasAsked = sharedPreferences.getBoolean("hasAskedNotifcations", false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if the POST_NOTIFICATIONS permission is granted
            if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED && (!hasAsked)) {
                // Request the permission
                ActivityCompat.requestPermissions(activity,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        100); // 100 is the request code (can be any number)
                // Return early to avoid sending the notification before permission is granted
                return;
            }
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, activity.getString(R.string.channel_id))
                .setSmallIcon(R.drawable.baseline_notifications_active_24) // Ensure the icon exists
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(activity);

        notificationManager.notify((deviceId + System.currentTimeMillis()).hashCode(), builder.build());
    }

}

