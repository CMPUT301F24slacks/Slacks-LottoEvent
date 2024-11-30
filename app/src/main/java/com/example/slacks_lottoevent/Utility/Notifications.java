package com.example.slacks_lottoevent.Utility;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles notification-related operations, including adding notifications
 * to the Firestore database and removing notifications associated with specific users.
 * <p>
 * This class interacts with the following Firestore collections:
 * - "events" for retrieving lists of device IDs associated with an event.
 * - "profiles" for fetching user profile details based on device IDs.
 * - "notifications" for storing and managing user-specific notifications.
 */
public class Notifications {
    ArrayList<String> deviceIds;

    private final FirebaseFirestore db;

    /**
     * Constructs a new instance of {@link Notifications}.
     * Initializes a Firestore database instance for performing database operations.
     */

    public Notifications() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Adds notifications to the "notifications" Firestore collection for all users
     * listed in a specific group (e.g., waitlist) of an event.
     *
     * @param customName    The title of the notification.
     * @param customMessage The message content of the notification.
     * @param listToGrab    The key in the event document representing the list of device IDs.
     * @param eventId       The unique identifier of the event to retrieve the list from.
     * @param allLists      The flag to determine if we send notifications too all people or just specific.
     */
    public void addNotifications(String customName, String customMessage, String listToGrab,
                                 String eventId, Boolean allLists) {
        db.collection("events").document(eventId).get().addOnSuccessListener(eventDoc -> {
            if (eventDoc.exists()) {
                ArrayList<String> deviceIds = null;
//                Grabbing people who want these notifications
                if (allLists) {
                    deviceIds.addAll((ArrayList<String>) eventDoc.get("waitlistedNotificationsList"));
                    deviceIds.addAll((ArrayList<String>) eventDoc.get("selectedNotificationsList"));
                    deviceIds.addAll((ArrayList<String>) eventDoc.get("joinedNotificationsList"));
                    deviceIds.addAll((ArrayList<String>) eventDoc.get("cancelledNotificationsList"));
                }

                else{
                    deviceIds = (ArrayList<String>) eventDoc.get(listToGrab);
                }

                if (deviceIds != null && !deviceIds.isEmpty()) {
                    // Send individual notifications to each user in the waitlist
                    for (int i = 0; i < deviceIds.size(); i++) {
                        String deviceId = deviceIds.get(i);

                        db.collection("profiles").document(deviceId).get()
                          .addOnSuccessListener(profileDoc -> {
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

    /**
     * Removes all notifications associated with a specific user ID from the "notifications" Firestore collection.
     *
     * @param deviceId The unique identifier of the user or device whose notifications should be deleted.
     */

    public void removeNotifications(String deviceId) {
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
