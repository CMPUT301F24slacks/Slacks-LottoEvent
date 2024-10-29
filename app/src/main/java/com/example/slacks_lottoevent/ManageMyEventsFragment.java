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

import com.example.slacks_lottoevent.databinding.FragmentManageMyEventsBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.common.BitMatrix;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This activity is responsible for managing the events created by the organizer.
 */
public class ManageMyEventsFragment extends Fragment {

    private OrganzierEventArrayAdapter organzierEventArrayAdapter;
    private ListView myEventsListView;
    private FragmentManageMyEventsBinding binding;

    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private ArrayList<Event> eventList;

//    organzierEventArrayAdapter = new void OrganzierEventArrayAdapter(getContext(), eventList);

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

        myEventsListView = binding.myEventsListView;
        organzierEventArrayAdapter = new OrganzierEventArrayAdapter(getContext(), eventList);
        myEventsListView.setAdapter(organzierEventArrayAdapter);

        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("FirestoreError", "Listen failed.", error);
                    return;
                }

                if (value != null && !value.isEmpty()) {
                    eventList.clear();
                    for (DocumentSnapshot document : value.getDocuments()) {
                        HashMap<String, Object> eventData = (HashMap<String, Object>) document.getData();

                        if (eventData != null) {
                            try {
                                HashMap<String, Object> eventDetails = (HashMap<String, Object>) eventData.get("eventDetails");
                                if (eventDetails != null) {
                                    // Now you can access fields within eventDetails
                                    HashMap<String, Object> facilityData = (HashMap<String, Object>) eventDetails.get("facilities");
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
                                    }

                                    // Create an Organizer instance (you may want to modify this as per your data structure)
                                    Organizer organizer = new Organizer();

                                    String name = (String) eventDetails.get("name");
                                    String date = (String) eventDetails.get("date");
                                    String time = (String) eventDetails.get("time");
                                    String price = (String) eventDetails.get("price");
                                    String details = (String) eventDetails.get("description");
                                    String qrData = (String) eventDetails.get("qrdata");
                                    Long capacityLong = (Long) eventDetails.get("capacity");
                                    Integer capacity = (capacityLong != null) ? capacityLong.intValue() : 0; // Default value

                                    Long signupAcptLong = (Long) eventDetails.get("signupAcpt");
                                    Integer pplAccpt = (signupAcptLong != null) ? signupAcptLong.intValue() : 0;
                                    String extraDesc = (String) eventDetails.get("extraDesc");

                                    String eventID = (String) eventDetails.get("eventid");

                                    Event newEvent = new Event(organizer, facility, name, date, time, price, details, pplAccpt, capacity, extraDesc, qrData, eventID);
                                    eventList.add(newEvent);
                                    organzierEventArrayAdapter.notifyDataSetChanged();
                                    Log.d("EventSuccess", "Added to eventList!!");

                                } else {
                                    Log.e("EventDataError", "eventDetails map is null for document: " + document.getId());
                                }
                            } catch (ClassCastException cce) {
                                Log.e("EventError", "ClassCastException: " + cce.getMessage());
                            }
                        }
                    }
                }
            }
        });

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
