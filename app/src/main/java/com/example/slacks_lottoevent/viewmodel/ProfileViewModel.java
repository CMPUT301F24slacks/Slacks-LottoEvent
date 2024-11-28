package com.example.slacks_lottoevent.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.slacks_lottoevent.Profile;
import com.example.slacks_lottoevent.database.ProfileDB;
import com.example.slacks_lottoevent.model.User;

import java.util.HashMap;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<HashMap<String, Profile>> profilesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Profile> currentProfileLiveData = new MutableLiveData<>();
    private final ProfileDB profileDB;
    private final User user = User.getInstance();

    public ProfileViewModel() {
        profileDB = ProfileDB.getInstance();
        profileDB.setProfileChangeListener(this::updateProfiles);
    }

    // Update profiles locally when notified by ProfileDB
    private void updateProfiles(HashMap<String, Profile> updatedProfiles) {
        profilesLiveData.setValue(updatedProfiles);
        updateCurrentProfile(); // Automatically update the current profile
    }

    // Expose live data for observing in the UI
    public LiveData<HashMap<String, Profile>> getProfilesLiveData() {
        return profilesLiveData;
    }

    public LiveData<Profile> getCurrentProfileLiveData() {
        return currentProfileLiveData;
    }

    // Directly update the current profile based on the device ID
    private void updateCurrentProfile() {
        HashMap<String, Profile> profiles = profilesLiveData.getValue();
        if (profiles != null) {
            Profile currentProfile = profiles.get(user.getDeviceId());
            if (currentProfile == null) {
                Log.d("ProfileViewModel", "No profile found for user ID: " + user.getDeviceId());
            } else {
                currentProfileLiveData.setValue(currentProfile);
                Log.d("ProfileViewModel", "Current profile updated for user ID: " + user.getDeviceId());
            }
        }
    }
}

