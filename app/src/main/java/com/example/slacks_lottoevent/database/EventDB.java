package com.example.slacks_lottoevent.database;

import com.example.slacks_lottoevent.Event;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;

public class EventDB {
    private static EventDB instance;
    private final FirebaseFirestore db;
    private final HashMap<String, Event> eventsCache;
    private ListenerRegistration listenerRegistration;
    private EventChangeListener eventChangeListener;

    private EventDB() {
        db = FirebaseFirestore.getInstance();
        eventsCache = new HashMap<>();
    }

    public static synchronized EventDB getInstance() {
        if (instance == null) {
            instance = new EventDB();
        }
        return instance;
    }

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
                                eventsCache.put(doc.getId(), doc.toObject(Event.class));
                            }
                            notifyEventChangeListener();
                        }
                    });
        }
    }

    public void stopListening() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }

    public void setEventChangeListener(EventChangeListener listener) {
        this.eventChangeListener = listener;
        startListening();
    }

    public void notifyEventChangeListener() {
        if (eventChangeListener != null) {
            eventChangeListener.onEventsChanged(new HashMap<>(eventsCache));
        }
    }

    public interface EventChangeListener {
        void onEventsChanged(HashMap<String, Event> updatedEvents);
    }

}
