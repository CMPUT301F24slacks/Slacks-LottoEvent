package com.example.slacks_lottoevent;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.concurrent.atomic.AtomicBoolean;
public class NotificationHelper {
    private final Context context;
    public NotificationHelper(Context context) {
        this.context = context;
    }

    public void sendNotificationsW(AtomicBoolean notis, String eventName){
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
            Log.d("NotificationSent", "YES");
        }
    }

    public void sendNotificationsWC(){}

    public void sendNotificationsSel(String eventName, Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.channel_id))
                .setSmallIcon(R.drawable.baseline_notifications_active_24) // Replace with your icon
                .setContentTitle("Update for : " + eventName)
                .setContentText("You have been selected for the event! Please check your invites to accept or decline the invitation ASAP!")
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


    public void sendNotificationsCanc(String eventName, Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.channel_id))
                .setSmallIcon(R.drawable.baseline_notifications_active_24) // Replace with your icon
                .setContentTitle("Updated for: " + eventName)
                .setContentText("Unfortunately you have not been selected for the event.")
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

    public void sendNotificationsJoined(String eventName, Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.channel_id))
                .setSmallIcon(R.drawable.baseline_notifications_active_24) // Replace with your icon
                .setContentTitle("Confirmation for: " + eventName)
                .setContentText("Yay!! You have successfully joined this event! All the work is done so not just kickback and relax until the event.")
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
