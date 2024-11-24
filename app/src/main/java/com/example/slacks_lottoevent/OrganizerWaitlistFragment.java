package com.example.slacks_lottoevent;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganizerWaitlistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganizerWaitlistFragment extends Fragment {

    private ListView ListViewEntrantsWaitlisted;
    private String eventId;
    private static final String ARG_EVENT_ID = "eventID";
    private FirebaseFirestore db;
    private ArrayList<Profile> profileList;

    /**
     * Default constructor
     */
    public OrganizerWaitlistFragment() {}

    /**
     * Factory method to create a new instance of this fragment using the provided parameters.
     * @param eventId The current event's ID.
     * @return A new instance of OrganizerWaitlistFragment.
     */
    public static OrganizerWaitlistFragment newInstance(String eventId) {
        OrganizerWaitlistFragment fragment = new OrganizerWaitlistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId); // Pass the event ID as a String
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        profileList = new ArrayList<>(); // Initialize the profile list
        // Retrieve the event ID from the fragment's arguments
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_organizer_waitlist, container, false);
        View dialogView = inflater.inflate(R.layout.dialog_custom_message, null);

        ListViewEntrantsWaitlisted = view.findViewById(R.id.listViewEntrantsWaitlisted);
        Button craftMessageButton = view.findViewById(R.id.craftMessage);

        ProfileListArrayAdapter adapter = new ProfileListArrayAdapter(getContext(), profileList, false);
        ListViewEntrantsWaitlisted.setAdapter(adapter);

        NotificationHelper notificationHelper = new NotificationHelper(getActivity()); // Initialize NotificationHelper

        craftMessageButton.setOnClickListener(v -> showMessageDialog()); // Button to show the dialog


        // Listen for real-time updates to the event document
        db.collection("events").document(eventId).addSnapshotListener((eventDoc, error) -> {
            if (error != null) {
                Log.e("Firestore", "Error listening to event document updates", error);
                return;
            }

            if (eventDoc != null && eventDoc.exists()) {
                ArrayList<String> deviceIds = (ArrayList<String>) eventDoc.get("waitlisted");

                if (deviceIds != null && !deviceIds.isEmpty()) {
                    profileList.clear(); // Clear the list before adding new data

                    // Listen for real-time updates to each profile document
                    for (String deviceId : deviceIds) {
                        db.collection("profiles").document(deviceId).addSnapshotListener((profileDoc, profileError) -> {
                            if (profileError != null) {
                                Log.e("Firestore", "Error listening to profile document updates", profileError);
                                return;
                            }

                            if (profileDoc != null && profileDoc.exists()) {
                                Profile profile = profileDoc.toObject(Profile.class);
                                profileList.add(profile); // Add the name if it’s not already in the list
                                adapter.notifyDataSetChanged(); // Update the adapter
                            }
                            else {
                                Log.d("Firestore", "Profile document does not exist for device ID: " + deviceId);
                            }
                        });
                    }

//                    notificationHelper.sendNotificationsW(1"Waitlist Updated");

                } else {
                    Log.d("Firestore", "No device IDs found in the waitlisted list.");
                    profileList.clear();
                    adapter.notifyDataSetChanged(); // Clear the ListView if no device IDs are found
                }
            } else {
                Log.d("Firestore", "Event document does not exist for ID: " + eventId);
                profileList.clear();
                adapter.notifyDataSetChanged(); // Clear the ListView if the event document doesn't exist
            }
        });

        return view;
    }

    // Show the dialog
    private void showMessageDialog() {
        // Inflate the custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_custom_message, null);

        // Get references to the input fields from the dialog view
        EditText inputTitle = dialogView.findViewById(R.id.dialogMessageTitle);  // Assuming inputTitle exists in the dialog layout
        EditText inputMessage = dialogView.findViewById(R.id.dialogMessageInput); // Assuming inputMessage exists in the dialog layout

        // Create and show the dialog
        new AlertDialog.Builder(getContext())
                .setTitle("Craft Message")
                .setView(dialogView)  // Set the custom dialog layout here
                .setPositiveButton("Send", (dialog, which) -> {
                    // Get the input values
                    String message = inputMessage.getText().toString();
                    String title = inputTitle.getText().toString();

                    // Check if the message and title are not empty
                    if (!message.isEmpty() && !title.isEmpty()) {
                        sendNotificationsToWaitlist(title, message);
                    } else {
                        // Show a Toast if either title or message is empty
                        Toast.makeText(getContext(), "Message and title cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void sendNotificationsToWaitlist(String customName, String customMessage) {
        db.collection("events").document(eventId).get().addOnSuccessListener(eventDoc -> {
            if (eventDoc.exists()) {
                ArrayList<String> deviceIds = (ArrayList<String>) eventDoc.get("waitlistedNotificationsList");

                if (deviceIds != null && !deviceIds.isEmpty()) {
                    NotificationHelper notificationHelper = new NotificationHelper(getActivity());
                    // Send individual notifications to each user in the waitlist
                    for (int i = 0; i < deviceIds.size(); i++) {
                        String deviceId = deviceIds.get(i);

                        db.collection("profiles").document(deviceId).get().addOnSuccessListener(profileDoc -> {
                            if (profileDoc.exists()) {
                                // Build a notification for each waitlisted user
                                notificationHelper.sendNotificationscraftW(deviceId,customName,customMessage);
                            }
                        });
                    }
                    Toast.makeText(getContext(), "Notifications sent to waitlisted users.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}