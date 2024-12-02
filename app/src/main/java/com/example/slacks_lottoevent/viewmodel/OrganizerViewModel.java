package com.example.slacks_lottoevent.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.slacks_lottoevent.model.Organizer;
import com.example.slacks_lottoevent.database.OrganizerDB;
import com.example.slacks_lottoevent.model.User;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * ViewModel for Organizer
 */
public class OrganizerViewModel extends ViewModel {
    private final MutableLiveData<HashMap<String, Organizer>> organizersLiveData = new MutableLiveData<>();
    private final MutableLiveData<Organizer> currentOrganizerLiveData = new MutableLiveData<>();
    private final OrganizerDB organizerDB;
    private User user = User.getInstance();

    /**
     * Constructor for OrganizerViewModel
     */
    public OrganizerViewModel() {
        organizerDB = OrganizerDB.getInstance();
        organizerDB.setOrganizerChangeListener(this::updateOrganizers);
    }

    /**
     * Update the organizers
     * @param updatedOrganizers updated organizers
     */
    private void updateOrganizers(HashMap<String, Organizer> updatedOrganizers) {
        organizersLiveData.setValue(updatedOrganizers);
        updateCurrentOrganizer();
    }

    /**
     * Get the organizers live data
     * @return organizers live data
     */
    public MutableLiveData<HashMap<String, Organizer>> getOrganizersLiveData() {
        return organizersLiveData;
    }

    /**
     * Get the current organizer live data
     * @return current organizer live data
     */
    public MutableLiveData<Organizer> getCurrentOrganizerLiveData() {
        return currentOrganizerLiveData;
    }

    /**
     * Update the current organizer
     */
    private void updateCurrentOrganizer() {
        HashMap<String, Organizer> organizers = organizersLiveData.getValue();
        if (organizers != null) {
            Organizer currentOrganizer = organizers.get(user.getDeviceId());
            currentOrganizerLiveData.setValue(currentOrganizer);
        }
    }

    /**
     * Update the organizer
     * @param eventIds event ids
     */
    public void updateOrganizer(ArrayList<String> eventIds) {
        organizerDB.updateOrganizer(user.getDeviceId(), eventIds);
    }
}
