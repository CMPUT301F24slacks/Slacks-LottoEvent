package com.example.slacks_lottoevent;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.slacks_lottoevent.databinding.FragmentManageMyEventsBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This activity is responsible for managing the events created by the organizer.
 */
public class ManageMyEventsFragment extends Fragment implements AddFacilityFragment.AddFacilityDialogListener{

    private OrganizerEventArrayAdapter organizerEventArrayAdapter;
    private ListView myEventsListView;
    private FragmentManageMyEventsBinding binding;
    private TextView facilityCreated;
    private Facility existingFacility;
    private Button createFacilitiesButton;

    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private ArrayList<Event> eventList;
    private CollectionReference facilitiesRef;
    private CollectionReference organizersRef;
    String deviceId;
    FacilityViewModel facilityViewModel;
    private List<ListenerRegistration> eventListeners = new ArrayList<>();


    /**
     * This method is called when the user creates a new facility.
     * It adds the facility to the Firestore database.
     *
     * @param facility The facility object to be added
     */
    @Override
    public void addFacility(Facility facility) {
        // Retrieve the facility name and set it to display on the screen
        String facilityName = facility.getFacilityName();

//        This is where we query for organizers for facility name
        facilityCreated.setText(facilityName);
        existingFacility = facility;
        System.out.println("facility" + existingFacility);
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("facilityName", facilityName);
        // Add other attributes as needed, like location, type, etc.
        facilityData.put("streetAddress1", facility.getStreetAddress1());
        facilityData.put("organizerID", facility.getOrganizerID());
        facilityData.put("deviceID", facility.getDeviceId());

        facilitiesRef.add(facilityData)
                .addOnSuccessListener(documentReference -> {
                    existingFacility.setFacilityId(documentReference.getId()); // Set document ID here

                    // Now add the facility data to the organizer after the facility ID is set
                    Map<String, Object> organizerData = new HashMap<>();
                    organizerData.put("userId", facility.getOrganizerID());
                    organizerData.put("facilityId", existingFacility.getFacilityId());
                    organizerData.put("events", null); // Assuming EventList can be serialized

                    organizersRef.document(facility.getOrganizerID()) // Sets userId as the document ID
                            .set(organizerData)
                            .addOnSuccessListener(aVoid -> {
                                // Update the ViewModel to reflect the change
                                facilityViewModel.setFacilityStatus(true);
                                facilityViewModel.setCurrentFacility(existingFacility);
                            })
                            .addOnFailureListener(e -> {
                                Log.w("addFacility", "Error adding organizer", e);
                            });
                })
                .addOnFailureListener(e -> Log.w("addFacility", "Error adding facility", e));


        // Hide the create facility button
        createFacilitiesButton.setVisibility(View.GONE);
        facilityCreated.setVisibility(View.VISIBLE);

    }

