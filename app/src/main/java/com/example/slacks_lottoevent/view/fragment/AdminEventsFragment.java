package com.example.slacks_lottoevent.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.slacks_lottoevent.R;
import com.example.slacks_lottoevent.model.Event;
import com.example.slacks_lottoevent.viewmodel.adapter.OrganizerEventArrayAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
/**
 * A fragment that displays a list of events for the admin to browser. Apart of the admin events tab.
 * Evenbs are fetched from the Firestore "events" collection and displayed.
 *
 * */
public class AdminEventsFragment extends Fragment {

    private ListView listViewAdminEvents;
    private FirebaseFirestore db;
    private ArrayList<Event> eventsList;
    private OrganizerEventArrayAdapter adapter;


    /**
     * Creates a new instance of {@link AdminEventsFragment}.
     * This static factory method can be used to create a new fragment instance.
     *
     * @return A new instance of {@link AdminEventsFragment}.
     */
    public static AdminEventsFragment newInstance() {
        return new AdminEventsFragment();
    }
    /**
     * Called when the fragment is created.
     * Initializes Firestore and the events list.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance(); // Initialize Firestore instance
        eventsList = new ArrayList<>(); // Initialize the profile list
    }
    /**
     * Called to create the view hierarchy associated with the fragment.
     * Inflates the layout and initializes the ListView and its adapter.
     *
     * @param inflater           The LayoutInflater object used to inflate views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
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
     * Fetches all events from the "events" collection in Firestore
     * and populates the events list.
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
