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
    private final MutableLiveData<List<Event>> hostingEventsLiveData = new MutableLiveData<>();

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

    public LiveData<List<Event>> getHostingEventsLiveData() {
        return hostingEventsLiveData;
    }

    public void updateEventLists(List<String> waitlistedIds, List<String> unselectedIds, List<String> invitedIds, List<String> attendingIds) {
        HashMap<String, Event> eventsHashMap = eventsLiveData.getValue();
        if (eventsHashMap != null) {
            waitlistedEventsLiveData.setValue(getEventsByIds(eventsHashMap, waitlistedIds));
            invitedEventsLiveData.setValue(getEventsByIds(eventsHashMap, unselectedIds));
            unselectedEventsLiveData.setValue(getEventsByIds(eventsHashMap, invitedIds));
            attendingEventsLiveData.setValue(getEventsByIds(eventsHashMap, attendingIds));
        }
    }

    private List<Event> getEventsByIds(HashMap<String, Event> eventsHashMap, List<String> ids) {
        List<Event> events = new ArrayList<>();
        for (String id : ids) {
            Event event = eventsHashMap.get(id);
            if (event != null) {
                events.add(event);
            }
        }
        return events;
    }

    public void updateOrganizerEvents(List<String> eventIds) {
        HashMap<String, Event> eventsHashMap = eventsLiveData.getValue();
        if (eventsHashMap != null) {
            List<Event> organizerEvents = getEventsByIds(eventsHashMap, eventIds);
            hostingEventsLiveData.setValue(organizerEvents);
        }
    }


}