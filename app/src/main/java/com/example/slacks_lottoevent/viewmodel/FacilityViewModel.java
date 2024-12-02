package com.example.slacks_lottoevent.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.slacks_lottoevent.model.Facility;
import com.example.slacks_lottoevent.database.FacilityDB;
import com.example.slacks_lottoevent.model.User;

import java.util.HashMap;

/**
 * FacilityViewModel is a class that provides the UI with the data it needs to display facilities.
 */
public class FacilityViewModel extends ViewModel {
    private final MutableLiveData<HashMap<String, Facility>> facilitiesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Facility> currentFacilityLiveData = new MutableLiveData<>();
    private final FacilityDB facilityDB;
    private final User user = User.getInstance();

    /**
     * FacilityViewModel is a constructor that initializes the FacilityDB and sets the facility change listener.
     */
    public FacilityViewModel() {
        facilityDB = FacilityDB.getInstance();
        facilityDB.setFacilityChangeListener(this::updateFacilities);
    }

    /**
     * updateFacilities is a method that updates the facilities live data.
     * @param updatedFacilities is a HashMap of facilities to update the live data with.
     */
    private void updateFacilities(HashMap<String, Facility> updatedFacilities) {
        facilitiesLiveData.setValue(updatedFacilities);
        updateCurrentFacility(); // Automatically update the current facility
    }

    /**
     * getFacilitiesLiveData is a method that returns the facilities live data.
     * @return the facilities live data.
     */
    public LiveData<HashMap<String, Facility>> getFacilitiesLiveData() {
        return facilitiesLiveData;
    }

    /**
     * getCurrentFacilityLiveData is a method that returns the current facility live data.
     * @return the current facility live data.
     */
    public LiveData<Facility> getCurrentFacilityLiveData() {
        return currentFacilityLiveData;
    }

    /**
     * updateCurrentFacility is a method that updates the current facility live data.
     */
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
     * @param name          The name of the facility
     * @param streetAddress The first line of the street address
     */
    public void updateFacility(String name, String streetAddress) {
        facilityDB.updateFacility(name, streetAddress, user.getDeviceId());
    }
}
