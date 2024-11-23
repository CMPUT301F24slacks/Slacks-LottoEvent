package com.example.slacks_lottoevent;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.slacks_lottoevent.databinding.NotificationsUserBinding;
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

}




