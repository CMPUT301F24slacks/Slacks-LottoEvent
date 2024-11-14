package com.example.slacks_lottoevent.database;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class EventDB {
    private static final FirebaseConnection db = FirebaseConnection.getInstance();
    private static final String COLLECTION_NAME = "events";
    private static final CollectionReference collection = db.getDatabase()
                                                            .collection(COLLECTION_NAME);
    private static EventDB instance;

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
     * Retrieves all events whose document IDs are in the given ArrayList.
     *
     * @param eventIds ArrayList of event IDs to retrieve events for.
     */
    public Task<QuerySnapshot> getEvents(ArrayList<String> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            // Return an empty Task if the input list is null or empty
            return Tasks.forResult(null);
        }

        // Use Firestore's `whereIn` method with `FieldPath.documentId()`
        return collection.whereIn(FieldPath.documentId(), eventIds).get();
    }

}
