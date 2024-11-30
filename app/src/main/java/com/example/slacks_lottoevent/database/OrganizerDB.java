package com.example.slacks_lottoevent.database;

import com.example.slacks_lottoevent.Organizer;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;

public class OrganizerDB {
    private static OrganizerDB instance;
    private final FirebaseFirestore db;
    private final HashMap<String, Organizer> organizersCache;
    private ListenerRegistration listenerRegistration;
    private OrganizerChangeListener organizerChangeListener;

    private OrganizerDB() {
        db = FirebaseFirestore.getInstance();
        organizersCache = new HashMap<>();
    }

    // Singleton pattern
    public static synchronized OrganizerDB getInstance() {
        if (instance == null) {
            instance = new OrganizerDB();
        }
        return instance;
    }

    // Start listening to the "organizers" collection for changes
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

    // Stop listening to Firestore updates
    public void stopListening() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }

    // Set a listener to notify when organizers change
    public void setOrganizerChangeListener(OrganizerChangeListener listener) {
        this.organizerChangeListener = listener;
        startListening();
    }

    // Notify the listener that organizers have changed
    private void notifyOrganizerChangeListener() {
        if (organizerChangeListener != null) {
            organizerChangeListener.onOrganizersChanged(new HashMap<>(organizersCache));
        }
    }

    // Update the organizer in Firestore
    public void updateOrganizer(String deviceId, ArrayList<String> eventIds) {
        db.collection("organizers").document(deviceId).set(new Organizer(deviceId, eventIds));
    }

    // Interface for listening to changes in the organizers collection
    public interface OrganizerChangeListener {
        void onOrganizersChanged(HashMap<String, Organizer> organizers);
    }
}
