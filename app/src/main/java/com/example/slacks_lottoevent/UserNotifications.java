package com.example.slacks_lottoevent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.example.slacks_lottoevent.viewmodel.adapter.EventNotificationsArrayAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * UserNotifications is an activity that displays the user's event notifications.
 * It fetches the user's facility ID, location, and invited events from Firestore.
 */
public class UserNotifications extends AppCompatActivity {
    ImageView organizerNotis;
    SharedPreferences sharedPreferences;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference entrantRef;
    //    private CollectionReference organizersRef;
//    private CollectionReference facilitiesRef;
    private ArrayList<UserEventNotifications> eventList;
    private EventNotificationsArrayAdapter adapter;

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
        adapter = new EventNotificationsArrayAdapter(this, eventList, getSharedPreferences(
                "SlacksLottoEventUserInfo", MODE_PRIVATE));
        ListView listView = findViewById(R.id.listViewUserInvitations);
        listView.setAdapter(adapter);

        // Fetch and populate invited events
        fetchInvitedEvents("invitedEvents", true);
        fetchInvitedEvents("uninvitedEvents", false);

        Button back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(v -> onBackPressed());

        organizerNotis = findViewById(R.id.organizerNotifications);
        boolean notisEnabled = sharedPreferences.getBoolean("notificationsEnabled", false);

        organizerNotis.setImageResource(
                notisEnabled ? R.drawable.baseline_notifications_active_24 : R.drawable.baseline_circle_notifications_24);

        organizerNotis.setOnClickListener(v -> {
            boolean negation = !notisEnabled;

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notificationsEnabled", negation);
            editor.apply();
            organizerNotis.setImageResource(
                    negation ? R.drawable.baseline_notifications_active_24 : R.drawable.baseline_circle_notifications_24);

            if (notisEnabled) {
                Toast.makeText(this,
                               "Notifications are disabled. To fully disable, revoke the permission in app settings.",
                               Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,
                               "Notifications are enabled. To fully disable, revoke the permission in app settings.",
                               Toast.LENGTH_LONG).show();
            }

            // Redirect to the app's notification settings
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            startActivityForResult(intent, 100);  // Start activity for result to return back to app

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            // Check notification status
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            boolean areNotificationsEnabled = notificationManager.areNotificationsEnabled();

            // Update shared preferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notificationsEnabled", areNotificationsEnabled);
            editor.apply();

            // Update the notification bell icon
            organizerNotis.setImageResource(areNotificationsEnabled ?
                                                    R.drawable.baseline_notifications_active_24 :
                                                    R.drawable.baseline_circle_notifications_24);

            // Notify user of the current state
            Toast.makeText(this, areNotificationsEnabled ?
                                   "Notifications are enabled." :
                                   "Notifications are disabled.",
                           Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Fetch the invited events for the current user and facility location.
     */
    private void fetchInvitedEvents(String eventTypes, Boolean selected) {
        String deviceId = Settings.Secure.getString(getContentResolver(),
                                                    Settings.Secure.ANDROID_ID);
        entrantRef.document(deviceId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot entrantDoc = task.getResult();

                if (entrantDoc.exists() && entrantDoc.contains(eventTypes)) {
                    List<String> eventIds = (List<String>) entrantDoc.get(eventTypes);

                    if (eventIds != null && !eventIds.isEmpty()) {
                        fetchEventDetails(eventIds, selected);
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
     *
     * @param eventIds the list of event IDs to fetch details for
     */
    private void fetchEventDetails(List<String> eventIds, Boolean invited) {
        for (String eventId : eventIds) {
            if (eventId == null || eventId.isEmpty()) {
                Log.e("Firestore", "Invalid eventId: " + eventId);
                continue; // Skip this iteration if eventId is invalid
            }

            // Check if the event has already been displayed
            if (isEventDisplayed(eventId)) {
                continue; // Skip adding this event if it was previously displayed
            }

            eventsRef.document(eventId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot eventDoc = task.getResult();

                    if (eventDoc.exists() && invited) {
                        String name = eventDoc.getString("name");
                        String date = eventDoc.getString("eventDate");
                        String time = eventDoc.getString("time");
                        String location = eventDoc.getString("location");

                        UserEventNotifications event = new UserEventNotifications(
                                name + ": Selected", date, time, location, eventId, true);
                        eventList.add(event);

//                        saveEventAsDisplayed(eventId);

                        // Notify adapter of data changes
                        adapter.notifyDataSetChanged();
                    } else if (eventDoc.exists() && !invited) {
                        String name = eventDoc.getString("name");
                        String date = eventDoc.getString("eventDate");
                        String time = eventDoc.getString("time");
                        String location = eventDoc.getString("location");

                        UserEventNotifications event = new UserEventNotifications(
                                name + ": Unselected", date, time, location, eventId, false);
                        eventList.add(event);

//                        saveEventAsDisplayed(eventId);

                        // Notify adapter of data changes
                        adapter.notifyDataSetChanged();

                    } else {
                        Log.d("Firestore", "Event document does not exist for ID: " + eventId);
                    }
                } else {
                    Log.e("Firestore", "Error fetching event data for ID: " + eventId,
                          task.getException());
                }
            });
        }
    }

    /**
     * Check if the event has already been displayed using SharedPreferences.
     *
     * @param eventId The ID of the event to check.
     * @return True if the event was already displayed, false otherwise.
     */
    private boolean isEventDisplayed(String eventId) {
        return sharedPreferences.getBoolean(eventId, false); // Default to false if not found
    }
}





