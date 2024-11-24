package com.example.slacks_lottoevent;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.concurrent.atomic.AtomicBoolean;
public class NotificationHelper {
    private final Context context;
    public NotificationHelper(Context context) {
        this.context = context;
    }

    public void sendNotifications(AtomicBoolean notis, String eventName){
        if(notis.get()) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.channel_id))
                    .setSmallIcon(R.drawable.baseline_notifications_active_24) // Replace with your icon
                    .setContentTitle("Event Registration: " + eventName)
                    .setContentText("You have successfully registered for the event waitlist!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            // Check for notification permissions (required for Android 13+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }

            notificationManager.notify(1, builder.build()); // Send the notification
        }
    }



}
