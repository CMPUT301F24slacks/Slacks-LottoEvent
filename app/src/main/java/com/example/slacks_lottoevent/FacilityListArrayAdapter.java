package com.example.slacks_lottoevent;

import android.content.Context;
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
                ovalButton.setOnClickListener(v -> showFacilityOptionsDialog(facility));

                // Set OnClickListener to show profile options in a dialog
                convertView.setOnClickListener(v -> showFacilityOptionsDialog(facility));
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
    private void showFacilityOptionsDialog(Facility facility) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Build the message with facility details
        String message = "Name: " + facility.getFacilityName() + "\n" +
                "Address: " + facility.getStreetAddress1();
//                + "OrganizerID " + facility.getOrganizerID()
//                + "FacilityID " + facility.getFacilityId();

        builder.setTitle("Facility Details")
                .setMessage(message)
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Call method to delete facility
                    deleteFacilityFromDatabase(
                            context,
                            db,
                            facility.getFacilityId(),
                            facility.getOrganizerID(),
                            eventsAdapter); // Reference to the events adapter

                    // Explicitly delete the facility document
                    db.collection("facilities").document(facility.getFacilityId()).delete()
                            .addOnSuccessListener(aVoid -> {
                                // Remove the facility from the adapter and notify the dataset change
                                remove(facility);
                                notifyDataSetChanged();
                                Toast.makeText(context, "Facility deleted successfully.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Failed to delete facility: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        builder.create().show();
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
                                                  OrganizerEventArrayAdapter eventsAdapter) {
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
