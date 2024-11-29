package com.example.slacks_lottoevent.database;

import com.example.slacks_lottoevent.Facility;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;

public class FacilityDB {
    private static FacilityDB instance;
    private final FirebaseFirestore db;
    private final HashMap<String, Facility> facilitiesCache;
    private ListenerRegistration listenerRegistration;
    private FacilityChangeListener facilityChangeListener;

    private FacilityDB() {
        db = FirebaseFirestore.getInstance();
        facilitiesCache = new HashMap<>();
    }

    // Singleton pattern
    public static synchronized FacilityDB getInstance() {
        if (instance == null) {
            instance = new FacilityDB();
        }
        return instance;
    }

    // Start listening to the "facilities" collection for changes
    public void startListening() {
        if (listenerRegistration == null) {
            listenerRegistration = db.collection("facilities")
                    .addSnapshotListener((snapshots, e) -> {
                        if (e != null) {
                            e.printStackTrace();
                            return;
                        }
                        if (snapshots != null) {
                            facilitiesCache.clear();
                            for (DocumentSnapshot doc : snapshots.getDocuments()) {
                                facilitiesCache.put(doc.getId(), doc.toObject(Facility.class));
                            }
                            notifyFacilityChangeListener();
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

    // Set a listener to notify when facilities change
    public void setFacilityChangeListener(FacilityChangeListener listener) {
        this.facilityChangeListener = listener;
        startListening();
    }

    // Notify the listener that the facilities have changed
    private void notifyFacilityChangeListener() {
        if (facilityChangeListener != null) {
            facilityChangeListener.onFacilitiesChanged(new HashMap<>(facilitiesCache));
        }
    }

    // Interface for notifying facility changes
    public interface FacilityChangeListener {
        void onFacilitiesChanged(HashMap<String, Facility> updatedFacilities);
    }

    // Create a new facility in Firestore
    public Task<Void> updateFacility(String name, String streetAddress, String deviceId) {
        return db.collection("facilities").document(deviceId).set(new Facility(name, streetAddress, deviceId));
    }
}
