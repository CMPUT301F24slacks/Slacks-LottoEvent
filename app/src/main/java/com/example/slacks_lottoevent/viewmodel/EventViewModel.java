package com.example.slacks_lottoevent.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.slacks_lottoevent.database.EventDB;
import com.example.slacks_lottoevent.model.Event;
import com.example.slacks_lottoevent.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * EventViewModel is a class that provides the UI with the data it needs to display events.
 */
public class EventViewModel extends ViewModel {
    // Live data for observing events
    private final MutableLiveData<HashMap<String, Event>> eventsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> entrantEventsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> hostingEventsLiveData = new MutableLiveData<>();

    private final EventDB eventDB;
    private final User user = User.getInstance();

    /**
     * EventViewModel is a constructor that initializes the EventDB and sets the event change listener.
     */
    public EventViewModel() {
        eventDB = EventDB.getInstance();
        eventDB.setEventChangeListener(this::updateEvents);
    }

    /**
     * updateEvents is a method that updates the events live data.
     * @param updatedEvents is a HashMap of events to update the live data with.
     */
    private void updateEvents(HashMap<String, Event> updatedEvents) {
        eventsLiveData.setValue(updatedEvents);
    }

    /**
     * getEventsLiveData is a method that returns the events live data.
     * @return the events live data.
     */
    public LiveData<HashMap<String, Event>> getEventsLiveData() {
        return eventsLiveData;
    }

    /**
     * getEntrantEventsLiveData is a method that returns the entrant events live data.
     * @return the entrant events live data.
     */
    public LiveData<List<Event>> getEntrantEventsLiveData() {
        return entrantEventsLiveData;
    }

    /**
     * getHostingEventsLiveData is a method that returns the hosting events live data.
     * @return the hosting events live data.
     */
    public LiveData<List<Event>> getHostingEventsLiveData() {
        return hostingEventsLiveData;
    }

    /**
     * updateEventLists is a method that updates the entrant events live data.
     * @param waitlistedIds is a list of waitlisted event IDs.
     * @param unselectedIds is a list of unselected event IDs.
     * @param invitedIds is a list of invited event IDs.
     * @param attendingIds is a list of attending event IDs.
     */
    public void updateEventLists(List<String> waitlistedIds, List<String> unselectedIds,
                                 List<String> invitedIds, List<String> attendingIds) {
        HashMap<String, Event> eventsHashMap = eventsLiveData.getValue();
        if (eventsHashMap != null) {
            List<Event> tempEvents = new ArrayList<>();
            tempEvents.addAll(getEventsByIds(eventsHashMap, waitlistedIds));
            tempEvents.addAll(getEventsByIds(eventsHashMap, unselectedIds));
            tempEvents.addAll(getEventsByIds(eventsHashMap, invitedIds));
            tempEvents.addAll(getEventsByIds(eventsHashMap, attendingIds));
            entrantEventsLiveData.setValue(tempEvents);
        }
    }

    /**
     * getEventsByIds is a method that returns a list of events by their IDs.
     * @param eventsHashMap is a HashMap of events.
     * @param ids is a list of event IDs.
     * @return a list of events by their IDs.
     */
    private List<Event> getEventsByIds(HashMap<String, Event> eventsHashMap, List<String> ids) {
        List<Event> events = new ArrayList<>();
        if (ids == null) {
            return events;
        }
        for (String id : ids) {
            Event event = eventsHashMap.get(id);
            if (event != null) {
                events.add(event);
            }
        }
        return events;
    }

    /**
     * updateOrganizerEvents is a method that updates the hosting events live data.
     * @param eventIds is a list of event IDs.
     */
    public void updateOrganizerEvents(List<String> eventIds) {
        HashMap<String, Event> eventsHashMap = eventsLiveData.getValue();
        if (eventsHashMap != null) {
            List<Event> organizerEvents = getEventsByIds(eventsHashMap, eventIds);
            hostingEventsLiveData.setValue(organizerEvents);
        }
    }

    /**
     * addEvent is a method that adds an event to the database.
     * @param event is an event to add.
     */
    public void updateEvent(Event event) {
        eventDB.updateEvent(event);
    }

}