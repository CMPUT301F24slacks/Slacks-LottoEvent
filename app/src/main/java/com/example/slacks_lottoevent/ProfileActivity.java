package com.example.slacks_lottoevent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PICK_IMAGE = 1001; // Unique request code

    private FirebaseFirestore db;
    private CollectionReference profilesRef;
    private Profile profile;
    private String userId;

    // UI elements
    private TextView usernameText;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private ImageView profilePhoto;
    private ImageView backButton;
    private ImageView notificationsIcon;
    private Switch notificationsSwitch;
    private Button editProfileButton;
    private Button editPictureButton;

    private Button confirmButton;
    private Button cancelButton;

    /**
     * OnCreate method for the ProfileActivity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        userId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        db = FirebaseFirestore.getInstance();
        profilesRef = db.collection("profiles");

        // Initialize UI elements
        usernameText = findViewById(R.id.username);
        nameEditText = findViewById(R.id.edit_name);
        emailEditText = findViewById(R.id.edit_email);
        phoneEditText = findViewById(R.id.edit_phone);
        profilePhoto = findViewById(R.id.profile_image);
        backButton = findViewById(R.id.back_button);
        notificationsIcon = findViewById(R.id.notifications_icon);
        notificationsSwitch = findViewById(R.id.switch_notifications);
        editProfileButton = findViewById(R.id.btn_edit_profile);
        editPictureButton= findViewById(R.id.btn_edit_picture);
        confirmButton = findViewById(R.id.btn_confirm);
        cancelButton = findViewById(R.id.btn_cancel);

        // Initially hide the confirm and cancel buttons
        confirmButton.setVisibility(Button.GONE);
        cancelButton.setVisibility(Button.GONE);

        // Set editability of the fields to false initially
        setFieldsEditable(false);

        // Load the profile from Firestore
        profilesRef.document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                profile = task.getResult().toObject(Profile.class);

                if (profile == null) {
                    // Create a default profile if none exists
                    profile = new Profile("User", "", "", this);
                    profilesRef.document(userId).set(profile);
                }

                // Update the UI after profile is loaded
                updateUIWithProfile();
            } else {
                // Handle Firestore error (optional)
                profile = new Profile("User", "", "", this);
                profilesRef.document(userId).set(profile);
                updateUIWithProfile();
            }
        });

        // Back button listener
        backButton.setOnClickListener(v -> finish());

        // Notifications switch listener
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            profile.setAdminNotifications(isChecked);
            profilesRef.document(userId).set(profile);

            // Update the notification icon
            if (isChecked) {
                notificationsIcon.setImageResource(R.drawable.baseline_notifications_active_24);
            } else {
                notificationsIcon.setImageResource(R.drawable.baseline_notifications_off_24);
            }
        });

        // Edit button listener
        editProfileButton.setOnClickListener(v -> {
            setFieldsEditable(true);
            editProfileButton.setVisibility(Button.GONE);
            confirmButton.setVisibility(Button.VISIBLE);
            cancelButton.setVisibility(Button.VISIBLE);
        });

        // Edit picture button listener
        // TODO: FIX THE EDIT PICTURE BUTTON
        //editPictureButton.setOnClickListener(v -> showEditPictureDialog());

        // Confirm button listener
        confirmButton.setOnClickListener(v -> {
            // Validate inputs before saving changes
            if (!validateInputs()) return;

            // Save changes to the profile
            profile.setName(nameEditText.getText().toString().trim(), getApplicationContext());
            profile.setEmail(emailEditText.getText().toString().trim());
            profile.setPhone(phoneEditText.getText().toString().trim());

            // Update the UI, including username and profile picture
            updateUIWithProfile();

            // Update the UI for buttons and editability
            setFieldsEditable(false);
            editProfileButton.setVisibility(Button.VISIBLE);
            confirmButton.setVisibility(Button.GONE);
            cancelButton.setVisibility(Button.GONE);

            // Save changes to Firestore
            profilesRef.document(userId).set(profile)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        updateUIWithProfile(); // Refresh UI
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ProfileActivity", "Failed to update profile: " + e.getMessage());
                        Toast.makeText(this, "Failed to save changes. Please try again.", Toast.LENGTH_SHORT).show();
                    });
        });


        // Cancel button listener
        cancelButton.setOnClickListener(v -> {
            // Revert changes
            updateUIWithProfile();

            setFieldsEditable(false);
            editProfileButton.setVisibility(Button.VISIBLE);
            confirmButton.setVisibility(Button.GONE);
            cancelButton.setVisibility(Button.GONE);
        });
    }

    /**
     * Method to update the profile picture
     * @param imageUri
     */
    private void updateProfilePicture(Uri imageUri) {
        String imagePath = imageUri.toString();
        profile.setProfilePicturePath(imagePath);
        profilesRef.document(userId).update("profilePicturePath", imagePath)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show();
                    Log.d("ProfileActivity", "Profile picture updated in Firestore.");
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileActivity", "Failed to update profile picture: " + e.getMessage());
                    Toast.makeText(this, "Failed to save changes. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Method to handle the result of the image picker
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            // Update the ImageView in the dialog
            ImageView selectedImageView = (ImageView) findViewById(R.id.selected_image_view).getTag(R.id.selected_image_view);
            selectedImageView.setImageURI(selectedImageUri);
            selectedImageView.setTag(selectedImageUri); // Store URI in the tag for confirmation
        }
    }

    /**
     * Method to open the image picker
     * @param selectedImageView
     */
    private void openImagePicker(ImageView selectedImageView) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);

        // Save the selected ImageView for later use in onActivityResult
        selectedImageView.setTag(R.id.selected_image_view, selectedImageView);
    }

    /**
     * Method to show the edit picture dialog
     */
    private void showEditPictureDialog() {
        // Create and configure the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_picture, null);
        builder.setView(dialogView);

        // Dialog UI elements
        ImageView selectedImageView = dialogView.findViewById(R.id.selected_image_view);
        Button uploadButton = dialogView.findViewById(R.id.upload_button);
        Button confirmButton = dialogView.findViewById(R.id.confirm_button);
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);

        AlertDialog dialog = builder.create();

        // Upload button logic
        uploadButton.setOnClickListener(view -> openImagePicker(selectedImageView));

        // Confirm button logic
        confirmButton.setOnClickListener(v -> {
            // Validate inputs
            if (!validateInputs()) return;

            // Update profile object
            profile.setName(nameEditText.getText().toString().trim(), getApplicationContext());
            profile.setEmail(emailEditText.getText().toString().trim());
            profile.setPhone(phoneEditText.getText().toString().trim());

            // Save changes to Firestore
            profilesRef.document(userId).set(profile)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        updateUIWithProfile(); // Refresh UI
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ProfileActivity", "Failed to update profile: " + e.getMessage());
                        Toast.makeText(this, "Failed to save changes. Please try again.", Toast.LENGTH_SHORT).show();
                    });

            // Reset UI state
            setFieldsEditable(false);
            editProfileButton.setVisibility(Button.VISIBLE);
            confirmButton.setVisibility(Button.GONE);
            cancelButton.setVisibility(Button.GONE);
        });


        // Cancel button logic
        cancelButton.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }


    /**
     * Method to validate the inputs
     * @return
     */
    private boolean validateInputs() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Name is required");
            nameEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email");
            emailEditText.requestFocus();
            return false;
        }

        if (!TextUtils.isEmpty(phone) && !Patterns.PHONE.matcher(phone).matches()) {
            phoneEditText.setError("Phone number should only contain numbers");
            phoneEditText.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Method to update the UI with the profile
     */
    private void updateUIWithProfile() {
        usernameText.setText(profile.getName());
        nameEditText.setText(profile.getName());
        emailEditText.setText(profile.getEmail());
        phoneEditText.setText(profile.getPhone());
        profilePhoto.setImageURI(Uri.parse(profile.getProfilePicturePath()));
        notificationsSwitch.setChecked(profile.getAdminNotifications());
    }

    /**
     * Method to set the fields editable
     * @param isEditable
     */
    private void setFieldsEditable(boolean isEditable) {
        nameEditText.setEnabled(isEditable);
        emailEditText.setEnabled(isEditable);
        phoneEditText.setEnabled(isEditable);
    }
}