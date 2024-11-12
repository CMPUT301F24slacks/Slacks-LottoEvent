package com.example.slacks_lottoevent.database;

import com.example.slacks_lottoevent.model.Facility;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;

public class FacilityDB {
    private static FacilityDB instance;
    private static final FirebaseConnection db = FirebaseConnection.getInstance();
    private static final String COLLECTION_NAME = "facilities";
    private static final CollectionReference collection = db.getDatabase().collection(COLLECTION_NAME);

    private FacilityDB() {
        // Private constructor to prevent instantiation
    }

    public static synchronized FacilityDB getInstance() {
        if (instance == null) {
            instance = new FacilityDB();
        }
        return instance;
    }

    /**
     * Adds a facility to the database asynchronously.
     */
    public Task<Void> addFacility(Facility facility) {
        return collection.document(facility.getDeviceId()).set(facility);
    }

    /**
     * Returns a boolean whether the facility with the given device ID key exists in the database.
     * @param deviceId Device ID of the facility to check for existence.
     */
    public Task<Boolean> facilityExists(String deviceId) {
        return collection.document(deviceId).get().continueWith(task -> task.getResult().exists());
    }

    /**
     * Returns a facility with the given device ID key from the database.
     */
    public Task<Facility> getFacility(String deviceId) {
        return collection.document(deviceId).get().continueWith(task -> task.getResult().toObject(Facility.class));
    }
}
