package com.example.slacks_lottoevent.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.slacks_lottoevent.R;
import com.example.slacks_lottoevent.Utility.Callback;
import com.example.slacks_lottoevent.Utility.SnackbarUtils;
import com.example.slacks_lottoevent.model.Profile;
import com.example.slacks_lottoevent.viewmodel.ProfileViewModel;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // UI elements
    private TextView usernameText;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private ImageView profilePhoto;
    private ImageView notificationsIcon;
    private Switch notificationsSwitch;
    private Button editProfileButton;
    private Button uploadButton;
    private Button removeButton;
    private Button confirmButton;
    private Button cancelButton;

    // ViewModel
    private ProfileViewModel profileViewModel;

    // Image picker
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;
    /**
     * Default constructor
     */
    public ProfileFragment() {
        // Required empty public constructor
    }
    /**
     * Factory method to create a new instance of the ProfileFragment.
     *
     * @return A new instance of ProfileFragment.
     */
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when the fragment is being created. Initializes the fragment.
     *
     * @param savedInstanceState A bundle containing the saved state of the fragment, if any.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    /**
     * Called to inflate the fragment's UI layout.
     *
     * @param inflater The LayoutInflater object used to inflate the view.
     * @param container The parent view that the fragment's UI will be attached to.
     * @param savedInstanceState A bundle containing the saved state of the fragment.
     * @return The view for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
    /**
     * Called when the fragment's view has been created. Initializes UI elements
     * and sets up ViewModel observers and listeners.
     *
     * @param view The root view of the fragment's layout.
     * @param savedInstanceState A bundle containing the saved state of the fragment.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ui elements
        // Initialize UI elements
        usernameText = view.findViewById(R.id.username);
        nameEditText = view.findViewById(R.id.edit_name);
        emailEditText = view.findViewById(R.id.edit_email);
        phoneEditText = view.findViewById(R.id.edit_phone);
        profilePhoto = view.findViewById(R.id.profile_image);
        editProfileButton = view.findViewById(R.id.btn_edit_profile);
        uploadButton = view.findViewById(R.id.btn_upload);
        removeButton = view.findViewById(R.id.btn_remove);
        confirmButton = view.findViewById(R.id.btn_confirm);
        cancelButton = view.findViewById(R.id.btn_cancel);

        // Set up ViewModel
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        profileViewModel.getCurrentProfileLiveData().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                usernameText.setText(profile.getName());
                nameEditText.setText(profile.getName());
                emailEditText.setText(profile.getEmail());
                phoneEditText.setText(profile.getPhone());
                // Set profile photo
                if (profile.getUsingDefaultPicture()) {
                    profilePhoto.setImageURI(Uri.parse(profile.getProfilePicturePath()));
                } else {
                    Glide.with(this).load(profile.getProfilePicturePath()).into(profilePhoto);
                }
            } else {
                SnackbarUtils.promptSignUp(requireView(), requireContext(),
                                           R.id.bottom_app_bar); // Prompt user to sign up
            }
        });

        // edit profile button click listener
        editProfileButton.setOnClickListener(v -> {
            if (profileViewModel.getCurrentProfileLiveData().getValue() == null) {
                SnackbarUtils.promptSignUp(requireView(), requireContext(),
                                           R.id.bottom_app_bar); // Prompt user to sign up
            } else {
                editToggle(true);
            }
        });

        // confirm button click listener
        confirmButton.setOnClickListener(v -> {
            if (validInputs()) {
                Profile profile = profileViewModel.getCurrentProfileLiveData().getValue();
                profile.setName(nameEditText.getText().toString().trim(), requireContext());
                profile.setEmail(emailEditText.getText().toString().trim());
                profile.setPhone(phoneEditText.getText().toString().trim());
                editToggle(false);
                profileViewModel.updateProfile(profile);
            }
        });

        // cancel button click listener
        cancelButton.setOnClickListener(v -> {
            nameEditText.setText(profileViewModel.getCurrentProfileLiveData().getValue().getName());
            emailEditText.setText(
                    profileViewModel.getCurrentProfileLiveData().getValue().getEmail());
            phoneEditText.setText(
                    profileViewModel.getCurrentProfileLiveData().getValue().getPhone());
            editToggle(false);
        });

        // upload button click listener
        uploadButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            imagePickerLauncher.launch(intent);
        });

        // remove button click listener
        removeButton.setOnClickListener(v -> {
            Profile profile = profileViewModel.getCurrentProfileLiveData().getValue();
            profile.setProfilePicturePath("");
            profile.setUsingDefaultPicture(true);
            profile.setName(profile.getName(), requireContext()); // Update the profile picture
            profileViewModel.updateProfile(profile);
            profilePhoto.setImageURI(Uri.parse(profile.getProfilePicturePath()));
        });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                        }
                        if (selectedImageUri != null) {
                            editImage(selectedImageUri);
                        }
                    }
                });
    }

    /**
     * Validates the input fields for name, email, and phone number.
     * Ensures that the name is not empty, the email is in a valid format,
     * and the phone number, if provided, only contains numbers.
     *
     * @return true if all inputs are valid, false otherwise.
     */
    private boolean validInputs() {
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
     * Toggles the editable state of the profile fields and buttons.
     * When `edit` is true, the input fields are enabled, and the edit buttons are hidden.
     * Otherwise, the input fields are disabled, and the confirm and cancel buttons are shown.
     *
     * @param edit true to enable editing, false to disable editing.
     */
    private void editToggle(boolean edit) {
        nameEditText.setEnabled(edit);
        emailEditText.setEnabled(edit);
        phoneEditText.setEnabled(edit);
        editProfileButton.setVisibility(edit ? View.GONE : View.VISIBLE);
        confirmButton.setVisibility(edit ? View.VISIBLE : View.GONE);
        cancelButton.setVisibility(edit ? View.VISIBLE : View.GONE);
    }
    /**
     * Handles the image change process by first deleting the old image from Google Cloud Storage,
     * then uploading the new image, and finally updating the Firestore profile document with the
     * new image URL. The UI is updated to reflect the new image.
     *
     * @param newImageUri The URI of the new image to be uploaded.
     */
    private void editImage(Uri newImageUri) {
        // Step 1: Delete the old image from Google Cloud Storage
        deleteOldImage(
                profileViewModel.getCurrentProfileLiveData().getValue().getProfilePicturePath(),
                () -> {
                    // Step 2: Upload the new image
                    uploadNewImage(newImageUri, newImageUrl -> {
                        // Step 3: Update Firestore with the new image URL
                        updateFirestoreWithNewImage(newImageUrl, success -> {
                            if (success) {
                                refreshUI(newImageUrl); // Update the UI
                            } else {
                                Toast.makeText(requireActivity(), "Failed to update Firestore",
                                               Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                });
    }
    /**
     * Deletes the old profile image from Google Cloud Storage if it exists.
     * This ensures that the old image is removed before uploading a new one.
     *
     * @param oldImageUrl The URL of the old image to be deleted.
     * @param onSuccess The callback to be invoked when the image is successfully deleted.
     */
    private void deleteOldImage(String oldImageUrl, Runnable onSuccess) {
        if (oldImageUrl != null && !oldImageUrl.isEmpty() &&
            !profileViewModel.getCurrentProfileLiveData().getValue().getUsingDefaultPicture()) {
            StorageReference storageReference = FirebaseStorage.getInstance()
                                                               .getReferenceFromUrl(oldImageUrl);
            storageReference.delete()
                            .addOnSuccessListener(aVoid -> onSuccess.run())
                            .addOnFailureListener(e -> {
                                Toast.makeText(requireActivity(), "Failed to delete old image",
                                               Toast.LENGTH_SHORT).show();
                            });
        } else {
            onSuccess.run(); // No old image to delete, continue
        }
    }
    /**
     * Uploads the new profile image to Google Cloud Storage and retrieves the image's download URL.
     *
     * @param newImageUri The URI of the new image to upload.
     * @param callback The callback to handle the resulting download URL.
     */
    private void uploadNewImage(Uri newImageUri, Callback<String> callback) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        String fileName = formatter.format(now);
        StorageReference storageRef = FirebaseStorage.getInstance()
                                                     .getReference("profile-pictures/" + fileName);

        storageRef.putFile(newImageUri)
                  .addOnSuccessListener(taskSnapshot -> {
                      Log.d("Storage", "Successfully uploaded");

                      // Retrieve the download URL
                      storageRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    String eventPosterURL = uri.toString();
                                    callback.onComplete(
                                            eventPosterURL); // Pass the URL to the callback
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Error", "Failed to get download URL", e);
                                    callback.onComplete(null); // Return null to indicate failure
                                });
                  })
                  .addOnFailureListener(e -> {
                      Log.d("Storage", "Failed to upload");
                      callback.onComplete(null); // Return null to indicate failure
                  });
    }
    /**
     * Updates the Firestore profile document with the new profile image URL.
     *
     * @param newImageUrl The URL of the new profile image.
     * @param callback The callback to indicate success or failure of the update operation.
     */
    private void updateFirestoreWithNewImage(String newImageUrl, Callback<Boolean> callback) {
        Profile profile = profileViewModel.getCurrentProfileLiveData().getValue();
        profile.setProfilePicturePath(newImageUrl);
        profile.setUsingDefaultPicture(false);
        profileViewModel.updateProfile(profile);
    }
    /**
     * Refreshes the UI to display the updated profile image.
     * It also shows a toast message indicating that the image was updated successfully.
     *
     * @param newImageUrl The URL of the new profile image.
     */
    private void refreshUI(String newImageUrl) {
        Glide.with(this).load(newImageUrl).into(profilePhoto); // Update your ImageView
        Toast.makeText(requireActivity(), "Image updated successfully", Toast.LENGTH_SHORT).show();
    }
}