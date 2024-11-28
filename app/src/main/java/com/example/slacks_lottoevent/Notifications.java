package com.example.slacks_lottoevent;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
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

    public void addNotifications(String customName, String customMessage, String listToGrab, String eventId) {
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
                                // Build a notification for each user on specific list and add too the notification collection using the userID/deviceIds

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


    public void removeNotifications(String deviceId){
        db.collection("notifications")
                .whereEqualTo("userId", deviceId)  // Match documents where userId is equal to deviceId
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Iterate through each matching document
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        // Document reference to be deleted
                        document.getReference().delete()
                                .addOnSuccessListener(aVoid -> {
                                    // Successfully deleted the document
                                    Log.d("Firestore", "Notification document deleted successfully.");
                                })
                                .addOnFailureListener(e -> {
                                    // Error occurred while deleting the document
                                    Log.e("Firestore", "Error deleting document", e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching notifications", e);
                });
    }
}
