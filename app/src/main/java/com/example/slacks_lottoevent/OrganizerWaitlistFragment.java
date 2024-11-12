package com.example.slacks_lottoevent;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.slacks_lottoevent.model.Event;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass to manage and display entrants.
 */
public class OrganizerWaitlistFragment extends Fragment {

    private ListView listViewEntrantsWaitlisted;
    private Event event;
    private FirebaseFirestore db;

    private static final String ARG_EVENT = "current_event";

    /**
     * Default constructor
     */
    public OrganizerWaitlistFragment() {}

    /**
     * Factory method to create a new instance of this fragment using the provided parameters.
     * @param event The current event.
     * @return A new instance of OrganizerWaitlistFragment.
     */
    public static OrganizerWaitlistFragment newInstance(Event event) {
        OrganizerWaitlistFragment fragment = new OrganizerWaitlistFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT, event);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * onCreate method
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
     * onCreateView method
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_waitlist, container, false);

        listViewEntrantsWaitlisted = view.findViewById(R.id.listViewEntrantsWaitlisted);

        // Initialize lists and adapters
        ArrayList<String> waitlistedNames = new ArrayList<>();

        EntrantListsArrayAdapter waitlistAdapter = new EntrantListsArrayAdapter(getContext(), waitlistedNames);

        listViewEntrantsWaitlisted.setAdapter(waitlistAdapter);

        // Fetch data for waitlisted entrants
        if (event != null) {
            fetchEntrants(event.getWaitlisted(), waitlistedNames, waitlistAdapter, "Waitlisted");
        }

        return view;
    }

    /**
     * Fetch entrant data from Firestore for a list of device IDs and update the corresponding list and adapter.
     * @param deviceIds The list of device IDs to fetch.
     * @param entrantNames The list to populate with names.
     * @param adapter The adapter to notify of data changes.
     * @param logTag A tag for logging the operation (e.g., "Waitlisted", "Cancelled").
     */
    private void fetchEntrants(ArrayList<String> deviceIds, ArrayList<String> entrantNames, EntrantListsArrayAdapter adapter, String logTag) {
        for (String deviceId : deviceIds) {
            db.collection("profiles").document(deviceId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        String name = document.getString("name"); // Adjust field as per Firestore structure
                        if (name != null) {
                            entrantNames.add(name);
                            Log.d("Firestore", logTag + " Entrant: " + name);
                        } else {
                            Log.d("Firestore", logTag + " Entrant has no name for deviceId: " + deviceId);
                        }
                    } else {
                        Log.d("Firestore", logTag + " No document found for deviceId: " + deviceId);
                    }
                } else {
                    Log.e("Firestore", logTag + " Error fetching document for deviceId: " + deviceId, task.getException());
                }

                // Notify adapter of data change on the main thread
                requireActivity().runOnUiThread(adapter::notifyDataSetChanged);
            });
        }
    }
}
