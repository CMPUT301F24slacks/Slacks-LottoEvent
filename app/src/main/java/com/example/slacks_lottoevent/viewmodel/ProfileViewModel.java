package com.example.slacks_lottoevent.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.slacks_lottoevent.model.Profile;
import com.example.slacks_lottoevent.database.ProfileDB;
import com.example.slacks_lottoevent.model.User;

import java.util.HashMap;

/**
 * ViewModel for the ProfileFragment. Manages the current profile and all profiles in Firestore.
 */
public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<HashMap<String, Profile>> profilesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Profile> currentProfileLiveData = new MutableLiveData<>();
    private final ProfileDB profileDB;
    private final User user = User.getInstance();

    /**
     * Constructor for the ProfileViewModel. Initializes the ProfileDB and sets the listener for
     * profile changes.
     */
    public ProfileViewModel() {
        profileDB = ProfileDB.getInstance();
        profileDB.setProfileChangeListener(this::updateProfiles);
    }

    /**
     * Updates the profiles in the ViewModel and sets the current profile.
     * @param updatedProfiles The updated profiles from Firestore.
     */
    private void updateProfiles(HashMap<String, Profile> updatedProfiles) {
        profilesLiveData.setValue(updatedProfiles);
        updateCurrentProfile(); // Automatically update the current profile
    }

    /**
     * Returns the profiles LiveData.
     * @return The profiles LiveData.
     */
    public LiveData<HashMap<String, Profile>> getProfilesLiveData() {
        return profilesLiveData;
    }

    /**
     * Returns the current profile LiveData.
     * @return The current profile LiveData.
     */
    public LiveData<Profile> getCurrentProfileLiveData() {
        return currentProfileLiveData;
    }

    /**
     * Updates the current profile LiveData with the current profile from the profiles LiveData.
     */
    private void updateCurrentProfile() {
        HashMap<String, Profile> profiles = profilesLiveData.getValue();
        if (profiles != null) {
            Profile currentProfile = profiles.get(user.getDeviceId());
            currentProfileLiveData.setValue(currentProfile);
        }
    }

    /**
     * Adds a new profile to Firestore.
     * @param profile The profile to add.
     */
    public void updateProfile(Profile profile) {
        profileDB.updateProfile(profile);
    }
}

