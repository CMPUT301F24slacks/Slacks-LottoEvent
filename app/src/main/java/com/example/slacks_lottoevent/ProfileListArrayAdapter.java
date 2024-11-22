package com.example.slacks_lottoevent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Custom ArrayAdapter for displaying profiles in a ListView.
 */
public class ProfileListArrayAdapter extends ArrayAdapter<Profile> {
    private final Context context;
    private final FirebaseFirestore db;

    /**
     * Constructor for the ArrayAdapter.
     *
     * @param context  The current context.
     * @param profiles The list of profiles to display.
     */
    public ProfileListArrayAdapter(@NonNull Context context, ArrayList<Profile> profiles) {
        super(context, 0, profiles);
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
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

            ImageButton ovalButton = convertView.findViewById(R.id.oval_rectangle);
            ovalButton.setOnClickListener(v -> showProfileOptionsDialog(profile, position));

            // Set OnClickListener to show profile options in a dialog
            convertView.setOnClickListener(v -> showProfileOptionsDialog(profile, position));
        }

        return convertView;
    }

    /**
     * Shows a dialog with "Cancel" and "Delete" options for a profile.
     *
     * @param profile  The profile to display options for.
     * @param position The position of the profile in the list.
     */
    private void showProfileOptionsDialog(Profile profile, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Build the message with profile details
        String message = "Name: " + profile.getName() + "\n" +
                "Email: " + profile.getEmail() + "\n" +
                "Phone: " + profile.getPhone();

        builder.setTitle("Profile Details")
                .setMessage(message)
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Call method to delete profile
                    deleteProfileFromDatabase(profile, position);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        builder.create().show();
    }

    /**
     * Deletes the profile from Firestore and updates the list.
     *
     * @param profile  The profile to delete.
     * @param position The position of the profile in the list.
     */
    private void deleteProfileFromDatabase(Profile profile, int position) {
        // Assume the profile's document ID is the same as the email
        db.collection("profiles").document(profile.getDeviceId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Remove profile from the list and notify the adapter
                    remove(getItem(position));
                    //delete cascade for their facility, events, and as an entrant, and as an organizer
                    notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Log error or show a toast
                    e.printStackTrace();
                });
    }
}
