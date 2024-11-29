package com.example.slacks_lottoevent.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.slacks_lottoevent.Organizer;
import com.example.slacks_lottoevent.database.OrganizerDB;
import com.example.slacks_lottoevent.model.User;

import java.util.ArrayList;
import java.util.HashMap;

public class OrganizerViewModel extends ViewModel {
    private final MutableLiveData<HashMap<String, Organizer>> organizersLiveData = new MutableLiveData<>();
    private final MutableLiveData<Organizer> currentOrganizerLiveData = new MutableLiveData<>();
    private final OrganizerDB organizerDB;
    private User user = User.getInstance();

    public OrganizerViewModel() {
        organizerDB = OrganizerDB.getInstance();
        organizerDB.setOrganizerChangeListener(this::updateOrganizers);
    }

    private void updateOrganizers(HashMap<String, Organizer> updatedOrganizers) {
        organizersLiveData.setValue(updatedOrganizers);
        updateCurrentOrganizer();
    }

    public MutableLiveData<HashMap<String, Organizer>> getOrganizersLiveData() {
        return organizersLiveData;
    }

    public MutableLiveData<Organizer> getCurrentOrganizerLiveData() {
        return currentOrganizerLiveData;
    }

    private void updateCurrentOrganizer() {
        HashMap<String, Organizer> organizers = organizersLiveData.getValue();
        if (organizers != null) {
            Organizer currentOrganizer = organizers.get(user.getDeviceId());
            currentOrganizerLiveData.setValue(currentOrganizer);
        }
    }

    public void updateOrganizer(ArrayList<String> eventIds) {
        organizerDB.updateOrganizer(user.getDeviceId(), eventIds);
    }
}
