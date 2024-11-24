package com.example.slacks_lottoevent;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.slacks_lottoevent.databinding.NotificationsUserBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * UserNotifications is an activity that displays the user's event notifications.
 * It fetches the user's facility ID, location, and invited events from Firestore.
 */
public class UserNotifications extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference entrantRef;
//    private CollectionReference organizersRef;
//    private CollectionReference facilitiesRef;
    private ArrayList<UserEventNotifications> eventList;
    private EventNotificationsArrayAdapter adapter;

    SharedPreferences sharedPreferences;

    /**
     * Initialize Firestore and Collections, and set up the adapter.
     * Fetch and populate invited events.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications_user);
        sharedPreferences = getSharedPreferences("SlacksLottoEventUserInfo", MODE_PRIVATE);

        // Initialize Firestore and Collections
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        entrantRef = db.collection("entrants");
//        organizersRef = db.collection("organizers");
//        facilitiesRef = db.collection("facilities");
        eventList = new ArrayList<>();

        // Set up the adapter
        adapter = new EventNotificationsArrayAdapter(this, eventList);
        ListView listView = findViewById(R.id.listViewUserInvitations);
        listView.setAdapter(adapter);

        // Fetch and populate invited events
        fetchInvitedEvents();

        Button back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(v -> onBackPressed());

        ImageView organizerNotis = findViewById(R.id.organizerNotifications);
        boolean notisEnabled = sharedPreferences.getBoolean("notisEnabled", false);

        organizerNotis.setImageResource(notisEnabled ? R.drawable.baseline_notifications_active_24 : R.drawable.baseline_circle_notifications_24);

        organizerNotis.setOnClickListener(v -> {
            boolean negation = !notisEnabled;

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notificationsEnabled", negation);
            editor.apply();
            organizerNotis.setImageResource(negation ? R.drawable.baseline_notifications_active_24 : R.drawable.baseline_circle_notifications_24);

            if(!notisEnabled){
                removeNotifications();
            }

            else{
                wantNotifications();

            }
        });
    }

    /**
     * Fetch the invited events for the current user and facility location.
     */
    private void fetchInvitedEvents() {
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        entrantRef.document(deviceId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot entrantDoc = task.getResult();

                if (entrantDoc.exists() && entrantDoc.contains("invitedEvents")) {
                    List<String> eventIds = (List<String>) entrantDoc.get("invitedEvents");

                    if (eventIds != null && !eventIds.isEmpty()) {
                        fetchEventDetails(eventIds);
                    } else {
                        Log.d("Firestore", "No invited events found.");
                    }
                } else {
                    Log.d("Firestore", "No entrant data found for this device ID.");
                }
            } else {
                Log.e("Firestore", "Error fetching entrant data: ", task.getException());
            }
        });
    }

    /**
     * Fetch the event details from the events collection.
     * @param eventIds the list of event IDs to fetch details for
     */
    private void fetchEventDetails(List<String> eventIds) {
        for (String eventId : eventIds) {
            if (eventId == null || eventId.isEmpty()) {
                Log.e("Firestore", "Invalid eventId: " + eventId);
                continue; // Skip this iteration if eventId is invalid
            }

            eventsRef.document(eventId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot eventDoc = task.getResult();

                    if (eventDoc.exists()) {
                        String name = eventDoc.getString("name");
                        String date = eventDoc.getString("eventDate");
                        String time = eventDoc.getString("time");
                        String location = eventDoc.getString("location");

                        UserEventNotifications event = new UserEventNotifications(name, date, time, location, eventId);
                        eventList.add(event);

                        // Notify adapter of data changes
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d("Firestore", "Event document does not exist for ID: " + eventId);
                    }
                } else {
                    Log.e("Firestore", "Error fetching event data for ID: " + eventId, task.getException());
                }
            });
        }
    }

    /**
     * If user opts out of notifications, then they are removed.
     */
    private void removeNotifications() {
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        eventsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Get the lists from the document
                    List<String> cancelledNotificationsList = (List<String>) document.get("cancelledNotificationsList");
                    List<String> joinedNotificationsList = (List<String>) document.get("joinedNotificationsList");
                    List<String> selectedNotificationsList = (List<String>) document.get("selectedNotificationsList");
                    List<String> waitlistedNotificationsList = (List<String>) document.get("waitlistedNotificationsList");

                    boolean isUpdated = false;

                    // Remove the notification ID from each list if it exists
                    if (cancelledNotificationsList != null && cancelledNotificationsList.contains(deviceId)) {
                        cancelledNotificationsList.remove(deviceId);
                        isUpdated = true;
                    }
                    if (joinedNotificationsList != null && joinedNotificationsList.contains(deviceId)) {
                        joinedNotificationsList.remove(deviceId);
                        isUpdated = true;
                    }
                    if (selectedNotificationsList != null && selectedNotificationsList.contains(deviceId)) {
                        selectedNotificationsList.remove(deviceId);
                        isUpdated = true;
                    }
                    if (waitlistedNotificationsList != null && waitlistedNotificationsList.contains(deviceId)) {
                        waitlistedNotificationsList.remove(deviceId);
                        isUpdated = true;
                    }

                    // If any list was updated, update the document in Firestore
                    if (isUpdated) {
                        eventsRef.document(document.getId())
                                .update("cancelledNotificationsList", cancelledNotificationsList,
                                        "joinedNotificationsList", joinedNotificationsList,
                                        "selectedNotificationsList", selectedNotificationsList,
                                        "waitlistedNotificationsList", waitlistedNotificationsList)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Notification Removal", "Notification removed from event " + document.getId());
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Notification Removal", "Failed to remove notification from event " + document.getId(), e);
                                });
                    }
                }
            }
        });
    }

    private void wantNotifications(){
        List<String> eventList = new ArrayList<>();  // List to store event IDs
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        entrantRef.document(deviceId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot entrantDoc = task.getResult();

                if (entrantDoc.exists()) {
                    // Get the event lists from Firestore
                    List<String> invitedEvents = (List<String>) entrantDoc.get("invitedEvents");
                    List<String> uninvitedEvents = (List<String>) entrantDoc.get("uninvitedEvents");
                    List<String> finalistEvents = (List<String>) entrantDoc.get("finalistEvents");
                    List<String> waitlistedEvents = (List<String>) entrantDoc.get("waitlistedEvents");

                    // Add all events to the eventList
                    eventList.addAll(invitedEvents);
                    eventList.addAll(uninvitedEvents);
                    eventList.addAll(finalistEvents);
                    eventList.addAll(waitlistedEvents);

                    // Now that the eventList is populated, perform further operations
                    if (eventList != null && !eventList.isEmpty()) {
                        for (String event : eventList) {
                            eventsRef.document(event).get().addOnCompleteListener(eventTask -> {
                                if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                                    DocumentSnapshot eventDoc = eventTask.getResult();

                                    if (eventDoc.exists()) {
                                        // Check which list the event belongs to and handle accordingly
                                        if (finalistEvents.contains(event)) {
                                            // If it's a finalist event, add to the finalist notifications list
                                            // Example: Add to the finalist notifications list
                                        } else if (uninvitedEvents.contains(event)) {
                                            // If it's an uninvited event, add to the cancelled notifications list
                                        } else if (invitedEvents.contains(event)) {
                                            // If it's an invited event, add to selected, finalist, and uninvited
                                        } else if (waitlistedEvents.contains(event)) {
                                            // If it's a waitlisted event, add to all four notification lists
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });

    }
}





