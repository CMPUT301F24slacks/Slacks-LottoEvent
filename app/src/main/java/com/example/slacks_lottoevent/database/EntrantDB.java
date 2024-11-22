package com.example.slacks_lottoevent.database;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;

public class EntrantDB {
    private static final FirebaseConnection db = FirebaseConnection.getInstance();
    private static final String COLLECTION_NAME = "entrants";
    private static final CollectionReference collection = db.getDatabase()
                                                            .collection(COLLECTION_NAME);
    private static EntrantDB instance;

    private EntrantDB() {
        // Private constructor to prevent instantiation
    }

    public static synchronized EntrantDB getInstance() {
        if (instance == null) {
            instance = new EntrantDB();
        }
        return instance;
    }

    // Add methods to interact with the database here

    public ListenerRegistration getEntrantSnapshotListener(String deviceId, EventListener<DocumentSnapshot> listener) {
        return collection.document(deviceId).addSnapshotListener(listener);
    }

    /**
     * Returns if the entrant with the given device ID exists in the database.
     *
     * @param deviceId Device ID of the entrant to check for existence.
     */
    public Task<Boolean> entrantExists(String deviceId) {
        return collection.document(deviceId).get().continueWith(task -> task.getResult().exists());
    }

    /**
     * Returns the entrant object with the given device ID from the database.
     *
     * @param deviceId Device ID of the entrant to retrieve.
     */
    public Task<DocumentSnapshot> getEntrant(String deviceId) {
        return collection.document(deviceId).get();
    }
}
