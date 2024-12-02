package com.example.slacks_lottoevent.database;

import com.example.slacks_lottoevent.model.Entrant;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;

/**
 * EntrantDB is a singleton class that listens to Firestore for changes to the "entrants" collection.
 */
public class EntrantDB {
    private static EntrantDB instance;
    private final FirebaseFirestore db;
    private final HashMap<String, Entrant> entrantsCache;
    private ListenerRegistration listenerRegistration;
    private EntrantChangeListener entrantChangeListener;

    /**
     * Private constructor to prevent instantiation from outside the class.
     */
    private EntrantDB() {
        db = FirebaseFirestore.getInstance();
        entrantsCache = new HashMap<>();
    }

    /**
     * Get the singleton instance of EntrantDB.
     * @return The singleton instance of EntrantDB.
     */
    public static synchronized EntrantDB getInstance() {
        if (instance == null) {
            instance = new EntrantDB();
        }
        return instance;
    }

    /**
     * Start listening to Firestore updates for the entrants collection.
     */
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

    /**
     * Stop listening to Firestore updates.
     */
    public void stopListening() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }

    /**
     * Get the entrants cache.
     * @param listener
     */
    public void setEntrantChangeListener(EntrantChangeListener listener) {
        this.entrantChangeListener = listener;
        startListening();
    }

    /**
     * Get the entrants cache and notify the listener.
     */
    private void notifyEntrantChangeListener() {
        if (entrantChangeListener != null) {
            entrantChangeListener.onEntrantsChanged(new HashMap<>(entrantsCache));
        }
    }

    /**
     * Interface for listening to changes in the entrants cache.
     */
    public interface EntrantChangeListener {
        /**
         * Called when the entrants cache has changed.
         * @param updatedEntrants The updated entrants cache.
         */
        void onEntrantsChanged(HashMap<String, Entrant> updatedEntrants);
    }

}
