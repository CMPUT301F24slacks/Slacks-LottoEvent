package com.example.slacks_lottoevent.database;

import com.example.slacks_lottoevent.model.Facility;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;

/**
 * FacilityDB is a singleton class that manages the Firestore database for facilities.
 */
public class FacilityDB {
    private static FacilityDB instance;
    private final FirebaseFirestore db;
    private final HashMap<String, Facility> facilitiesCache;
    private ListenerRegistration listenerRegistration;
    private FacilityChangeListener facilityChangeListener;

    /**
     * Private constructor to prevent instantiation from outside the class.
     */
    private FacilityDB() {
        db = FirebaseFirestore.getInstance();
        facilitiesCache = new HashMap<>();
    }

    /**
     * Get the singleton instance of FacilityDB.
     * @return the singleton instance of FacilityDB
     */
    public static synchronized FacilityDB getInstance() {
        if (instance == null) {
            instance = new FacilityDB();
        }
        return instance;
    }

    /**
     * Start listening to Firestore updates for the facilities collection.
     */
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
                                                 facilitiesCache.put(doc.getId(),
                                                                     doc.toObject(Facility.class));
                                             }
                                             notifyFacilityChangeListener();
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
     * Set the facility change listener.
     * @param listener the facility change listener
     */
    public void setFacilityChangeListener(FacilityChangeListener listener) {
        this.facilityChangeListener = listener;
        startListening();
    }

    /**
     * Notify the facility change listener.
     * This method is called when the facilities in the Firestore database change.
     */
    private void notifyFacilityChangeListener() {
        if (facilityChangeListener != null) {
            facilityChangeListener.onFacilitiesChanged(new HashMap<>(facilitiesCache));
        }
    }

    /**
     * Add a new facility to the Firestore database.
     * @param name the name of the facility
     * @param streetAddress the street address of the facility
     * @param deviceId the device ID of the organizer
     * @return a Task representing the completion of the operation
     */
    public Task<Void> updateFacility(String name, String streetAddress, String deviceId) {
        return db.collection("facilities").document(deviceId)
                 .set(new Facility(name, streetAddress, deviceId));
    }

    /**
     * Interface for listening to changes in the facilities collection.
     */
    public interface FacilityChangeListener {

        /**
         * Called when the facilities in the Firestore database have changed.
         * @param updatedFacilities the updated facilities
         */
        void onFacilitiesChanged(HashMap<String, Facility> updatedFacilities);
    }
}
