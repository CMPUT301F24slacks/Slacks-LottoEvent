package com.example.slacks_lottoevent.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.slacks_lottoevent.database.FacilityDB;
import com.example.slacks_lottoevent.Facility;
import com.example.slacks_lottoevent.model.User;

import java.util.HashMap;

public class FacilityViewModel extends ViewModel {
    private final MutableLiveData<HashMap<String, Facility>> facilitiesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Facility> currentFacilityLiveData = new MutableLiveData<>();
    private final FacilityDB facilityDB;
    private final User user = User.getInstance();

    public FacilityViewModel() {
        facilityDB = FacilityDB.getInstance();
        facilityDB.setFacilityChangeListener(this::updateFacilities);
    }

    // Update facilities locally when notified by FacilityDB
    private void updateFacilities(HashMap<String, Facility> updatedFacilities) {
        facilitiesLiveData.setValue(updatedFacilities);
        updateCurrentFacility(); // Automatically update the current facility
    }

    // Expose live data for observing in the UI
    public LiveData<HashMap<String, Facility>> getFacilitiesLiveData() {
        return facilitiesLiveData;
    }

    public LiveData<Facility> getCurrentFacilityLiveData() {
        return currentFacilityLiveData;
    }

    // Directly update the current facility based on the device ID
    private void updateCurrentFacility() {
        HashMap<String, Facility> facilities = facilitiesLiveData.getValue();
        if (facilities != null) {
            Facility currentFacility = facilities.get(user.getDeviceId());
            currentFacilityLiveData.setValue(currentFacility);
        }
    }

    /**
     * Create a new facility in Firestore if it doesn't already exist. Otherwise, update the existing facility.
     *
     * @param name The name of the facility
     * @param streetAddress The first line of the street address
     */
    public void updateFacility(String name, String streetAddress) {
        facilityDB.updateFacility(name, streetAddress, user.getDeviceId());
    }
}
