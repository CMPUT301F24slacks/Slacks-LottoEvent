package com.example.slacks_lottoevent.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.slacks_lottoevent.EventNotificationsArrayAdapter;
import com.example.slacks_lottoevent.R;
import com.example.slacks_lottoevent.UserEventNotifications;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class InboxFragment extends Fragment {

    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference entrantRef;
    private ArrayList<UserEventNotifications> eventList;
    private EventNotificationsArrayAdapter adapter;

    private ImageView organizerNotis;
    private SharedPreferences sharedPreferences;

    public InboxFragment() {
        // Required empty public constructor
    }

    public static InboxFragment newInstance() {
        return new InboxFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_invites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity();
        sharedPreferences = requireActivity().getSharedPreferences("SlacksLottoEventUserInfo",
                                                                   Context.MODE_PRIVATE);

        // Initialize Firestore and Collections
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        entrantRef = db.collection("entrants");
        eventList = new ArrayList<>();

        // Set up the adapter
        adapter = new EventNotificationsArrayAdapter(requireContext(), eventList,
                                                     sharedPreferences);
        ListView listView = view.findViewById(R.id.listViewUserInvitations);
        listView.setAdapter(adapter);

        // Fetch and populate invited events
        fetchInvitedEvents("invitedEvents", true);
        fetchInvitedEvents("uninvitedEvents", false);

        organizerNotis = view.findViewById(R.id.organizerNotifications);
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

            Toast.makeText(requireContext(), negation
                                   ? "Notifications are enabled. To fully disable, revoke the permission in app settings."
                                   : "Notifications are disabled. To fully enable, allow permissions in app settings.",
                           Toast.LENGTH_LONG).show();

            // Redirect to the app's notification settings
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireActivity().getPackageName());
            startActivityForResult(intent, 100);
        });
    }

    private void fetchInvitedEvents(String eventTypes, Boolean selected) {
        String deviceId = Settings.Secure.getString(requireActivity().getContentResolver(),
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

    private void fetchEventDetails(List<String> eventIds, Boolean invited) {
        for (String eventId : eventIds) {
            if (eventId == null || eventId.isEmpty()) {
                Log.e("Firestore", "Invalid eventId: " + eventId);
                continue;
            }

            if (isEventDisplayed(eventId)) {
                continue;
            }

            eventsRef.document(eventId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot eventDoc = task.getResult();

                    if (eventDoc.exists()) {
                        String name = eventDoc.getString("name");
                        String date = eventDoc.getString("eventDate");
                        String time = eventDoc.getString("time");
                        String location = eventDoc.getString("location");

                        UserEventNotifications event = new UserEventNotifications(
                                name + (invited ? ": Selected" : ": Unselected"), date, time,
                                location, eventId, invited);
                        eventList.add(event);

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

    private boolean isEventDisplayed(String eventId) {
        return sharedPreferences.getBoolean(eventId, false);
    }
}
