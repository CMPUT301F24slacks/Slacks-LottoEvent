package com.example.slacks_lottoevent.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.slacks_lottoevent.database.EventDB;
import com.example.slacks_lottoevent.Event;
import com.example.slacks_lottoevent.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventViewModel extends ViewModel {
    // Live data for observing events
    private final MutableLiveData<HashMap<String, Event>> eventsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> waitlistedEventsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> invitedEventsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> unselectedEventsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> attendingEventsLiveData = new MutableLiveData<>();

    private final EventDB eventDB;
    private final User user = User.getInstance();

    public EventViewModel() {
        eventDB = EventDB.getInstance();
        eventDB.setEventChangeListener(this::updateEvents);
    }

    // Update events locally when notified by EventDB
    private void updateEvents(HashMap<String, Event> updatedEvents) {
        eventsLiveData.setValue(updatedEvents);
    }

    // Expose live data for observing in the UI
    public LiveData<HashMap<String, Event>> getEventsLiveData() {
        return eventsLiveData;
    }

    public LiveData<List<Event>> getWaitlistedEventsLiveData() {
        return waitlistedEventsLiveData;
    }

    public LiveData<List<Event>> getInvitedEventsLiveData() {
        return invitedEventsLiveData;
    }

    public LiveData<List<Event>> getUnselectedEventsLiveData() {
        return unselectedEventsLiveData;
    }

    public LiveData<List<Event>> getAttendingEventsLiveData() {
        return attendingEventsLiveData;
    }

}