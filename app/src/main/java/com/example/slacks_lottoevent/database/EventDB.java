package com.example.slacks_lottoevent.database;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QuerySnapshot;

public class EventDB {
    private static EventDB instance;
    private static final FirebaseConnection db = FirebaseConnection.getInstance();
    private static final String COLLECTION_NAME = "events";
    private static final CollectionReference collection = db.getDatabase().collection(COLLECTION_NAME);

    private EventDB() {
        // Private constructor to prevent instantiation
    }

    public static synchronized EventDB getInstance() {
        if (instance == null) {
            instance = new EventDB();
        }
        return instance;
    }

    /**
     * Returns all the events in the database asynchronously.
     * @return Task<QuerySnapshot> object representing the asynchronous operation.
     */
    public Task<QuerySnapshot> getEvents() {
        return collection.get();
    }

}
