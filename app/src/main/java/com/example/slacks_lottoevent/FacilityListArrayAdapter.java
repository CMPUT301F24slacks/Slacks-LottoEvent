package com.example.slacks_lottoevent;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom ArrayAdapter for displaying profiles in a ListView.
 */
public class FacilityListArrayAdapter extends ArrayAdapter<Facility> {
    private final Context context;
    private final FirebaseFirestore db;
    private boolean isAdmin;
    private OrganizerEventArrayAdapter eventsAdapter; // Reference to the events adapter
    private ArrayList<Event> eventList; // Shared list of events


    /**
     * Constructor for the ArrayAdapter.
     *
     * @param context    The current context.
     * @param facilities The list of profiles to display.
     */
    public FacilityListArrayAdapter(@NonNull Context context, ArrayList<Facility> facilities, boolean isAdmin,
                                    OrganizerEventArrayAdapter eventsAdapter, ArrayList<Event> eventList) {
        super(context, 0, facilities);
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.isAdmin = isAdmin;
        this.eventsAdapter = eventsAdapter;
        this.eventList = eventList;
    }

    /**
     * Get the view for a single profile.
     *
     * @param position    The position of the profile in the list.
     * @param convertView The view to reuse.
     * @param parent      The parent view.
     * @return The view for the profile.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate the view if it doesn't already exist
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.content_admin_profiles, parent, false);
        }
        if (eventsAdapter == null){
            eventsAdapter = new OrganizerEventArrayAdapter(context, eventList, true);
        }

        // Get the profile at the current position
        Facility facility = getItem(position);

        if (facility != null) {
            // Set the profile name
            TextView facilityName = convertView.findViewById(R.id.admin_profile_name);
            facilityName.setText(facility.getFacilityName());

            // Customize view based on "check" value
            ImageView userIcon = convertView.findViewById(R.id.userIcon);
            userIcon.setVisibility(View.GONE); // Hide the location icon

            if (isAdmin) {
                ImageButton ovalButton = convertView.findViewById(R.id.oval_rectangle);
                ovalButton.setOnClickListener(v -> showFacilityOptionsDialog(context, db, facility, eventsAdapter, this, false));

                // Set OnClickListener to show profile options in a dialog
                convertView.setOnClickListener(v -> showFacilityOptionsDialog(context, db, facility, eventsAdapter, this, false));
            }
        }

        return convertView;
    }

    /**
     * Shows a dialog with "Cancel" and "Delete" options for a profile.
     *
     * @param facility The profile to display options for.
//     * @param position The position of the profile in the list.
     */
    public static void showFacilityOptionsDialog(Context context, FirebaseFirestore db, Facility facility,
                                                  OrganizerEventArrayAdapter eventsAdapter, FacilityListArrayAdapter facilitiesAdapter, boolean FromProfile) {
        if (!FromProfile){
            // Build the message with facility details
            String message = "Name: " + facility.getFacilityName() + "\n" +
                    "Address: " + facility.getStreetAddress1();

            AdminActivity.showAdminAlertDialog(context, () ->
                            AdminActivity.showAdminAlertDialog(context,
                                    () -> deleteFacilityFromDatabase(context, db, facility.getFacilityId(), facility.getDeviceId(),
                                            eventsAdapter, facilitiesAdapter), // Reference to the events adapter
                                    "Confirm Deletion", "Are you sure you want to delete this profile?",
                                    "WARNING: DELETION CANNOT BE UNDONE",
                                    "Cancel", "Confirm", null),
                    "Facility Details", message, "WARNING: DELETION CANNOT BE UNDONE",
                    "Cancel", "Delete", null);
        }
        else{
            deleteFacilityFromDatabase(context, db, facility.getFacilityId(), facility.getDeviceId(),
                eventsAdapter, facilitiesAdapter);
        }


    }


    /**
     * Deletes the facility from Firestore and updates the list.
     *
     * @param context  The context for Toast messages.
     * @param db       The Firestore database instance.
//     * @param FacilityId The facility Id to delete.
//     * @param OrganizerId The facility's organizer Id to be deleted.
     */
    public static void deleteFacilityFromDatabase(Context context, FirebaseFirestore db, String facilityId, String organizerId,
                                                  OrganizerEventArrayAdapter eventsAdapter, FacilityListArrayAdapter facilitiesAdapter) {
        db.collection("facilities").document(facilityId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Facility deleted successfully.", Toast.LENGTH_SHORT).show();

                    // Fetch the organizer's events and update the list
                    db.collection("organizers").document(organizerId).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    // Get the events array from the organizer document
                                    List<String> events = (List<String>) documentSnapshot.get("events");
                                    if (events != null) {
                                        for (String eventId : events) {
                                            // Remove event from shared list
//                                            eventList.removeIf(event -> event.getEventID().equals(eventId));
//                                          Delete each event document explicitly
                                            OrganizerEventDetailsActivity.DeletingEvent(context, eventId, db,
                                                () -> Toast.makeText(context, "Event deleted successfully.", Toast.LENGTH_SHORT).show(),
                                                () -> Toast.makeText(context, "Failed to delete event.", Toast.LENGTH_SHORT).show(),
                                                    true
                                            );
                                        }

                                        // Delete the organizer document itself
                                        db.collection("organizers").document(organizerId).delete()
                                                .addOnSuccessListener(success -> {
                                                    Toast.makeText(context, "Organizer data deleted successfully.", Toast.LENGTH_SHORT).show();

                                                    // Notify the adapter to refresh the UI
                                                    if (facilitiesAdapter != null) {
                                                        facilitiesAdapter.notifyDataSetChanged();
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(context, "Failed to delete organizer data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    e.printStackTrace();
                                                });

                                        db.collection("facilities").document(facilityId).delete()
                                                .addOnSuccessListener(success -> { // Rename 'aVoid' to 'success'
                                                    // Successfully deleted the facility
                                                    Toast.makeText(context, "Facility deleted successfully.", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    // Handle failure
                                                    Toast.makeText(context, "Failed to delete facility: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    Log.e("Firestore", "Error deleting facility: ", e);
                                                });


                                        // Notify the adapter to refresh the UI
                                        if (eventsAdapter != null) {
                                            eventsAdapter.notifyDataSetChanged();
                                        }
                                        }
                                    }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Failed to fetch organizer details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete facility: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
