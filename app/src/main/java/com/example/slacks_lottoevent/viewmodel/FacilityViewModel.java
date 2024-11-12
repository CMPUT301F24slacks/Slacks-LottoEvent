package com.example.slacks_lottoevent.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.slacks_lottoevent.database.FacilityDB;
import com.example.slacks_lottoevent.model.Facility;
import com.example.slacks_lottoevent.model.User;

public class FacilityViewModel extends ViewModel {
    private final FacilityDB facilityDB;
    private final String deviceId;
    private static MutableLiveData<Facility> facility = new MutableLiveData<>();

    public FacilityViewModel(String deviceId) {
        facilityDB = FacilityDB.getInstance();
        this.deviceId = deviceId;
    }

    /**
     * Set facility to be observed.
     */
    public void setFacility(Facility facility) {
        FacilityViewModel.facility.setValue(facility);
    }

    /**
     * Adds a facility to the database.
     * @param facility Facility object to be added to the database.
     */
    public void addFacility(Facility facility) {
        facilityDB.addFacility(facility)
                .addOnSuccessListener(aVoid -> {
                    // Facility added successfully
                    Log.d("FacilityViewModel", "Facility added successfully");
                })
                .addOnFailureListener(e -> {
                    // Handle errors during facility addition
                    Log.e("FacilityViewModel", "Failed to add facility: " + e.getMessage());
                });
    }

    /**
     * Checks if a facility with the given device ID exists in the database.
     * @param deviceId Device ID of the facility to check for existence.
     */
    public LiveData<Boolean> facilityExists(String deviceId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        facilityDB.facilityExists(deviceId)
                .addOnSuccessListener(exists -> {
                    // Update LiveData with the result
                    result.setValue(exists);
                    Log.d("FacilityViewModel", "Facility with device ID " + deviceId + (exists ? " exists" : " does not exist"));
                })
                .addOnFailureListener(e -> {
                    // Update LiveData with failure state
                    result.setValue(false);
                    Log.e("FacilityViewModel", "Failed to check facility existence: " + e.getMessage());
                });
        return result;
    }

    /**
     * Returns a facility with the matching device ID from the database.
     */
    public LiveData<Facility> getFacility() {
        MutableLiveData<Facility> result = new MutableLiveData<>();
        facilityDB.getFacility(deviceId)
                .addOnSuccessListener(facility -> {
                    // Update LiveData with the facility
                    result.setValue(facility);
                    Log.d("FacilityViewModel", "Retrieved facility with device ID " + deviceId);
                })
                .addOnFailureListener(e -> {
                    // Update LiveData with failure state
                    result.setValue(null);
                    Log.e("FacilityViewModel", "Failed to retrieve facility: " + e.getMessage());
                });
        return result;
    }

}
