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
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.slacks_lottoevent.viewmodel.adapter.EventNotificationsArrayAdapter;
import com.example.slacks_lottoevent.R;
import com.example.slacks_lottoevent.UserEventNotifications;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
/**
 * A fragment that displays event invitations  and other notifications for the user.
 * The user can see both selected and unselected event notifications/invitations and manage notification settings.
 * */
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
    /**
     * Inflates the layout for the fragment
     * @param inflater The LayoutInflanter object used to inflate views in the fragment.
     * @param container parent view that the fragment's UI should be attached to.
     * @param savedInstanceState fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     * */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_invites, container, false);
    }
    /**
     * Sets up UI elements, initializes Firestore collections, and fetches user invitations.
     * @param view The View returned by onCreateView.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */
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
            // Redirect to the app's notification settings
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireActivity().getPackageName());
            startActivityForResult(intent, 100);

        });


    }
    /**
     * Handles the result from the notification settings activity.
     * Updates the notification icon and shared preferences based on the current status.
     * @param requestCode The request code used when starting the activity.
     * @param resultCode  The result code returned by the activity.
     * @param data Any additional data returned by the activity.
     */
    @Override
    @SuppressWarnings("deprecation")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            // Check notification status
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
            boolean areNotificationsEnabled = notificationManager.areNotificationsEnabled();
            Log.d("NotisEnables", String.valueOf(areNotificationsEnabled));

            // Update shared preferences with the correct notification status
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notificationsEnabled", areNotificationsEnabled);
            editor.apply();

            // Update the notification bell icon based on the new status
            organizerNotis.setImageResource(areNotificationsEnabled ?
                    R.drawable.baseline_notifications_active_24 :
                    R.drawable.baseline_circle_notifications_24);

            // Notify user of the current state
            Toast.makeText(requireContext(), areNotificationsEnabled ?
                            "Notifications are enabled." :
                            "Notifications are disabled.",
                    Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Fetches event invitations for the user based on the event type (invited or uninvited).
     * @param eventTypes The key representing the event type in the entrant's document.
     * @param selected True if fetching invited events, false for uninvited events.
     */
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
    /**
     * Fetches event details from Firestore for a list of event IDs.
     * Populates the ListView with event information.
     * @param eventIds A list of event IDs to fetch details for.
     * @param invited  True if the events are invited, false if uninvited.
     */
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
    /**
     * Checks if an event with the given ID is already displayed in the list.
     * @param eventId The ID of the event to check.
     * @return True if the event is already displayed, false otherwise.
     */
    private boolean isEventDisplayed(String eventId) {
        return sharedPreferences.getBoolean(eventId, false);
    }
}
