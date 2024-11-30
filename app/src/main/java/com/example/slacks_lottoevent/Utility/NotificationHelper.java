package com.example.slacks_lottoevent.Utility;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.slacks_lottoevent.R;

/**
 * Helper class for managing and sending notifications in the application.
 * This class handles notification permissions, building notification content,
 * and displaying notifications to the user.
 */
public class NotificationHelper {

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1;

    private final Activity activity;

    /**
     * Constructs a new instance of {@link NotificationHelper}.
     *
     * @param activity The activity context used for permission checks and notification management.
     */
    public NotificationHelper(Activity activity) {
        this.activity = activity;
    }

    /**
     * Sends a notification with the specified title and message to the user.
     * Handles permission requests
     *
     * @param deviceId A unique identifier for the device, used to generate unique notification IDs.
     * @param title    The title of the notification.
     * @param message  The message content of the notification.
     */
    public void sendNotifications(String deviceId, String title, String message) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences( "SlacksLottoEventUserInfo", MODE_PRIVATE);
        Boolean hasAsked = sharedPreferences.getBoolean("hasAskedNotifcations", false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if the POST_NOTIFICATIONS permission is granted
            if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED && (!hasAsked)) {
                // Request the permission
                ActivityCompat.requestPermissions(activity, new String[]{ android.Manifest.permission.POST_NOTIFICATIONS}, 100); // 100 is the request code (can be any number)
                // Return early to avoid sending the notification before permission is granted
                return;
            }
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity,
                                                                            activity.getString(
                                                                                    R.string.channel_id))
                .setSmallIcon(R.drawable.baseline_notifications_active_24) // Ensure the icon exists
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(activity);

        notificationManager.notify((deviceId + System.currentTimeMillis()).hashCode(),
                                   builder.build());
    }

}

