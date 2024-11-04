package com.example.slacks_lottoevent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.slacks_lottoevent.databinding.FragmentManageMyEventsBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This activity is responsible for managing the events created by the organizer.
 */
public class ManageMyEventsFragment extends Fragment implements AddFacilityFragment.AddFacilityDialogListener{

    private OrganzierEventArrayAdapter organzierEventArrayAdapter;
    private ListView myEventsListView;
    private FragmentManageMyEventsBinding binding;
    private TextView facilityCreated;
    private Facility existingFacility;
    private Button createFacilitiesButton;

    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private ArrayList<Event> eventList;
    private CollectionReference facilitiesRef;

//    organzierEventArrayAdapter = new void OrganzierEventArrayAdapter(getContext(), eventList);

    @Override
    public void addFacility(Facility facility) {
        // Retrieve the facility name and set it to display on the screen
        String facilityName = facility.getFacilityName();
        facilityCreated.setText(facilityName);
        existingFacility = facility;

        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("name", facilityName);
        // Add other attributes as needed, like location, type, etc.
        facilityData.put("streetAddress1", facility.getStreetAddress1());
        facilityData.put("streetAddress2", facility.getStreetAddress2());
        facilityData.put("city", facility.getCity());
        facilityData.put("province", facility.getProvince());
        facilityData.put("country", facility.getCountry());
        facilityData.put("postalCode", facility.getProvince());

        // Add the facility data to Firestore
        facilitiesRef.add(facilityData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("addFacility", "Facility added with ID: " + documentReference.getId());
                    existingFacility.setFacilityId(documentReference.getId()); // Set document ID here
                })
                .addOnFailureListener(e -> Log.w("addFacility", "Error adding facility", e));

        // Hide the create facility button
        createFacilitiesButton.setVisibility(View.GONE);

    }

    @Override
    public void updateFacility() {
        String facilityName = existingFacility.getFacilityName();
        facilityCreated.setText(facilityName);

        if (existingFacility == null) {
            Log.w("updateFacility", "No facility selected to update");
            return;
        }

        // Retrieve the document ID for the existing facility
        String documentId = existingFacility.getFacilityId();
        if (documentId == null || documentId.isEmpty()) {
            Log.w("updateFacility", "No document ID provided for update");
            return;
        }

        // Prepare the updated facility data
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("name", existingFacility.getFacilityName());
        facilityData.put("streetAddress1", existingFacility.getStreetAddress1());
        facilityData.put("streetAddress2", existingFacility.getStreetAddress2());
        facilityData.put("city", existingFacility.getCity());
        facilityData.put("province", existingFacility.getProvince());
        facilityData.put("country", existingFacility.getCountry());
        facilityData.put("postalCode", existingFacility.getPostalCode());

        // Update the document in Firestore
        facilitiesRef.document(documentId).update(facilityData)
                .addOnSuccessListener(aVoid -> Log.d("updateFacility", "Facility updated successfully"))
                .addOnFailureListener(e -> Log.w("updateFacility", "Error updating facility", e));
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentManageMyEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        eventList = new ArrayList<>(); // Initialize the event list
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events"); // Reference to events collection

//        TODO: remove the facilities collection from here because it is unneeded and grab the organizer data pertaining to it
//        TODO: grab organizer collection, grab the ID, grab the facility object and grab the event ID list
        facilitiesRef = db.collection("facilities");

        myEventsListView = binding.myEventsListView;
        organzierEventArrayAdapter = new OrganzierEventArrayAdapter(getContext(), eventList);
        myEventsListView.setAdapter(organzierEventArrayAdapter);

        createFacilitiesButton = view.findViewById(R.id.create_facility_button);
        facilityCreated = view.findViewById(R.id.facility_created);



//        TODO: compare with the events in organizer collection and grab the events in the event collection and grab the event object and then yeah
//        Grab organizer id, go into that collection, grab events, cross reference with the events collections, grab eventIDs and then dislpay

        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("FirestoreError", "Listen failed.", error);
                    return;
                }

                if (snapshots != null && !snapshots.isEmpty()) {
                    ArrayList<Event> newEvents = new ArrayList<>(); // Temporary list to minimize UI updates

                    for (DocumentSnapshot document : snapshots.getDocuments()) {
                        // Convert each document into an Event object using Firestore’s built-in method
                        Event event = document.toObject(Event.class);
                        if (event != null) {
                            newEvents.add(event);
                        }
                    }
                    // Update eventList and UI only if there are changes
                    if (!newEvents.equals(eventList)) {
                        eventList.clear();
                        eventList.addAll(newEvents);
                        organzierEventArrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        facilitiesRef.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.w("Firestore", "Listen failed.", e);
                return;
            }
            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                String facilityName = document.getString("name");
                if (facilityName != null && !facilityName.isEmpty()) {
                    facilityCreated.setText(facilityName);
                    facilityCreated.setVisibility(View.VISIBLE);
                    // Populate existingFacility with data from Firestore
                    existingFacility = document.toObject(Facility.class);
                    existingFacility.setFacilityId(document.getId());
                    existingFacility.setFacilityName(facilityName);
                } else {
                    facilityCreated.setVisibility(View.GONE);
                    existingFacility = null;
                }
            } else {
                facilityCreated.setVisibility(View.GONE);
                createFacilitiesButton.setVisibility(View.VISIBLE);
            }
        });

        if (existingFacility == null) {
            createFacilitiesButton.setVisibility(View.GONE);
        } else {
            createFacilitiesButton.setVisibility(View.VISIBLE);
        }

        createFacilitiesButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                // Referenced ChatGPT with prompt "How to show a dialog within a fragment" on Oct 29, 2024
                // License: OpenAI
                new AddFacilityFragment().show(getChildFragmentManager(), "Add Facility");
            }
        });

        facilityCreated.setOnClickListener(v -> {
            new AddFacilityFragment(existingFacility, true).show(getChildFragmentManager(), "Edit Facility");
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
