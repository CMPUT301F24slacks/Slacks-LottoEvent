package com.example.slacks_lottoevent.database;

import com.example.slacks_lottoevent.model.Organizer;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * OrganizerDB is a singleton class that manages the Firestore database for organizers.
 */
public class OrganizerDB {
    private static OrganizerDB instance;
    private final FirebaseFirestore db;
    private final HashMap<String, Organizer> organizersCache;
    private ListenerRegistration listenerRegistration;
    private OrganizerChangeListener organizerChangeListener;

    /**
     * Private constructor to prevent instantiation from outside the class.
     */
    private OrganizerDB() {
        db = FirebaseFirestore.getInstance();
        organizersCache = new HashMap<>();
    }

    /**
     * Get the singleton instance of OrganizerDB.
     * @return The singleton instance of OrganizerDB.
     */
    public static synchronized OrganizerDB getInstance() {
        if (instance == null) {
            instance = new OrganizerDB();
        }
        return instance;
    }

    /**
     * Start listening to Firestore updates for the organizers collection.
     */
    public void startListening() {
        if (listenerRegistration == null) {
            listenerRegistration = db.collection("organizers")
                                     .addSnapshotListener((snapshots, e) -> {
                                         if (e != null) {
                                             e.printStackTrace();
                                             return;
                                         }
                                         if (snapshots != null) {
                                             organizersCache.clear();
                                             for (DocumentSnapshot doc : snapshots.getDocuments()) {
                                                 organizersCache.put(doc.getId(),
                                                                     doc.toObject(Organizer.class));
                                             }
                                             notifyOrganizerChangeListener();
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
     * Set the listener for changes in the organizers collection.
     * @param listener the listener for changes in the organizers collection
     */
    public void setOrganizerChangeListener(OrganizerChangeListener listener) {
        this.organizerChangeListener = listener;
        startListening();
    }

    /**
     * Notify the listener that the organizers have changed.
     */
    private void notifyOrganizerChangeListener() {
        if (organizerChangeListener != null) {
            organizerChangeListener.onOrganizersChanged(new HashMap<>(organizersCache));
        }
    }

    /**
     * Update the organizer in the Firestore database.
     * @param deviceId the device ID of the organizer
     * @param eventIds the list of event IDs
     */
    public void updateOrganizer(String deviceId, ArrayList<String> eventIds) {
        db.collection("organizers").document(deviceId).set(new Organizer(deviceId, eventIds));
    }

    /**
     * Interface for listening to changes in the organizers collection.
     */
    public interface OrganizerChangeListener {

        /**
         * Called when the organizers collection has changed.
         * @param organizers The updated organizers collection.
         */
        void onOrganizersChanged(HashMap<String, Organizer> organizers);
    }
}
