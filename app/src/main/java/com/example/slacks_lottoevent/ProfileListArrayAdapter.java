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

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.zxing.WriterException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Custom ArrayAdapter for displaying profiles in a ListView.
 */
public class ProfileListArrayAdapter extends ArrayAdapter<Profile> {
    private final Context context;
    private final FirebaseFirestore db;
    private boolean isAdmin;

    private ArrayList<Event> eventList; // Shared list of events
    private OrganizerEventArrayAdapter eventsAdapter; // Reference to the events adapter

    private ArrayList<Facility> facilities; // Shared list of facilities
    private FacilityListArrayAdapter facilitiesAdapter; // Reference to the facilities adapter

    /**
     * Constructor for the ArrayAdapter.
     *
     * @param context          The current context.
     * @param profiles         The list of profiles to display.
     * @param isAdmin          Boolean flag for admin functionality.
     * @param facilities       Shared list of facilities.
     * @param facilitiesAdapter Adapter for facility views.
     * @param eventList        Shared list of events.
     * @param eventsAdapter    Adapter for event views.
     */
    public ProfileListArrayAdapter(@NonNull Context context, ArrayList<Profile> profiles, boolean isAdmin,
                                   ArrayList<Facility> facilities, FacilityListArrayAdapter facilitiesAdapter,
                                   ArrayList<Event> eventList, OrganizerEventArrayAdapter eventsAdapter) {
        super(context, 0, profiles);
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.isAdmin = isAdmin;
        this.facilities = facilities;
        this.facilitiesAdapter = facilitiesAdapter;
        this.eventList = eventList;
        this.eventsAdapter = eventsAdapter;
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
        if (facilitiesAdapter == null){
            facilitiesAdapter = new FacilityListArrayAdapter(context, facilities, isAdmin, eventsAdapter, eventList);
        }
        // Get the profile at the current position
        Profile profile = getItem(position);

        if (profile != null) {
            // Set the profile name
            TextView profileName = convertView.findViewById(R.id.admin_profile_name);
            profileName.setText(profile.getName());

            // Customize view based on "check" value
            ImageView locationIcon = convertView.findViewById(R.id.location_icon);
            locationIcon.setVisibility(View.GONE); // Hide the location icon

            // If user is an admin, enable additional functionality
            if (isAdmin) {
                ImageButton ovalButton = convertView.findViewById(R.id.oval_rectangle);

                // Set OnClickListener for the oval button
                ovalButton.setOnClickListener(v -> showProfileOptionsDialog(profile));

                // Set OnClickListener for the entire view
                convertView.setOnClickListener(v -> showProfileOptionsDialog(profile));
            }
        }

        return convertView;
    }


    /**
     * Shows a dialog with "Cancel" and "Delete" options for a profile.
     *
     * @param profile The profile to display options for.
     */
    private void showProfileOptionsDialog(Profile profile) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Build the message with profile details
        String message = "Name: " + profile.getName() + "\n" +
                "Email: " + profile.getEmail();

