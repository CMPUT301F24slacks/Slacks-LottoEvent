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

    // Reusable notification sender
    public void sendNotifications(String deviceId, String title, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.POST_NOTIFICATIONS},1);
                return; // Return early if permission isn't granted
            }
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, activity.getString(R.string.channel_id))
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(activity);


        notificationManager.notify((deviceId + System.currentTimeMillis()).hashCode(), builder.build());

        Log.d("Notis", "Notifications send");
    }

}

