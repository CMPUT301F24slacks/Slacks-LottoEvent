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

public class AdminProfilesFragment extends Fragment {

    private ListView listViewAdminProfiles;
    private FirebaseFirestore db;
    private ArrayList<Profile> profileList;
    private ProfileListArrayAdapter adapter;

    // Static factory method (optional)
    public static AdminProfilesFragment newInstance() {
        return new AdminProfilesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance(); // Initialize Firestore instance
        profileList = new ArrayList<>(); // Initialize the profile list
    }

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
