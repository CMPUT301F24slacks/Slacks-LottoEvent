package com.example.slacks_lottoevent;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {

    private Activity activity;

    public NotificationHelper(Activity activity) {
        this.activity = activity;
    }

    public void sendNotifications(String deviceId, String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, activity.getString(R.string.channel_id))
                .setSmallIcon(R.drawable.baseline_notifications_active_24) // Ensure the icon exists
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(activity);

        notificationManager.notify((deviceId + System.currentTimeMillis()).hashCode(), builder.build());
    }

}

