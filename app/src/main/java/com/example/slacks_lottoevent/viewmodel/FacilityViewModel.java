package com.example.slacks_lottoevent.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.slacks_lottoevent.database.FacilityDB;
import com.example.slacks_lottoevent.model.Facility;

public class FacilityViewModel extends ViewModel {
    private final FacilityDB facilityDB;

    public FacilityViewModel() {
        facilityDB = FacilityDB.getInstance();
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
    public void facilityExists(String deviceId) {
        facilityDB.facilityExists(deviceId)
                .addOnSuccessListener(exists -> {
                    if (exists) {
                        // Facility exists
                        Log.d("FacilityViewModel", "Facility with device ID " + deviceId + " exists");
                    } else {
                        // Facility does not exist
                        Log.d("FacilityViewModel", "Facility with device ID " + deviceId + " does not exist");
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors during facility existence check
                    Log.e("FacilityViewModel", "Failed to check facility existence: " + e.getMessage());
                });
    }

}
