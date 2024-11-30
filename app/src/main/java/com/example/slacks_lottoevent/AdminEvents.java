package com.example.slacks_lottoevent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminEvents extends Fragment {

    private ListView listViewAdminEvents;
    private FirebaseFirestore db;
    private ArrayList<Event> eventsList;
    private OrganizerEventArrayAdapter adapter;

    // Static factory method (optional)
    public static AdminEvents newInstance() {
        return new AdminEvents();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance(); // Initialize Firestore instance
        eventsList = new ArrayList<>(); // Initialize the profile list
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_events, container, false);
        listViewAdminEvents = view.findViewById(R.id.ListViewAdminEvents);

        // Initialize adapter with the profile list
        adapter = new OrganizerEventArrayAdapter(getContext(), eventsList, true);
        listViewAdminEvents.setAdapter(adapter);

        // Fetch profiles from Firestore
        fetchEventsFromFirestore();

        return view;
    }

    /**
     * Fetches all profiles from the "profiles" collection in Firestore
     * and populates the profile list.
     */
    private void fetchEventsFromFirestore() {
        db.collection("events").addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.e("Firestore", "Listen failed.", e);
                return;
            }

            if (querySnapshot != null) {
                eventsList.clear(); // Clear the list before adding new data
                for (DocumentSnapshot document : querySnapshot) {
                    // Map the document to the Event class
                    Event event = document.toObject(Event.class);
                    eventsList.add(event); // Add to the list
                }
                adapter.notifyDataSetChanged(); // Notify adapter about data changes
            } else {
                Log.d("Firestore", "No data found in events collection.");
            }
        });
    }

}
