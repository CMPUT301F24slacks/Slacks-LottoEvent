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
        adapter = new FacilityListArrayAdapter(getContext(), facilitiesList, true);
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
                    // Create Facility object from Firestore data
                    String deviceId = document.getString("deviceId");
                    String name = document.getString("name");
                    String streetAddress1 = document.getString("streetAddress1");
                    Facility facility = new Facility(name, streetAddress1, deviceId, deviceId);
//                    Facility facility = document.toObject(Facility.class);
                    facilitiesList.add(facility); // Add to the list
                }
                adapter.notifyDataSetChanged(); // Notify adapter about data changes
            } else {
                Log.d("Firestore", "No facilities found.");
            }
        });
    }

}