    /**
     * This method is called when the user updates an existing facility.
     * It updates the facility in the Firestore database.
     */
    @Override
    public void updateFacility() {
        if (existingFacility == null) {
            Log.w("updateFacilityAndEvents", "No facility selected to update");
            return;
        }

        String facilityId = existingFacility.getFacilityId();
        if (facilityId == null || facilityId.isEmpty()) {
            Log.w("updateFacilityAndEvents", "No facility ID provided for update");
            return;
        }

        // Prepare the updated facility data
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("facilityName", existingFacility.getFacilityName());
        facilityData.put("streetAddress1", existingFacility.getStreetAddress1());

        // Update the facility and then proceed to update related events
        facilitiesRef.document(facilityId).update(facilityData)
                .addOnSuccessListener(aVoid -> {
                    // Query to fetch all events associated with this facility
                    eventsRef.whereEqualTo("deviceId", deviceId).get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    WriteBatch batch = db.batch(); // Create a batch for atomic updates
                                    // Iterate through all matching events and update their location
                                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                                        DocumentReference eventDocRef = document.getReference();
                                        batch.update(eventDocRef, "location", existingFacility.getStreetAddress1());
                                    }
                                    // Commit the batch write
                                    batch.commit()
                                            .addOnSuccessListener(aVoid1 -> Log.d("updateFacilityAndEvents", "Event locations updated successfully"))
                                            .addOnFailureListener(e -> Log.w("updateFacilityAndEvents", "Error updating event locations", e));
                                } else {
                                    Log.d("updateFacilityAndEvents", "No events found for this facility");
                                }
                            })
                            .addOnFailureListener(e -> Log.w("updateFacilityAndEvents", "Error fetching events", e));
                })
                .addOnFailureListener(e -> Log.w("updateFacilityAndEvents", "Error updating facility", e));
    }

    private void fetchOrganizerEvents(String deviceId) {
        organizersRef.document(deviceId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot organizerDoc = task.getResult();
                if (organizerDoc.exists() && organizerDoc.contains("events")) {
                    List<String> eventIDs = (List<String>) organizerDoc.get("events");
                    if (eventIDs != null && !eventIDs.isEmpty()) {
                        retrieveEventDetails(eventIDs);
                    } else {
                        handleEmptyEvents();
                    }
                } else {
                    handleMissingEvents();
                }
            } else {
                Log.w("Firestore", "Organizer query failed.", task.getException());
            }
        });
    }

    private void retrieveEventDetails(List<String> eventIDs) {
        ArrayList<Event> newEvents = new ArrayList<>();
        for (String eventID : eventIDs) {
            eventsRef.document(eventID).get().addOnCompleteListener(eventTask -> {
                if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                    DocumentSnapshot eventDoc = eventTask.getResult();
                    if (eventDoc.exists()) {
                        Event event = eventDoc.toObject(Event.class);
                        if (event != null) {
                            newEvents.add(event);
                            if (newEvents.size() == eventIDs.size()) {
                                updateEventList(newEvents);
                            }
                        }
                    } else {
                        Log.w("Firestore", "Event document not found: " + eventID);
                    }
                } else {
                    Log.w("Firestore", "Failed to retrieve event document.", eventTask.getException());
                }
            });
        }
    }

    private void updateEventList(ArrayList<Event> newEvents) {
        if (!newEvents.equals(eventList)) {
            eventList.clear();
            eventList.addAll(newEvents);
            organizerEventArrayAdapter.notifyDataSetChanged();
        }
    }

    private void handleEmptyEvents() {
        Log.w("Firestore", "No events found for this organizer.");
        eventList.clear();
        organizerEventArrayAdapter.notifyDataSetChanged();
    }

    private void handleMissingEvents() {
        Log.w("Firestore", "Organizer document does not exist or eventList missing.");
        eventList.clear();
        organizerEventArrayAdapter.notifyDataSetChanged();
    }

    private void fetchOrganizerFacility(String deviceId) {
        organizersRef.document(deviceId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot organizerDoc = task.getResult();
                if (organizerDoc.exists() && organizerDoc.contains("facilityId")) {
                    String facilityID = organizerDoc.getString("facilityId");
                    if (facilityID != null && !facilityID.isEmpty()) {
                        fetchFacilityDetails(facilityID);
                    } else {
                        handleMissingFacilityID();
                    }
                } else {
                    handleMissingFacilityID();
                }
            } else {
                Log.w("Firestore", "Organizer query failed.", task.getException());
                facilityViewModel.setFacilityStatus(false);
            }
        });
    }

    private void fetchFacilityDetails(String facilityID) {
        facilitiesRef.document(facilityID).addSnapshotListener((facilitySnapshot, e) -> {
            if (e != null) {
                Log.w("Firestore", "Facility listen failed.", e);
                facilityViewModel.setFacilityStatus(false);
                return;
            }

            if (facilitySnapshot != null && facilitySnapshot.exists()) {
                handleFacilityExists(facilitySnapshot);
            } else {
                handleFacilityNotExists();
            }
        });
    }

    private void handleFacilityExists(DocumentSnapshot facilitySnapshot) {
        String facilityName = facilitySnapshot.getString("facilityName");
        if (facilityName != null && !facilityName.isEmpty()) {
            facilityCreated.setText(facilityName);
            facilityCreated.setVisibility(View.VISIBLE);
            Facility existingFacility = facilitySnapshot.toObject(Facility.class);
            if (existingFacility != null) {
                existingFacility.setFacilityId(facilitySnapshot.getId());
                existingFacility.setFacilityName(facilityName);
            }
            facilityViewModel.setFacilityStatus(true);
            facilityViewModel.setCurrentFacility(existingFacility);
        } else {
            facilityCreated.setVisibility(View.GONE);
            facilityViewModel.setFacilityStatus(false);
        }
    }

    private void handleFacilityNotExists() {
        facilityCreated.setVisibility(View.GONE);
        createFacilitiesButton.setVisibility(View.VISIBLE);
        facilityViewModel.setFacilityStatus(false);
    }

    private void handleMissingFacilityID() {
        Log.w("Firestore", "No facilityID found for this organizer.");
        facilityCreated.setVisibility(View.GONE);
        createFacilitiesButton.setVisibility(View.VISIBLE);
        facilityViewModel.setFacilityStatus(false);
    }


    /**
     * This method is called when the user deletes an existing facility.
     * It deletes the facility from the Firestore database.
     */
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentManageMyEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * This method is called when the user deletes an existing facility.
     * It deletes the facility from the Firestore database.
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventList = new ArrayList<>(); // Initialize the event list
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events"); // Reference to events collection
        facilitiesRef = db.collection("facilities");
        organizersRef = db.collection("organizers");
        myEventsListView = binding.myEventsListView;
        organizerEventArrayAdapter = new OrganizerEventArrayAdapter(getContext(), eventList, false);
        myEventsListView.setAdapter(organizerEventArrayAdapter);

        createFacilitiesButton = view.findViewById(R.id.create_facility_button);
        facilityCreated = view.findViewById(R.id.facility_created);

        deviceId = Settings.Secure.getString(requireActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        facilityViewModel = new ViewModelProvider(requireActivity()).get(FacilityViewModel.class);

//        /// Listen for real-time updates to the organizer's document so we can get the events in real-time
        organizersRef.document(deviceId).addSnapshotListener((organizerSnapshot, error) -> {
            if (error != null) {
                Log.w("Firestore", "Listen failed.", error);
                return;
            }

            if (organizerSnapshot != null && organizerSnapshot.exists()) {
                List<String> eventIDs = (List<String>) organizerSnapshot.get("events");

                if (eventIDs != null && !eventIDs.isEmpty()) {
                    eventList.clear(); // Clear the current list to avoid duplicates

                    for (String eventID : eventIDs) {
                        // Add a real-time listener for each event document
                        eventsRef.document(eventID).addSnapshotListener((eventSnapshot, eventError) -> {
                            if (eventError != null) {
                                Log.w("Firestore", "Listen failed for event: " + eventID, eventError);
                                return;
                            }

                            if (eventSnapshot != null && eventSnapshot.exists()) {
                                Event updatedEvent = eventSnapshot.toObject(Event.class);
                                if (updatedEvent != null) {
                                    // Check if the event already exists in the list
                                    boolean eventExists = false;
                                    for (int i = 0; i < eventList.size(); i++) {
                                        if (eventList.get(i).getEventID().equals(updatedEvent.getEventID())) {
                                            // Update the existing event in the list
                                            eventList.set(i, updatedEvent);
                                            eventExists = true;
                                            break;
                                        }
                                    }
                                    // If the event is new, add it to the list
                                    if (!eventExists) {
                                        eventList.add(updatedEvent);
                                    }

                                    // Notify the adapter of the changes
                                    organizerEventArrayAdapter.notifyDataSetChanged();
                                }
                            } else {
                                Log.w("Firestore", "Event document does not exist: " + eventID);
                            }
                        });
                    }
                } else {
                    // Handle the case where no events are associated with this organizer
                    eventList.clear();
                    organizerEventArrayAdapter.notifyDataSetChanged();
                }
            } else {
                Log.w("Firestore", "Organizer document does not exist or event list missing.");
                eventList.clear();
                organizerEventArrayAdapter.notifyDataSetChanged();
            }
        });

        fetchOrganizerEvents(deviceId);
        fetchOrganizerFacility(deviceId);

        if (existingFacility == null) {
            createFacilitiesButton.setVisibility(View.GONE);
        } else {
            createFacilitiesButton.setVisibility(View.VISIBLE);
        }

        createFacilitiesButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SlacksLottoEventUserInfo", MODE_PRIVATE);
                boolean isSignedUp = sharedPreferences.getBoolean("isSignedUp", false);

                if (isSignedUp) {
                    // Referenced ChatGPT with prompt "How to show a dialog within a fragment" on Oct 29, 2024
                    // License: OpenAI
                    new AddFacilityFragment().show(getChildFragmentManager(), "Add Facility");
                } else {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Sign-Up Required")
                            .setMessage("In order to create a facility, we need to collect some information about you.")
                            .setPositiveButton("Proceed", (dialog, which) -> {
                                Intent signUpIntent = new Intent(requireContext(), SignUpActivity.class);
                                startActivity(signUpIntent);
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .show();
                }
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
