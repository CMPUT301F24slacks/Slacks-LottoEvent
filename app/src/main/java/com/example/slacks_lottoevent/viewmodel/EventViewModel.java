package com.example.slacks_lottoevent.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.slacks_lottoevent.database.EventDB;
import com.example.slacks_lottoevent.Event;

import java.util.List;

public class EventViewModel extends ViewModel {
    private final EventDB eventDB;

    // Directly exposed MutableLiveData
    public final MutableLiveData<List<Event>> events = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public final MutableLiveData<String> error = new MutableLiveData<>();

    public EventViewModel() {
        eventDB = EventDB.getInstance();
    }

    public void loadEvents() {
        isLoading.setValue(true); // Indicate loading state
        eventDB.getEvents()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Successfully fetched events
                    List<Event> eventList = queryDocumentSnapshots.toObjects(Event.class);
                    events.setValue(eventList);
                    error.setValue(null); // Clear previous errors
                })
                .addOnFailureListener(e -> {
                    // Handle errors during data fetching
                    error.setValue("Failed to load events: " + e.getMessage());
                })
                .addOnCompleteListener(task -> {
                    // Always reset loading state
                    isLoading.setValue(false);
                });
    }
}
