package com.example.slacks_lottoevent;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.slacks_lottoevent.databinding.NotificationsUserBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UserNotifications extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference entrantRef;
    private CollectionReference organizersRef;
    private CollectionReference facilitiesRef;
    private ArrayList<UserEventNotifications> eventList;
    private EventNotificationsArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications_user);

        // Initialize Firestore and Collections
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        entrantRef = db.collection("entrants");
        organizersRef = db.collection("organizers");
        facilitiesRef = db.collection("facilities");
        eventList = new ArrayList<>();

        // Set up the adapter
        adapter = new EventNotificationsArrayAdapter(this, eventList);
        ListView listView = findViewById(R.id.listViewUserInvitations);
        listView.setAdapter(adapter);

        // Fetch and populate invited events
        fetchFacilityIdAndInvitedEvents();
    }

    /**
     * Fetch the facility ID associated with the current user, then fetch invited events.
     */
    private void fetchFacilityIdAndInvitedEvents() {
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//        Toast.makeText(this, "Device ID: " + deviceId, Toast.LENGTH_LONG).show();

        organizersRef.document(deviceId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot organizerDoc = task.getResult();

                if (organizerDoc.exists() && organizerDoc.contains("facilityId")) {
                    String facilityId = organizerDoc.getString("facilityId");
                    fetchFacilityLocationAndInvitedEvents(deviceId, facilityId);
                } else {
                    Log.d("Firestore", "No facilityID found for this device ID.");
                }
            } else {
                Log.e("Firestore", "Error fetching organizer data: ", task.getException());
            }
        });
    }

    /**
     * Fetch the location (city and country) of the facility and invited events.
     */
    private void fetchFacilityLocationAndInvitedEvents(String deviceId, String facilityId) {
        facilitiesRef.document(facilityId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot facilityDoc = task.getResult();

                if (facilityDoc.exists() && facilityDoc.contains("city") && facilityDoc.contains("country")) {
                    String city = facilityDoc.getString("city");
                    String country = facilityDoc.getString("country");
                    String location = city + ", " + country;

                    fetchInvitedEvents(deviceId, location);
                } else {
                    Log.d("Firestore", "No location data found for facility ID: " + facilityId);
                }
            } else {
                Log.e("Firestore", "Error fetching facility data: ", task.getException());
            }
        });
    }

    /**
     * Fetch the invited events for the current user and facility location.
     */
    private void fetchInvitedEvents(String deviceId, String location) {
        entrantRef.document(deviceId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot entrantDoc = task.getResult();

                if (entrantDoc.exists() && entrantDoc.contains("invitedEvents")) {
                    List<String> eventIds = (List<String>) entrantDoc.get("invitedEvents");

                    if (eventIds != null && !eventIds.isEmpty()) {
                        fetchEventDetails(eventIds, location);
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
     */
    private void fetchEventDetails(List<String> eventIds, String location) {
        for (String eventId : eventIds) {
            if (eventId == null || eventId.isEmpty()) {
                Log.e("Firestore", "Invalid eventId: " + eventId);
                continue; // Skip this iteration if eventId is invalid
            }

            eventsRef.document(eventId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot eventDoc = task.getResult();

                    if (eventDoc.exists()) {
                        // Create Event object from Firestore data
                        String name = eventDoc.getString("name");
                        String date = eventDoc.getString("date");
                        String time = eventDoc.getString("time");

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


//        take from the entrant.invited array, then for loop through that and make the object of accepting/declining
//        with fields using getters on that specific id of that event already in the firestore, then
//        make if its accepted, we add that to our finalist events in our entrant class, and if declined
//        we remove ourselves from the entrant.invited array whilst removing ourselves from the events.invited
//        array, and then updating the count (if there is one) of how many people are invited.

//        for the organizer lists of cancelled joined and etc, take it from the event associated when clicked using intent
//        then replace that dummy data with the real one, also fix leylas implementation



