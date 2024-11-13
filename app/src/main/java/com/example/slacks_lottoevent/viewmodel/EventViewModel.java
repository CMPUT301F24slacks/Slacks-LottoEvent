package com.example.slacks_lottoevent.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.slacks_lottoevent.database.EventDB;
import com.example.slacks_lottoevent.model.Event;

import java.util.List;

public class EventViewModel extends ViewModel {
    // Directly exposed MutableLiveData
    public final MutableLiveData<List<Event>> events = new MutableLiveData<>();
    public final MutableLiveData<List<Event>> waitlistedEvents = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public final MutableLiveData<String> error = new MutableLiveData<>();
    private final EventDB eventDB;
    private final String deviceId;

    public EventViewModel(String deviceId) {
        eventDB = EventDB.getInstance();
        this.deviceId = deviceId;
    }

}
