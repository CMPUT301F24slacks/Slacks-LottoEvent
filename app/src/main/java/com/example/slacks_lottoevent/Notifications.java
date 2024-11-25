package com.example.slacks_lottoevent;

import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Notifications {
    private String title;
    private String content;
    private String userId;
    private String eventId;

    private FirebaseFirestore db;

    public Notifications(){
        db = FirebaseFirestore.getInstance();
    }

    public void sendNotifications(String customName, String customMessage, String listToGrab, String eventId) {
        db.collection("events").document(eventId).get().addOnSuccessListener(eventDoc -> {
            if (eventDoc.exists()) {
//                Grabbing people who want these notifications
                ArrayList<String> deviceIds = (ArrayList<String>) eventDoc.get(listToGrab);

                if (deviceIds != null && !deviceIds.isEmpty()) {
                    // Send individual notifications to each user in the waitlist
                    for (int i = 0; i < deviceIds.size(); i++) {
                        String deviceId = deviceIds.get(i);

                        db.collection("profiles").document(deviceId).get().addOnSuccessListener(profileDoc -> {
                            if (profileDoc.exists()) {
                                // Build a notification for each waitlisted user and add too the notification collection using the userID/deviceIds

                                Map<String, Object> notificationData = new HashMap<>();
                                notificationData.put("userId", deviceId);
                                notificationData.put("eventId", eventId);
                                notificationData.put("title", customName);
                                notificationData.put("message", customMessage);

                                db.collection("notifications").add(notificationData);
                            }
                        });
                    }
                }
            }
        });
    }
}
