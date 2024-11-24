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
    private void sendNotification(String deviceId, String title, String message) {
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


        notificationManager.notify(deviceId.hashCode(), builder.build());

        Log.d("Notis", "Notifications send");
    }

    public void sendNotificationsW(String deviceId, String eventName) {
        sendNotification(deviceId,"Event Registration: " + eventName,
                "You have successfully registered for the event waitlist!");
    }

//    public void sendNotificationsSel(String eventName) {
//        sendNotification("Update for: " + eventName,
//                "You have been selected for the event! Please check your invites to accept or decline the invitation ASAP!");
//    }
//
//    public void sendNotificationsCanc(String eventName) {
//        sendNotification("Updated for: " + eventName,
//                "Unfortunately, you have not been selected for the event.");
//    }
//
//    public void sendNotificationsJoined(String eventName) {
//        sendNotification("Confirmation for: " + eventName,
//                "Yay!! You have successfully joined this event! All the work is done so now just kick back and relax until the event.");
//    }
//
    public void sendNotificationscraftW(String deviceId, String eventTitle, String desc){
        sendNotification(deviceId, eventTitle, desc);
    }

}

