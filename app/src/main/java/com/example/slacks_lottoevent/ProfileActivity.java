package com.example.slacks_lottoevent;

import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {
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
    private Button editButton;
    private Button confirmButton;
    private Button cancelButton;

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
        editButton = findViewById(R.id.btn_edit_profile);
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
        editButton.setOnClickListener(v -> {
            setFieldsEditable(true);
            editButton.setVisibility(Button.GONE);
            confirmButton.setVisibility(Button.VISIBLE);
            cancelButton.setVisibility(Button.VISIBLE);
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
            editButton.setVisibility(Button.VISIBLE);
            confirmButton.setVisibility(Button.GONE);
            cancelButton.setVisibility(Button.GONE);
        });


        // Cancel button listener
        cancelButton.setOnClickListener(v -> {
            // Revert changes
            updateUIWithProfile();

            setFieldsEditable(false);
            editButton.setVisibility(Button.VISIBLE);
            confirmButton.setVisibility(Button.GONE);
            cancelButton.setVisibility(Button.GONE);
        });
    }

    // Method to validate user inputs
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

    // Method to update the UI with profile data
    private void updateUIWithProfile() {
        usernameText.setText(profile.getName());
        nameEditText.setText(profile.getName());
        emailEditText.setText(profile.getEmail());
        phoneEditText.setText(profile.getPhone());
        profilePhoto.setImageURI(Uri.parse(profile.getProfilePicturePath()));
        notificationsSwitch.setChecked(profile.getAdminNotifications());
    }

    // Method to set fields editable or not
    private void setFieldsEditable(boolean isEditable) {
        nameEditText.setEnabled(isEditable);
        emailEditText.setEnabled(isEditable);
        phoneEditText.setEnabled(isEditable);
    }
}
