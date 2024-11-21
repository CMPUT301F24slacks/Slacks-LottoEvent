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
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganizerInvitedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganizerInvitedFragment extends Fragment {

    private ListView listViewEntrantsInvited;
    private Event event;
    private static final String ARG_EVENT = "current_event";
    private FirebaseFirestore db;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     //     * @param param1 Parameter 1.
     //     * @param param2 Parameter 2.
     * @return A new instance of fragment OrganizerFirstFragment.
     */
    public OrganizerInvitedFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     //     * @param param1 Parameter 1.
     //     * @param param2 Parameter 2.
     * @return A new instance of fragment OrganizerFirstFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrganizerInvitedFragment newInstance(Event event) {
        OrganizerInvitedFragment fragment = new OrganizerInvitedFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT, event);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     //     * @param param1 Parameter 1.
     //     * @param param2 Parameter 2.
     * @return A new instance of fragment OrganizerFirstFragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable(ARG_EVENT);
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     //     * @param param1 Parameter 1.
     //     * @param param2 Parameter 2.
     * @return A new instance of fragment OrganizerFirstFragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_organizer_invited, container, false);
        listViewEntrantsInvited = view.findViewById(R.id.listViewEntrantsInvited);

        ArrayList<String> entrantNames = new ArrayList<>(); // List to hold retrieved names
        EntrantListsArrayAdapter adapter = new EntrantListsArrayAdapter(getContext(), entrantNames);
        listViewEntrantsInvited.setAdapter(adapter); // Set the adapter once

        // Event ID for testing
        String eventId = event.getEventID();

        db.collection("events").document(eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot eventDoc = task.getResult();

                if (eventDoc.exists()) {
                    // Retrieve the list of device IDs from Firestore
                    ArrayList<String> deviceIds = (ArrayList<String>) eventDoc.get("selectedNotificationsList");

                    if (deviceIds != null && !deviceIds.isEmpty()) {
                        // Loop through device IDs and fetch profile names
                        for (String deviceId : deviceIds) {
                            db.collection("profiles").document(deviceId).get().addOnCompleteListener(profileTask -> {
                                if (profileTask.isSuccessful() && profileTask.getResult() != null) {
                                    DocumentSnapshot profileDoc = profileTask.getResult();

                                    if (profileDoc.exists()) {
                                        String name = profileDoc.getString("name"); // Adjust key as per your structure
                                        entrantNames.add(name);

                                        // Notify the adapter that the data has changed
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