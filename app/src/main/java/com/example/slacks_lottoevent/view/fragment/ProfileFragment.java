package com.example.slacks_lottoevent.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.slacks_lottoevent.R;
import com.example.slacks_lottoevent.viewmodel.ProfileViewModel;

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
    private ImageView backButton;
    private ImageView notificationsIcon;
    private Switch notificationsSwitch;
    private Button editProfileButton;
    private Button editPictureButton;
    private Button confirmButton;
    private Button cancelButton;

    // ViewModel
    private ProfileViewModel profileViewModel;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

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
        backButton = view.findViewById(R.id.back_button);
        notificationsIcon = view.findViewById(R.id.notifications_icon);
        notificationsSwitch = view.findViewById(R.id.switch_notifications);
        editProfileButton = view.findViewById(R.id.btn_edit_profile);
        editPictureButton = view.findViewById(R.id.btn_edit_picture);
        confirmButton = view.findViewById(R.id.btn_confirm);
        cancelButton = view.findViewById(R.id.btn_cancel);

        // Set up ViewModel
        ProfileViewModel profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        profileViewModel.getCurrentProfileLiveData().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                usernameText.setText(profile.getName());
                nameEditText.setText(profile.getName());
                emailEditText.setText(profile.getEmail());
                phoneEditText.setText(profile.getPhone());
                // Set profile photo
                // Set notifications switch
            }
        });

    }
}