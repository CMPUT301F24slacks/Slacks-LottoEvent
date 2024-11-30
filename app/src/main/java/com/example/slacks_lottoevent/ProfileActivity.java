package com.example.slacks_lottoevent;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;

    /**
     * OnCreate method for the ProfileActivity
     *
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
        editPictureButton = findViewById(R.id.btn_edit_picture);
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
                    profile = new Profile("User", "", "", userId, this);
                    profilesRef.document(userId).set(profile);
                }

                // Update the UI after profile is loaded
                updateUIWithProfile();
            } else {
                // Handle Firestore error (optional)
                profile = new Profile("User", "", "", userId, this);
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
        editPictureButton.setOnClickListener(v -> {
            selectImage();
        });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null && data.getData() != null) {
                                selectedImageUri = data.getData();
                            }
                            if (selectedImageUri != null) {
                                editImage(selectedImageUri);
                            }
                        }
                    }

                });

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
                           Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT)
                                .show();
                           updateUIWithProfile(); // Refresh UI
                       })
                       .addOnFailureListener(e -> {
                           Log.e("ProfileActivity", "Failed to update profile: " + e.getMessage());
                           Toast.makeText(this, "Failed to save changes. Please try again.",
                                          Toast.LENGTH_SHORT).show();
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

    private void refreshUI(String newImageUrl) {
        Glide.with(this).load(newImageUrl).into(profilePhoto); // Update your ImageView
        Toast.makeText(this, "Image updated successfully", Toast.LENGTH_SHORT).show();
    }

    private void updateFirestoreWithNewImage(String newImageUrl, Callback<Boolean> callback) {
        profilesRef.document(profile.getDeviceId()).update("profilePicturePath", newImageUrl)
                   .addOnSuccessListener(aVoid -> {
                       profile.setProfilePicturePath(newImageUrl);
                       profile.setUsingDefaultPicture(false);
                       callback.onComplete(true);
                   })
                   .addOnFailureListener(e -> {
                       Log.e("ProfileActivity", "Failed to update Firestore with new image URL", e);
                       callback.onComplete(false);
                   });
    }

    private void editImage(Uri newImageUri) {
        // Step 1: Delete the old image from Google Cloud Storage
        deleteOldImage(profile.getProfilePicturePath(), () -> {
            // Step 2: Upload the new image
            uploadNewImage(newImageUri, newImageUrl -> {
                // Step 3: Update Firestore with the new image URL
                updateFirestoreWithNewImage(newImageUrl, success -> {
                    if (success) {
                        refreshUI(newImageUrl); // Update the UI
                    } else {
                        Toast.makeText(this, "Failed to update Firestore", Toast.LENGTH_SHORT)
                             .show();
                    }
                });
            });
        });
    }

    private void uploadNewImage(Uri newImageUri, Callback<String> callback) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        String fileName = formatter.format(now);
        StorageReference storageRef = FirebaseStorage.getInstance()
                                                     .getReference("profile-pictures/" + fileName);

        storageRef.putFile(newImageUri)
                  .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                                                                  .addOnSuccessListener(uri -> {
                                                                      String newImageUrl = uri.toString();
                                                                      callback.onComplete(
                                                                              newImageUrl); // Pass the new URL to the callback
                                                                  })
                                                                  .addOnFailureListener(e -> {
                                                                      Log.e("ProfileActivity",
                                                                            "Failed to get download URL",
                                                                            e);
                                                                      callback.onComplete(null);
                                                                  }))
                  .addOnFailureListener(e -> {
                      Log.e("ProfileActivity", "Failed to upload file", e);
                      callback.onComplete(null);
                  });
    }

    private void deleteOldImage(String oldImageUrl, Runnable onSuccess) {
        if (oldImageUrl != null && (oldImageUrl.startsWith("gs://") || oldImageUrl.startsWith(
                "https://firebasestorage.googleapis.com/"))) {
            StorageReference storageReference = FirebaseStorage.getInstance()
                                                               .getReferenceFromUrl(oldImageUrl);
            storageReference.delete()
                            .addOnSuccessListener(aVoid -> onSuccess.run())
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to delete old image",
                                               Toast.LENGTH_SHORT).show();
                                Log.e("ProfileActivity", "Error deleting old image: ", e);
                            });
        } else {
            Log.w("ProfileActivity",
                  "Old image URL is not a valid Firebase Storage URL. Skipping deletion.");
            onSuccess.run(); // No valid URL to delete, continue with the operation
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickerLauncher.launch(intent);
    }

    /**
     * Method to handle the result of the image picker
     *
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
            ImageView selectedImageView = (ImageView) findViewById(R.id.selected_image_view).getTag(
                    R.id.selected_image_view);
            selectedImageView.setImageURI(selectedImageUri);
            selectedImageView.setTag(selectedImageUri); // Store URI in the tag for confirmation
        }
    }

    /**
     * Method to open the image picker
     *
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
     * Method to validate the inputs
     *
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
        if (profile.getUsingDefaultPicture()) {
            profilePhoto.setImageURI(Uri.parse(profile.getProfilePicturePath()));
        } else {
            Glide.with(this).load(profile.getProfilePicturePath()).into(profilePhoto);
        }
        notificationsSwitch.setChecked(profile.getAdminNotifications());
    }

    /**
     * Method to set the fields editable
     *
     * @param isEditable
     */
    private void setFieldsEditable(boolean isEditable) {
        nameEditText.setEnabled(isEditable);
        emailEditText.setEnabled(isEditable);
        phoneEditText.setEnabled(isEditable);
    }
}