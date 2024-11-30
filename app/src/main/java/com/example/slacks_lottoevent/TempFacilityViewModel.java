package com.example.slacks_lottoevent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.slacks_lottoevent.model.Facility;

public class TempFacilityViewModel extends ViewModel {
    private final MutableLiveData<Boolean> hasFacility = new MutableLiveData<>(false);
    private final MutableLiveData<Facility> currentFacility = new MutableLiveData<>();

    // Getter for LiveData
    public LiveData<Boolean> getFacilityStatus() {
        return hasFacility;
    }

    // Setter to update the facility status
    public void setFacilityStatus(boolean status) {
        hasFacility.setValue(status);
    }

    public LiveData<Facility> getCurrentFacility() {
        return currentFacility;
    }

    // Setter for current facility
    public void setCurrentFacility(Facility facility) {
        currentFacility.setValue(facility);
    }
}
