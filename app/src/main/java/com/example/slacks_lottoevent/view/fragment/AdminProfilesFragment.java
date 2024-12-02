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
import com.example.slacks_lottoevent.model.Profile;
import com.example.slacks_lottoevent.viewmodel.adapter.ProfileListArrayAdapter;
import com.example.slacks_lottoevent.R;
import com.example.slacks_lottoevent.viewmodel.adapter.FacilityListArrayAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
/**
 * A fragment that displays a list of user profiles in the admin panel.
 * Profiles are fetched from a Firestore "profiles" collection and displayed in a ListView.
 */
public class AdminProfilesFragment extends Fragment {

    private ListView listViewAdminProfiles;
    private FirebaseFirestore db;
    private ArrayList<Profile> profileList;
    private ProfileListArrayAdapter adapter;


    /**
     * Static factory method to create a new instance of {@link AdminProfilesFragment}.
     *
     * @return A new instance of {@link AdminProfilesFragment}.
     */
    public static AdminProfilesFragment newInstance() {
        return new AdminProfilesFragment();
    }
    /**
     * Called when the fragment is created.
     * Initializes Firestore and the profile list.
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance(); // Initialize Firestore instance
        profileList = new ArrayList<>(); // Initialize the profile list
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
        View view = inflater.inflate(R.layout.fragment_admin_profiles, container, false);
        listViewAdminProfiles = view.findViewById(R.id.ListViewAdminProfiles);

        ArrayList<Event> eventList = new ArrayList<>();
        ArrayList<Facility> facilityList = new ArrayList<>();

        OrganizerEventArrayAdapter eventsAdapter = new OrganizerEventArrayAdapter(getContext(),
                                                                                  eventList, true);
        FacilityListArrayAdapter facilitiesAdapter = new FacilityListArrayAdapter(getContext(),
                                                                                  facilityList,
                                                                                  true,
                                                                                  eventsAdapter,
                                                                                  eventList);

        // Initialize adapter with the profile list
        adapter = new ProfileListArrayAdapter(getContext(), profileList, true, facilityList,
                                              facilitiesAdapter,
                                              eventList, eventsAdapter);
        listViewAdminProfiles.setAdapter(adapter);

        // Fetch profiles from Firestore
        fetchProfilesFromFirestore();

        return view;
    }

    /**
     * Fetches all profiles from the "profiles" collection in Firestore
     * and populates the profile list.
     */
    private void fetchProfilesFromFirestore() {
        db.collection("profiles").addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
                Log.e("Firestore", "Error listening for profile updates: ", error);
                return;
            }

            if (querySnapshot != null) {
                profileList.clear(); // Clear the list before adding new data

                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    // Create Profile object from Firestore data
                    Profile profile = document.toObject(Profile.class);
                    profileList.add(profile); // Add to the list
                }
                adapter.notifyDataSetChanged(); // Notify adapter about data changes
            } else {
                Log.d("Firestore", "No profiles found.");
            }
        });
    }
}
