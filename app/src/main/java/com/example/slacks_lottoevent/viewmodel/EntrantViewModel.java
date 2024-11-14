package com.example.slacks_lottoevent.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.slacks_lottoevent.database.EntrantDB;
import com.example.slacks_lottoevent.model.Entrant;

public class EntrantViewModel extends ViewModel {
    public final MutableLiveData<Entrant> entrant = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public final MutableLiveData<String> error = new MutableLiveData<>();
    public final MutableLiveData<Boolean> result = new MutableLiveData<>(false);
    private final EntrantDB entrantDB;

    public EntrantViewModel() {
        entrantDB = EntrantDB.getInstance();
    }

    /**
     * Checks if an entrant with the given device ID exists in the database.
     *
     * @param deviceId Device ID of the entrant to check for existence.
     */
    public LiveData<Boolean> entrantExists(String deviceId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>(null);
        entrantDB.entrantExists(deviceId)
                 .addOnSuccessListener(exists -> {
                     // Update LiveData with the result
                     result.setValue(exists);
                     Log.d("EntrantViewModel", "Entrant with device ID " + deviceId +
                                               (exists ? " exists" : " does not exist"));
                 })
                 .addOnFailureListener(e -> {
                     // Update LiveData with failure state
                     result.setValue(false);
                     Log.e("EntrantViewModel",
                           "Failed to check entrant existence: " + e.getMessage());
                 });
        return result;
    }

    /**
     * Returns the current entrant LiveData object.
     */
    public LiveData<Entrant> getCurrentEntrant() {
        return entrant;
    }

    /**
     * Retrieves the entrant object with the given device ID from the database and sets it as the current entrant.
     *
     * @param deviceId Device ID of the entrant to retrieve.
     */
    public void setCurrentEntrant(String deviceId) {
        isLoading.setValue(true);
        entrantDB.getEntrant(deviceId)
                 .addOnSuccessListener(documentSnapshot -> {
                     if (documentSnapshot.exists()) {
                         Entrant entrant = documentSnapshot.toObject(Entrant.class);
                         this.entrant.setValue(entrant);
                     } else {
                         error.setValue("Entrant not found");
                     }
                     isLoading.setValue(false);
                 })
                 .addOnFailureListener(e -> {
                     error.setValue("Failed to retrieve entrant: " + e.getMessage());
                     isLoading.setValue(false);
                 });
    }
}
