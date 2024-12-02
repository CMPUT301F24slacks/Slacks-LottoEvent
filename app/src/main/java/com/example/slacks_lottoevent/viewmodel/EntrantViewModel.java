package com.example.slacks_lottoevent.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.slacks_lottoevent.model.Entrant;
import com.example.slacks_lottoevent.database.EntrantDB;
import com.example.slacks_lottoevent.model.User;

import java.util.HashMap;

/**
 * ViewModel for Entrant
 */
public class EntrantViewModel extends ViewModel {
    private final MutableLiveData<HashMap<String, Entrant>> entrantsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Entrant> currentEntrantLiveData = new MutableLiveData<>();
    private final EntrantDB entrantDB;
    private final User user = User.getInstance();

    /**
     * Constructor for EntrantViewModel
     */
    public EntrantViewModel() {
        entrantDB = EntrantDB.getInstance();
        entrantDB.setEntrantChangeListener(this::updateEntrants);
    }

    /**
     * Update entrants locally and update current entrant
     * @param updatedEntrants updated entrants
     */
    private void updateEntrants(HashMap<String, Entrant> updatedEntrants) {
        entrantsLiveData.setValue(updatedEntrants);
        updateCurrentEntrant();
    }

    /**
     * Get entrants live data
     * @return entrants live data
     */
    public LiveData<HashMap<String, Entrant>> getEntrantsLiveData() {
        return entrantsLiveData;
    }

    /**
     * Get current entrant live data
     * @return current entrant live data
     */
    public LiveData<Entrant> getCurrentEntrantLiveData() {
        return currentEntrantLiveData;
    }

    /**
     * Update current entrant
     */
    private void updateCurrentEntrant() {
        HashMap<String, Entrant> entrants = entrantsLiveData.getValue();
        if (entrants != null) {
            Entrant currentEntrant = entrants.get(user.getDeviceId());
            currentEntrantLiveData.setValue(currentEntrant);
        }
    }
}
