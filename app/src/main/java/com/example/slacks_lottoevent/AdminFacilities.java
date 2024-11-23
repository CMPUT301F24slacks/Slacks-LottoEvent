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

public class AdminFacilities extends Fragment {

    private ListView listViewAdminFacilities;
    private FirebaseFirestore db;
    private ArrayList<Facility> facilitiesList;
    private FacilityListArrayAdapter adapter;

    // Static factory method (optional)
    public static AdminFacilities newInstance() {
        return new AdminFacilities();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance(); // Initialize Firestore instance
        facilitiesList = new ArrayList<>(); // Initialize the profile list
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_facilities, container, false);
        listViewAdminFacilities = view.findViewById(R.id.ListViewAdminFacilities);

        // Initialize adapter with the profile list
        adapter = new FacilityListArrayAdapter(getContext(), facilitiesList);
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
        db.collection("profiles")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        facilitiesList.clear(); // Clear the list before adding new data
                        for (DocumentSnapshot document : task.getResult()) {
                            // Create Profile object from Firestore data
                            String name = document.getString("name");
                            String facilityId = document.getId();
                            String organizerId = document.getString("organizerID");
                            Facility facility = new Facility(name," ",organizerId,facilityId); // Adjust constructor if needed

                            facilitiesList.add(facility); // Add to the list
                        }
                        adapter.notifyDataSetChanged(); // Notify adapter about data changes
                    } else {
                        Log.e("Firestore", "Error fetching profiles: ", task.getException());
                    }
                });
    }
}