        if (!profile.getUsingDefaultPicture()) {
            // Show the profile picture for custom profiles
            AdminActivity.showAdminAlertDialog(context, () ->
                            AdminActivity.showAdminAlertDialog(context,
                                    () -> deleteProfileFromDatabase(context, db, profile, facilitiesAdapter, eventsAdapter),
                                    "Confirm Deletion", "Are you sure you want to delete this profile?",
                                    "WARNING: DELETION CANNOT BE UNDONE",
                                    "Cancel", "Confirm", null),
                    "Profile Details", message, "WARNING: DELETION CANNOT BE UNDONE",
                    "Cancel", "Delete", profile.getProfilePicturePath());
        } else {
            // Show the name and email directly for default profiles
            AdminActivity.showAdminAlertDialog(context, () ->
                    AdminActivity.showAdminAlertDialog(context,
                            () -> deleteProfileFromDatabase(context, db, profile, facilitiesAdapter, eventsAdapter),
                            "Confirm Deletion", "Are you sure you want to delete this profile?",
                            "WARNING: DELETION CANNOT BE UNDONE",
                            "Cancel", "Confirm", null),
                    "Profile Details", message, "WARNING: DELETION CANNOT BE UNDONE",
                    "Cancel", "Delete", null);
        }
    }

    /**
     * Deletes the profile from Firestore and calls appropriate callbacks.
     *
     * @param context   The context for Toast messages.
     * @param db        The Firestore instance.
//     * @param deviceId  The device ID of the profile to delete.
     */
    public static void deleteProfileFromDatabase(Context context, FirebaseFirestore db, Profile profile,
                                                 FacilityListArrayAdapter facilitiesAdapter,
                                                 OrganizerEventArrayAdapter eventsAdapter) {
        // Has Profile Picture
        if (!profile.getUsingDefaultPicture()) {
            AdminImagesAdapter.deleteImageFromFirestore(context, db, profile.getProfilePicturePath(), false);
        }

        // Delete entrant-related data
        deleteProfileFromDatabaseEntrant(context, db, profile.getDeviceId());

        // Delete organizer-related data
        deleteProfileFromDatabaseOrganizer(context, db, profile.getDeviceId(), true, facilitiesAdapter, eventsAdapter);

        // Delete the profile document from the "profiles" collection
        db.collection("profiles").document(profile.getDeviceId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Call the success callback
                    Toast.makeText(context, "Profile deleted successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Call the failure callback
                    Toast.makeText(context, "Failed to delete profile.", Toast.LENGTH_SHORT).show();
                });
    }


    /**
     * Deletes data associated with the profile from the "entrants" collection.
     */
    private static void deleteProfileFromDatabaseEntrant(Context context, FirebaseFirestore db, String deviceId) {
        db.collection("entrants").document(deviceId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Fetch event lists from the entrant document
                        ArrayList<String> waitlistedEvents = (ArrayList<String>) documentSnapshot.get("waitlistedEvents");
                        ArrayList<String> selectedEvents = (ArrayList<String>) documentSnapshot.get("invitedEvents");
                        ArrayList<String> finalistEvents = (ArrayList<String>) documentSnapshot.get("finalistEvents");
                        ArrayList<String> cancelledEvents = (ArrayList<String>) documentSnapshot.get("uninvitedEvents");

                        // Remove the entrant from associated events
                        EntrantRemovalWaitlisted(db, waitlistedEvents, deviceId);
                        EntrantRemovalSelected(db, selectedEvents, deviceId);
                        EntrantRemovalFinalist(db, finalistEvents, deviceId);
                        EntrantRemovalCancelled(db, cancelledEvents, deviceId);

                        // Delete the entrant document itself
                        db.collection("entrants").document(deviceId).delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Entrant data deleted successfully.", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to delete entrant data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                });
                    } else {
                        Toast.makeText(context, "Entrant profile not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to fetch entrant profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    /**
     * Deletes data associated with the profile from the "organizers" collection.
     */
    private static void deleteProfileFromDatabaseOrganizer(Context context, FirebaseFirestore db, String deviceId, boolean FromProfile,
                                                           FacilityListArrayAdapter facilitiesAdapter,
                                                           OrganizerEventArrayAdapter eventsAdapter) {
        db.collection("organizers").document(deviceId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Fetch the facility ID from the organizer document
                        Facility facility = documentSnapshot.toObject(Facility.class);

                        if (facility.getFacilityId() != null && !facility.getFacilityId().isEmpty()) {
                            // Call method to delete the facility
                            FacilityListArrayAdapter.showFacilityOptionsDialog(context, db, facility, eventsAdapter, facilitiesAdapter, FromProfile);
                        } else {
                            Toast.makeText(context, "No associated facility found for organizer.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Organizer profile not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to fetch organizer profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }


    public static void EntrantRemovalWaitlisted(FirebaseFirestore db, ArrayList<String> waitlistedEvents, String deviceId) {
        if (waitlistedEvents == null || waitlistedEvents.isEmpty()) {
            return; // No events to process
        }

        for (String eventID : waitlistedEvents) {
            // Remove deviceId from "waitlisted", "waitlistedNotificationsList", and "reselected"
            db.collection("events").document(eventID)
                    .update(
                            "waitlisted", FieldValue.arrayRemove(deviceId),
                            "waitlistedNotificationsList", FieldValue.arrayRemove(deviceId),
                            "reselected", FieldValue.arrayRemove(deviceId)
                    )
                    .addOnSuccessListener(aVoid -> {
                        System.out.println("Device ID removed from event lists: " + eventID);

                        // Remove deviceId from "joinLocations"
                        db.collection("events").document(eventID).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        List<Map<String, List<Double>>> joinLocations =
                                                (List<Map<String, List<Double>>>) documentSnapshot.get("joinLocations");

                                        if (joinLocations != null) {
                                            // Filter out entries with the deviceId
                                            List<Map<String, List<Double>>> updatedJoinLocations = new ArrayList<>();
                                            for (Map<String, List<Double>> entry : joinLocations) {
                                                if (!entry.containsKey(deviceId)) {
                                                    updatedJoinLocations.add(entry); // Keep entries not matching the deviceId
                                                }
                                            }

                                            // Update Firestore with the filtered joinLocations
                                            db.collection("events").document(eventID)
                                                    .update("joinLocations", updatedJoinLocations)
                                                    .addOnSuccessListener(aVoid2 -> System.out.println("Device ID removed from joinLocations: " + eventID))
                                                    .addOnFailureListener(e -> System.err.println("Failed to update joinLocations for event: " + eventID + " - " + e.getMessage()));
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> System.err.println("Failed to fetch joinLocations for event: " + eventID + " - " + e.getMessage()));
                    })
                    .addOnFailureListener(e -> System.err.println("Failed to remove Device ID from event lists: " + eventID + " - " + e.getMessage()));
        }
    }

    public static void EntrantRemovalSelected(FirebaseFirestore db, ArrayList<String> SelectedEvents, String deviceId) {
        if (SelectedEvents == null || SelectedEvents.isEmpty()) {
            return; // No events to process
        }

        for (String eventID : SelectedEvents) {
            // Remove deviceId from "waitlisted", "waitlistedNotificationsList", and "reselected"
            db.collection("events").document(eventID)
                    .update(
                            "selected", FieldValue.arrayRemove(deviceId),
                            "selectedNotificationsList", FieldValue.arrayRemove(deviceId)
                    )
                    .addOnSuccessListener(aVoid -> {
                        System.out.println("Device ID removed from event lists: " + eventID);

                        // Remove deviceId from "joinLocations"
                        db.collection("events").document(eventID).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        List<Map<String, List<Double>>> joinLocations =
                                                (List<Map<String, List<Double>>>) documentSnapshot.get("joinLocations");

                                        if (joinLocations != null) {
                                            // Filter out entries with the deviceId
                                            List<Map<String, List<Double>>> updatedJoinLocations = new ArrayList<>();
                                            for (Map<String, List<Double>> entry : joinLocations) {
                                                if (!entry.containsKey(deviceId)) {
                                                    updatedJoinLocations.add(entry); // Keep entries not matching the deviceId
                                                }
                                            }

                                            // Update Firestore with the filtered joinLocations
                                            db.collection("events").document(eventID)
                                                    .update("joinLocations", updatedJoinLocations)
                                                    .addOnSuccessListener(aVoid2 -> System.out.println("Device ID removed from joinLocations: " + eventID))
                                                    .addOnFailureListener(e -> System.err.println("Failed to update joinLocations for event: " + eventID + " - " + e.getMessage()));
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> System.err.println("Failed to fetch joinLocations for event: " + eventID + " - " + e.getMessage()));
                    })
                    .addOnFailureListener(e -> System.err.println("Failed to remove Device ID from event lists: " + eventID + " - " + e.getMessage()));
        }
    }

    public static void EntrantRemovalFinalist(FirebaseFirestore db, ArrayList<String> FinalistEvents, String deviceId) {
        if (FinalistEvents == null || FinalistEvents.isEmpty()) {
            return; // No events to process
        }

        for (String eventID : FinalistEvents) {
            // Remove deviceId from "waitlisted", "waitlistedNotificationsList", and "reselected"
            db.collection("events").document(eventID)
                    .update(
                            "finalists", FieldValue.arrayRemove(deviceId),
                            "joinedNotificationsList", FieldValue.arrayRemove(deviceId)
                            )
                    .addOnSuccessListener(aVoid -> {
                        System.out.println("Device ID removed from event lists: " + eventID);

                        // Remove deviceId from "joinLocations"
                        db.collection("events").document(eventID).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        List<Map<String, List<Double>>> joinLocations =
                                                (List<Map<String, List<Double>>>) documentSnapshot.get("joinLocations");

                                        if (joinLocations != null) {
                                            // Filter out entries with the deviceId
                                            List<Map<String, List<Double>>> updatedJoinLocations = new ArrayList<>();
                                            for (Map<String, List<Double>> entry : joinLocations) {
                                                if (!entry.containsKey(deviceId)) {
                                                    updatedJoinLocations.add(entry); // Keep entries not matching the deviceId
                                                }
                                            }

                                            // Update Firestore with the filtered joinLocations
                                            db.collection("events").document(eventID)
                                                    .update("joinLocations", updatedJoinLocations)
                                                    .addOnSuccessListener(aVoid2 -> System.out.println("Device ID removed from joinLocations: " + eventID))
                                                    .addOnFailureListener(e -> System.err.println("Failed to update joinLocations for event: " + eventID + " - " + e.getMessage()));
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> System.err.println("Failed to fetch joinLocations for event: " + eventID + " - " + e.getMessage()));
                    })
                    .addOnFailureListener(e -> System.err.println("Failed to remove Device ID from event lists: " + eventID + " - " + e.getMessage()));
        }
    }

    public static void EntrantRemovalCancelled(FirebaseFirestore db, ArrayList<String> CancelledEvents, String deviceId) {
        if (CancelledEvents == null || CancelledEvents.isEmpty()) {
            return; // No events to process
        }

        for (String eventID : CancelledEvents) {
            db.collection("events").document(eventID)
                    .update(
                            "cancelled", FieldValue.arrayRemove(deviceId),
                            "cancelledNotificationsList", FieldValue.arrayRemove(deviceId)
                    )
                    .addOnSuccessListener(aVoid -> {
                        // Successfully removed deviceId from the lists
                        System.out.println("Device ID removed from event: " + eventID);
                    })
                    .addOnFailureListener(e -> {
                        // Log or handle the failure
                        System.err.println("Failed to remove Device ID from event: " + eventID + " - " + e.getMessage());
                    });
        }
    }
}
