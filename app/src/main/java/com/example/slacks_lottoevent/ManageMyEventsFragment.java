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
        facilitiesRef = db.collection("facilities");

        myEventsListView = binding.myEventsListView;
        organzierEventArrayAdapter = new OrganzierEventArrayAdapter(getContext(), eventList);
        myEventsListView.setAdapter(organzierEventArrayAdapter);

        createFacilitiesButton = view.findViewById(R.id.create_facility_button);
        facilityCreated = view.findViewById(R.id.facility_created);




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
                        HashMap<String, Object> eventData = (HashMap<String, Object>) document.getData();

                        if (eventData != null) {
                            try {
                                // Extract event details
                                HashMap<String, Object> eventDetails = (HashMap<String, Object>) eventData.get("eventDetails");
                                if (eventDetails != null) {
                                    // Now you can access fields within eventDetails
                                    HashMap<String, Object> facilityData = (HashMap<String, Object>) eventDetails.get("facilities");
                                    // Facility and organizer setup
                                    Facility facility = null;

                                    if (facilityData != null) {
                                        String facilityName = (String) facilityData.get("name");
                                        String facilityStreetAddress1 = (String) facilityData.get("streetAddress1");
                                        String facilityStreetAddress2 = (String) facilityData.get("streetAddress2");
                                        String facilityCity = (String) facilityData.get("city");
                                        String facilityProvince = (String) facilityData.get("province");
                                        String facilityCountry = (String) facilityData.get("country");
                                        String facilityPostalCode = (String) facilityData.get("postalCode");

                                        facility = new Facility(facilityName, facilityStreetAddress1, facilityStreetAddress2, facilityCity, facilityProvince, facilityCountry, facilityPostalCode);
                                        Profile tempUser = new Profile("John Doe", "123-456-7890", "123@gmail.com");

                                    }

                                    Profile tempUser = new Profile("John Doe", "123-456-7890", "123@gmail.com");
                                    Organizer organizer = new Organizer(tempUser);

                                    // Retrieve and cast event fields
                                    String name = (String) eventDetails.get("name");
                                    String date = (String) eventDetails.get("date");
                                    String time = (String) eventDetails.get("time");
                                    String price = (String) eventDetails.get("price");
                                    String details = (String) eventDetails.get("description");
                                    String qrData = (String) eventDetails.get("qrdata");
                                    Integer capacity = ((Long) eventDetails.getOrDefault("capacity", 0L)).intValue();
                                    Integer pplAccpt = ((Long) eventDetails.getOrDefault("signupAcpt", 0L)).intValue();
                                    Boolean geoLoc = (Boolean) eventDetails.get("geoLocation");
                                    String eventID = (String) eventDetails.get("eventid");

                                    // Add new event to the temporary list
                                    Event newEvent = new Event(organizer, facility, name, date, time, price, details, pplAccpt, capacity, qrData, eventID, geoLoc);
                                    newEvents.add(newEvent);
                                }
                            } catch (ClassCastException e) {
                                Log.e("EventError", "ClassCastException while processing event data", e);
                            }
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
