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

    /**
     * Constructor for the ArrayAdapter.
     *
     * @param context    The current context.
     * @param facilities The list of profiles to display.
     */
    public FacilityListArrayAdapter(@NonNull Context context, ArrayList<Facility> facilities, boolean isAdmin) {
        super(context, 0, facilities);
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.isAdmin = isAdmin;
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

        builder.setTitle("Facility Details")
                .setMessage(message)
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Call method to delete facility
                    deleteFacilityFromDatabase(this.getContext(), db, facility.getFacilityId(), facility.getOrganizerId());
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
     * @param FacilityId The facility Id to delete.
     * @param OrganizerId The facility's organizer Id to be deleted.
     */
    public static void deleteFacilityFromDatabase(Context context, FirebaseFirestore db, String FacilityId, String OrganizerId) {
        // Add a real-time listener to track the facility document
        db.collection("facilities").document(FacilityId)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        Toast.makeText(context, "Error listening for facility updates: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (documentSnapshot != null && !documentSnapshot.exists()) {
                        // Facility document has been deleted
                        Toast.makeText(context, "Facility deleted successfully (real-time update).", Toast.LENGTH_SHORT).show();
                    }
                });

        // Fetch the organizer's events and delete each event
        db.collection("organizers").document(OrganizerId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get the events array from the organizer document
                        List<String> events = (List<String>) documentSnapshot.get("events");
                        if (events != null) {
                            for (String eventID : events) {
                                // Step 6: Add real-time listener for event updates
                                db.collection("events").document(eventID)
                                        .addSnapshotListener((eventSnapshot, eventError) -> {
                                            if (eventError != null) {
                                                Toast.makeText(context, "Error listening for event updates: " + eventError.getMessage(), Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            if (eventSnapshot != null && !eventSnapshot.exists()) {
                                                // Event document has been deleted
                                                Toast.makeText(context, "Event deleted successfully (real-time update).", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                // Delete each event document explicitly
                                OrganizerEventDetailsActivity.DeletingEvent(context, eventID, db,
                                        () -> Toast.makeText(context, "Event deleted successfully.", Toast.LENGTH_SHORT).show(),
                                        () -> Toast.makeText(context, "Failed to delete event.", Toast.LENGTH_SHORT).show()
                                );
                            }
                        }

                        // Delete the organizer document explicitly
                        db.collection("organizers").document(OrganizerId).delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Organizer deleted successfully.", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to delete organizer: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to fetch organizer details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
