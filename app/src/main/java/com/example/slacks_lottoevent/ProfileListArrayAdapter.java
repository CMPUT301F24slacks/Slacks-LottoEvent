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

/**
 * Custom ArrayAdapter for displaying profiles in a ListView.
 */
public class ProfileListArrayAdapter extends ArrayAdapter<Profile> {
    private final Context context;
    private final FirebaseFirestore db;
    private boolean isAdmin;

    /**
     * Constructor for the ArrayAdapter.
     *
     * @param context  The current context.
     * @param profiles The list of profiles to display.
     */
    public ProfileListArrayAdapter(@NonNull Context context, ArrayList<Profile> profiles, boolean isAdmin) {
        super(context, 0, profiles);
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

        if (!profile.isUsingDefaultPicture()) {
            // Show the profile picture for custom profiles
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_profile_details, null);
            ImageView profilePicture = dialogView.findViewById(R.id.profile_picture);
            Glide.with(context)
                    .load(profile.getProfilePicturePath())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(profilePicture);

            TextView detailsText = dialogView.findViewById(R.id.profile_details_text);
            detailsText.setText(message);

            builder.setView(dialogView);
        } else {
            // Show the name and email directly for default profiles
            builder.setMessage(message);
        }

        // Set Delete button
        builder.setPositiveButton("Delete", (dialog, which) -> {
            // Show a confirmation dialog to avoid accidental deletion
            new AlertDialog.Builder(context)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this profile?")
                    .setPositiveButton("Yes", (confirmDialog, confirmWhich) -> {
                        deleteProfileFromDatabase(context, db, profile);
                        Toast.makeText(context, "Profile deleted successfully.", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (confirmDialog, confirmWhich) -> confirmDialog.dismiss())
                    .create().show();
        });

        // Set Cancel button
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        builder.create().show();
    }

    /**
     * Deletes the profile from Firestore and calls appropriate callbacks.
     *
     * @param context   The context for Toast messages.
     * @param db        The Firestore instance.
//     * @param deviceId  The device ID of the profile to delete.
     */
    public static void deleteProfileFromDatabase(Context context, FirebaseFirestore db, Profile profile) {
//        String name = profile.getName();
//        String email = profile.getEmail();
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("Profile Details");
//
//        String message = "Name: " + (name != null ? name : "N/A") + "\n" +
//                "Email: " + (email != null ? email : "N/A");
//
//        builder.setMessage(message);
//
//        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
//
//        builder.setPositiveButton("Delete", (dialog, which) -> {
            // Has Profile Picture
            if (!profile.isUsingDefaultPicture()) {
                AdminImagesAdapter.deleteImageFromStorageAndFirestore(context, db, profile.getProfilePicturePath(), false);
            }

            // Delete entrant-related data
            deleteProfileFromDatabaseEntrant(context, db, profile.getDeviceId());

            // Delete organizer-related data
            deleteProfileFromDatabaseOrganizer(context, db, profile.getDeviceId());

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
//        }); // Corrected: Added missing closing parenthesis and semicolon for the setPositiveButton block

        // Show the dialog
//        builder.create().show();
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
    private static void deleteProfileFromDatabaseOrganizer(Context context, FirebaseFirestore db, String deviceId) {
        db.collection("organizers").document(deviceId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Fetch the facility ID from the organizer document
                        String facilityId = documentSnapshot.getString("facilityId");
                        if (facilityId != null && !facilityId.isEmpty()) {
                            // Call method to delete the facility
                            FacilityListArrayAdapter.deleteFacilityFromDatabase(context, db, facilityId, deviceId);
                        } else {
                            Toast.makeText(context, "No associated facility found for organizer.", Toast.LENGTH_SHORT).show();
                        }

                        // Delete the organizer document itself
                        db.collection("organizers").document(deviceId).delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Organizer data deleted successfully.", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to delete organizer data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                });
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
            db.collection("events").document(eventID)
                    .update(
                            "waitlisted", FieldValue.arrayRemove(deviceId),
                            "waitlistedNotificationsList", FieldValue.arrayRemove(deviceId),
                            "reselected", FieldValue.arrayRemove(deviceId)
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

    public static void EntrantRemovalSelected(FirebaseFirestore db, ArrayList<String> SelectedEvents, String deviceId) {
        if (SelectedEvents == null || SelectedEvents.isEmpty()) {
            return; // No events to process
        }

        for (String eventID : SelectedEvents) {
            db.collection("events").document(eventID)
                    .update(
                            "selected", FieldValue.arrayRemove(deviceId),
                            "selectedNotificationsList", FieldValue.arrayRemove(deviceId)
//                            "reselected", FieldValue.arrayRemove(deviceId)
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

    public static void EntrantRemovalFinalist(FirebaseFirestore db, ArrayList<String> FinalistEvents, String deviceId) {
        if (FinalistEvents == null || FinalistEvents.isEmpty()) {
            return; // No events to process
        }

        for (String eventID : FinalistEvents) {
            db.collection("events").document(eventID)
                    .update(
                            "finalists", FieldValue.arrayRemove(deviceId),
                            "joinedNotificationsList", FieldValue.arrayRemove(deviceId)
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

    //    private void showingProfilePicture(Profile profile) {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View popupView = inflater.inflate(R.layout.dialog_qr_code, null);
//
//        // Find the ImageView in the popup layout
//        ImageView imageView = popupView.findViewById(R.id.qr_code_image);
//
//        // Set the Profile Picture
//        if (!profile.isUsingDefaultPicture() && profile.getProfilePicturePath() != null && !profile.getProfilePicturePath().isEmpty()) {
//            // Load the custom profile picture using Glide
//            Glide.with(context)
//                    .load(profile.getProfilePicturePath()) // Load the image from the URL
//                    .placeholder(R.drawable.placeholder_image) // Placeholder while loading
//                    .error(R.drawable.error_image) // Error image if the URL fails to load
//                    .into(imageView); // Set into the ImageView
//        } else {
//            // If qrData is null or empty, set a default or error image
//            imageView.setImageResource(R.drawable.error_image);
//        }
//
//        // Create the AlertDialog
//        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
//        builder.setView(popupView);
//
//        // Add the Close button
//        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
//
//        if (!profile.isUsingDefaultPicture()) {
//            builder.setNegativeButton("continue to delete profile", (dialog, which) -> {
//                try {
//                    // Call method to delete profile
//                    deleteProfileFromDatabase(context, db, profile);
////                    Toast.makeText(context, "Profile deleted successfully.", Toast.LENGTH_SHORT).show();
//                } catch (Exception e) {
//                    // Notify the user of the failure
//                    Toast.makeText(context, "Failed to delete profile.", Toast.LENGTH_SHORT).show();
//                    Log.e("ProfileDeletion", "Error deleting profile: ", e);
//                }
//            });
//        }
//
//        // Show the dialog
//        builder.create().show();
//    }

}
