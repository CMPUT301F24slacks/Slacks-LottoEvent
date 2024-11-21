package com.example.slacks_lottoevent;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

        // Retrieve the event ID from the fragment's arguments
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_organizer_waitlist, container, false);
        ListViewEntrantsWaitlisted = view.findViewById(R.id.listViewEntrantsWaitlisted);

        ArrayList<String> entrantNames = new ArrayList<>();
        EntrantListsArrayAdapter adapter = new EntrantListsArrayAdapter(getContext(), entrantNames);
        ListViewEntrantsWaitlisted.setAdapter(adapter);

        // Use the event ID to fetch data from Firestore
        db.collection("events").document(eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot eventDoc = task.getResult();

                if (eventDoc.exists()) {
                    ArrayList<String> deviceIds = (ArrayList<String>) eventDoc.get("waitlisted");

                    if (deviceIds != null && !deviceIds.isEmpty()) {
                        for (String deviceId : deviceIds) {
                            db.collection("profiles").document(deviceId).get().addOnCompleteListener(profileTask -> {
                                if (profileTask.isSuccessful() && profileTask.getResult() != null) {
                                    DocumentSnapshot profileDoc = profileTask.getResult();

                                    if (profileDoc.exists()) {
                                        String name = profileDoc.getString("name");
                                        entrantNames.add(name);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        Log.d("Firestore", "Profile document does not exist for device ID: " + deviceId);
                                    }
                                } else {
                                    Log.e("Firestore", "Error fetching profile data for device ID: " + deviceId, profileTask.getException());
                                }
                            });
                        }
                    } else {
                        Log.d("Firestore", "No device IDs found in the selectedNotificationsList.");
                    }
                } else {
                    Log.d("Firestore", "Event document does not exist for ID: " + eventId);
                }
            } else {
                Log.e("Firestore", "Error fetching event data for ID: " + eventId, task.getException());
            }
        });

        return view;
    }
}
