package com.example.slacks_lottoevent.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.slacks_lottoevent.database.EntrantDB;
import com.example.slacks_lottoevent.model.Entrant;
import com.google.firebase.firestore.ListenerRegistration;

public class EntrantViewModel extends ViewModel {
    public final MutableLiveData<Entrant> entrantLiveData = new MutableLiveData<>(); // LiveData for current entrant
    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(); // LiveData for loading state
    public final MutableLiveData<String> error = new MutableLiveData<>(); // LiveData for error state

    private ListenerRegistration currentEntrantListener; // Listener for current entrant data changes
    private final EntrantDB entrantDB = EntrantDB.getInstance(); // Database instance

    public EntrantViewModel() {
        isLoading.setValue(false);
        error.setValue(null);
    }

    /**
     * Observes the entrant with the given device ID and updates the LiveData object with the
     * entrant data.
     *
     * @param deviceId Device ID of the entrant to observe.
     */
    public void observeEntrant(String deviceId) {
        isLoading.postValue(true);
        if (currentEntrantListener != null) {
            currentEntrantListener.remove();
        }

        currentEntrantListener = entrantDB.getEntrantSnapshotListener(deviceId, (snapshot, e) -> {
            isLoading.postValue(false); // Loading finished
            if (e != null) {
                error.postValue("Failed to observe entrant data.");
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                Entrant entrant = snapshot.toObject(Entrant.class);
                if (entrant != null) {
                    entrantLiveData.postValue(entrant);
                }
            }
        });
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        if (currentEntrantListener != null) {
            currentEntrantListener.remove();
        }
    }

    /**
     * Checks if an entrant with the given device ID exists in the database.
     *
     * @param deviceId Device ID of the entrant to check for existence.
     */
    public LiveData<Boolean> entrantExists(String deviceId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>(false);
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
        return entrantLiveData;
    }
}
