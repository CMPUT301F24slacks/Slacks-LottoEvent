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

import com.example.slacks_lottoevent.model.Event;
import com.example.slacks_lottoevent.model.Facility;
import com.example.slacks_lottoevent.viewmodel.adapter.OrganizerEventArrayAdapter;
import com.example.slacks_lottoevent.R;
import com.example.slacks_lottoevent.viewmodel.adapter.FacilityListArrayAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
/**
 * A fragment that displays a list of facilities in the admin panel.
 * The facilities are fetched from a Firestore "facilities" collection
 * and displayed in a ListView.
 */
public class AdminFacilitiesFragment extends Fragment {

    private ListView listViewAdminFacilities;
    private FirebaseFirestore db;
    private ArrayList<Facility> facilitiesList;
    private FacilityListArrayAdapter adapter;

    /**
     * Static factory method to create a new instance of AdminFacilitiesFragment.
     *
     * @return A new instance of AdminFacilitiesFragment.
     */
    public static AdminFacilitiesFragment newInstance() {
        return new AdminFacilitiesFragment();
    }
    /**
     * Called when the fragment is created.
     * Initializes Firestore and the facilities list.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance(); // Initialize Firestore instance
        facilitiesList = new ArrayList<>(); // Initialize the profile list
    }
    /**
     * Called to create the view hierarchy associated with the fragment.
     * Inflates the layout and initializes the {@link ListView} and its adapter.
     *
     * @param inflater           The { LayoutInflater object used to inflate views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_facilities, container, false);
        listViewAdminFacilities = view.findViewById(R.id.ListViewAdminFacilities);

        // Initialize facilities list and event list
        ArrayList<Event> eventList = new ArrayList<>();

        // Create the events adapter
        OrganizerEventArrayAdapter eventsAdapter = new OrganizerEventArrayAdapter(getContext(),
                                                                                  eventList, true);

        // Create the facilities adapter
        adapter = new FacilityListArrayAdapter(getContext(), facilitiesList, true, eventsAdapter,
                                               eventList);

        // Set the adapter to a ListView or RecyclerView
        listViewAdminFacilities.setAdapter(adapter);

        // Fetch profiles from Firestore
        fetchFacilitiesFromFirestore();

        return view;
    }

    /**
     * Fetches all profiles from the "profiles" collection in Firestore
     * and populates the profile list.
     */
    private void fetchFacilitiesFromFirestore() {
        db.collection("facilities").addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
                Log.e("Firestore", "Error listening for facility updates: ", error);
                return;
            }

            if (querySnapshot != null) {
                facilitiesList.clear(); // Clear the list before adding new data

                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    Facility facility = document.toObject(Facility.class);

                    facilitiesList.add(facility); // Add to the list
                }
                adapter.notifyDataSetChanged(); // Notify adapter about data changes
            } else {
                Log.d("Firestore", "No facilities found.");
            }
        });
    }

}
