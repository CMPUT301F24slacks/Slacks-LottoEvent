package com.example.slacks_lottoevent.database;

import com.example.slacks_lottoevent.Entrant;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;

public class EntrantDB {
    private static EntrantDB instance;
    private final FirebaseFirestore db;
    private final HashMap<String, Entrant> entrantsCache;
    private ListenerRegistration listenerRegistration;
    private EntrantChangeListener entrantChangeListener;

    private EntrantDB() {
        db = FirebaseFirestore.getInstance();
        entrantsCache = new HashMap<>();
    }

    // Singleton pattern
    public static synchronized EntrantDB getInstance() {
        if (instance == null) {
            instance = new EntrantDB();
        }
        return instance;
    }

    // Start listening to the "entrants" collection for changes
    public void startListening() {
        if (listenerRegistration == null) {
            listenerRegistration = db.collection("entrants")
                                     .addSnapshotListener((snapshots, e) -> {
                                         if (e != null) {
                                             e.printStackTrace();
                                             return;
                                         }
                                         if (snapshots != null) {
                                             entrantsCache.clear();
                                             for (DocumentSnapshot doc : snapshots.getDocuments()) {
                                                 entrantsCache.put(doc.getId(),
                                                                   doc.toObject(Entrant.class));
                                             }
                                             notifyEntrantChangeListener();
                                         }
                                     });
        }
    }

    // Stop listening to Firestore updates
    public void stopListening() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }

    // Set a listener to notify when entrants change
    public void setEntrantChangeListener(EntrantChangeListener listener) {
        this.entrantChangeListener = listener;
        startListening();
    }

    // Notify the listener that entrants have changed
    private void notifyEntrantChangeListener() {
        if (entrantChangeListener != null) {
            entrantChangeListener.onEntrantsChanged(new HashMap<>(entrantsCache));
        }
    }

    // Interface for notifying entrant changes
    public interface EntrantChangeListener {
        void onEntrantsChanged(HashMap<String, Entrant> updatedEntrants);
    }

}
