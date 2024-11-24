package com.example.slacks_lottoevent;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {

    private final Context context;

    public NotificationHelper(Context context) {
        this.context = context;
    }

    // Reusable notification sender
    private void sendNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.channel_id))
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public void sendNotificationsW(String eventName) {
        sendNotification("Event Registration: " + eventName,
                "You have successfully registered for the event waitlist!");
    }

    public void sendNotificationsSel(String eventName) {
        sendNotification("Update for: " + eventName,
                "You have been selected for the event! Please check your invites to accept or decline the invitation ASAP!");
    }

    public void sendNotificationsCanc(String eventName) {
        sendNotification("Updated for: " + eventName,
                "Unfortunately, you have not been selected for the event.");
    }

    public void sendNotificationsJoined(String eventName) {
        sendNotification("Confirmation for: " + eventName,
                "Yay!! You have successfully joined this event! All the work is done so now just kick back and relax until the event.");
    }

    public void sendNotificationscraftW(String eventTitle, String desc){
        sendNotification(eventTitle, desc);
    }

}

