package com.example.slacks_lottoevent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganizerWaitlistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganizerWaitlistFragment extends Fragment {

    private static final String ARG_EVENT_ID = "eventID";
    Notifications notifications;
    private ListView ListViewEntrantsWaitlisted;
    private String eventId;
    private FirebaseFirestore db;
    private ArrayList<Profile> profileList;

    /**
     * Default constructor
     */
    public OrganizerWaitlistFragment() {
    }

    /**
     * Factory method to create a new instance of this fragment using the provided parameters.
     *
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_organizer_waitlist, container, false);
        ListViewEntrantsWaitlisted = view.findViewById(R.id.listViewEntrantsWaitlisted);

        ProfileListArrayAdapter adapter = new ProfileListArrayAdapter(getContext(), profileList,
                                                                      false, null, null,
                                                                      null, null);
        ListViewEntrantsWaitlisted.setAdapter(adapter);

        Button craftMessageButton = view.findViewById(R.id.craftMessage);
        notifications = new Notifications();

        craftMessageButton.setOnClickListener(
                v -> DialogHelper.showMessageDialog(getContext(), notifications, eventId,
                                                    "waitlistedNotificationsList")); // Button to show the dialog

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
                        db.collection("profiles").document(deviceId)
                          .addSnapshotListener((profileDoc, profileError) -> {
                              if (profileError != null) {
                                  Log.e("Firestore", "Error listening to profile document updates",
                                        profileError);
                                  return;
                              }

                              if (profileDoc != null && profileDoc.exists()) {
                                  Profile profile = profileDoc.toObject(Profile.class);
                                  profileList.add(
                                          profile); // Add the name if it’s not already in the list
                                  adapter.notifyDataSetChanged(); // Update the adapter
                              } else {
                                  Log.d("Firestore",
                                        "Profile document does not exist for device ID: " +
                                        deviceId);
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

}