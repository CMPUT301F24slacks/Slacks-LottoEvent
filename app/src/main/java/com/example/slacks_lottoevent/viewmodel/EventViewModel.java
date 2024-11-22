package com.example.slacks_lottoevent.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.slacks_lottoevent.database.EventDB;
import com.example.slacks_lottoevent.model.Event;

import java.util.ArrayList;
import java.util.List;

public class EventViewModel extends ViewModel {
    public final MutableLiveData<List<Event>> waitlistedEvents = new MutableLiveData<>();
    public final MutableLiveData<List<Event>> unselectedEvents = new MutableLiveData<>();
    public final MutableLiveData<List<Event>> invitedEvents = new MutableLiveData<>();
    public final MutableLiveData<List<Event>> attendingEvents = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public final MutableLiveData<String> error = new MutableLiveData<>();
    private final EventDB eventDB;

    public EventViewModel() {
        eventDB = EventDB.getInstance();
    }

    /**
     * Retrieves all events whose document IDs are in the given ArrayList.
     *
     * @param eventIds ArrayList of event IDs to retrieve events for.
     */
    public LiveData<List<Event>> getEvents(ArrayList<String> eventIds) {
        isLoading.setValue(true);
        MutableLiveData<List<Event>> tempEvents = new MutableLiveData<>();
        eventDB.getEvents(eventIds)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Event> eventList = queryDocumentSnapshots.toObjects(Event.class);
                    tempEvents.setValue(eventList);
                    isLoading.setValue(false);
                    Log.d("EventViewModel", "Retrieved " + eventList.size() + " events");
                })
                .addOnFailureListener(e -> {
                    error.setValue(e.getMessage());
                    isLoading.setValue(false);
                    Log.e("EventViewModel", "Failed to retrieve events: " + e.getMessage());
                });
        return tempEvents;
    }


    /**
     * Sets waitlisted events LiveData to the events corresponding to the list of event IDs.
     */
    public void setWaitlistedEvents(ArrayList<String> eventIds) {
        isLoading.setValue(true);
        eventDB.getEvents(eventIds)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Event> eventList = queryDocumentSnapshots.toObjects(Event.class);
                    waitlistedEvents.setValue(eventList);
                    isLoading.setValue(false);
                    Log.d("EventViewModel", "Retrieved " + eventList.size() + " waitlisted events");
                })
                .addOnFailureListener(e -> {
                    error.setValue(e.getMessage());
                    isLoading.setValue(false);
                    Log.e("EventViewModel", "Failed to retrieve waitlisted events: " + e.getMessage());
                });

    }

    /**
     * Sets unselected events LiveData to the given list of events.
     */
    public void setUnselectedEvents(List<Event> eventList) {
        unselectedEvents.setValue(eventList);
    }

    /**
     * Sets invited events LiveData to the given list of events.
     */
    public void setInvitedEvents(List<Event> eventList) {
        invitedEvents.setValue(eventList);
    }

    /**
     * Sets attending events LiveData to the given list of events.
     */
    public void setAttendingEvents(List<Event> eventList) {
        attendingEvents.setValue(eventList);
    }

    /**
     * Return LiveData of waitlisted events.
     */
    public LiveData<List<Event>> getWaitlistedEvents() {
        return waitlistedEvents;
    }

}