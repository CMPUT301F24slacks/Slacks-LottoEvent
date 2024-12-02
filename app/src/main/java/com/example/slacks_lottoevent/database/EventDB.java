package com.example.slacks_lottoevent.database;

import com.example.slacks_lottoevent.model.Event;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;

/**
 * EventDB is a singleton class that manages the events in the Firestore database.
 */
public class EventDB {
    private static EventDB instance;
    private final FirebaseFirestore db;
    private final HashMap<String, Event> eventsCache;
    private ListenerRegistration listenerRegistration;
    private EventChangeListener eventChangeListener;

    /**
     * Private constructor to prevent instantiation from outside the class.
     */
    private EventDB() {
        db = FirebaseFirestore.getInstance();
        eventsCache = new HashMap<>();
    }

    /**
     * Returns the singleton instance of EventDB.
     *
     * @return the singleton instance of EventDB
     */
    public static synchronized EventDB getInstance() {
        if (instance == null) {
            instance = new EventDB();
        }
        return instance;
    }

    /**
     * Starts listening for Firestore updates for the events collection.
     */
    public void startListening() {
        if (listenerRegistration == null) {
            listenerRegistration = db.collection("events")
                                     .addSnapshotListener((snapshots, e) -> {
                                         if (e != null) {
                                             e.printStackTrace();
                                             return;
                                         }
                                         if (snapshots != null) {
                                             eventsCache.clear();
                                             for (DocumentSnapshot doc : snapshots.getDocuments()) {
                                                 eventsCache.put(doc.getId(),
                                                                 doc.toObject(Event.class));
                                             }
                                             notifyEventChangeListener();
                                         }
                                     });
        }
    }

    /**
     * Stops listening to the events in the Firestore database.
     */
    public void stopListening() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }

    /**
     * Sets the EventChangeListener for this EventDB.
     *
     * @param listener the EventChangeListener to set
     */
    public void setEventChangeListener(EventChangeListener listener) {
        this.eventChangeListener = listener;
        startListening();
    }

    /**
     * Notifies the EventChangeListener that the events have changed.
     */
    public void notifyEventChangeListener() {
        if (eventChangeListener != null) {
            eventChangeListener.onEventsChanged(new HashMap<>(eventsCache));
        }
    }

    /**
     * Adds an event to the Firestore database.
     *
     * @param event the event to add
     */
    public void updateEvent(Event event) {
        db.collection("events").document(event.getEventID()).set(event);
    }

    /**
     * Interface for listening to changes in the events in the Firestore database.
     */
    public interface EventChangeListener {

        /**
         * Called when the events in the Firestore database have changed.
         *
         * @param updatedEvents the updated events
         */
        void onEventsChanged(HashMap<String, Event> updatedEvents);
    }
}
