package com.example.slacks_lottoevent;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * EntrantDB is a class that interacts with the Firebase Firestore database to perform CRUD operations on entrants.
 */
public class EntrantsDB {
    private FirebaseFirestore db;
    private CollectionReference entrantRef;

    public EntrantsDB() {
        db = FirebaseFirestore.getInstance();
        entrantRef = db.collection("entrants"); // Reference to entrants collection
    }

    /**
     * Updates an entrant in the database. If the entrant does not exist, it will be created.
     * @param entrant the entrant to update
     */
    public void updateEntrant(Entrant entrant) {
        entrantRef.document(entrant.getUserId()).set(entrant);
    }

    /**
     * Deletes an entrant from the database.
     * @param entrant the entrant to delete
     */
    public void deleteEntrant(Entrant entrant) {
        entrantRef.document(entrant.getUserId()).delete();
    }

    /**
     * Retrieves an entrant from the database and returns it as an Entrant object.
     * @param entrantId the ID of the entrant to retrieve
     * @return the entrant object
     */
    public Entrant getEntrant(String entrantId) {
        Task<DocumentSnapshot> task = entrantRef.document(entrantId).get();

        // Wait for the task to complete (blocking)
        try {
            Tasks.await(task);

            // Check if the task was successful and the document exists
            if (task.isSuccessful() && task.getResult() != null) {
                Log.d("EntrantsDB", "got the entrant");
                return task.getResult().toObject(Entrant.class);
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.d("EntrantsDB", "failed to get the entrant");
            Log.e("EntrantsDB", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Adds an event to the waitlisted events of an entrant.
     * @param entrantId the ID of the entrant
     * @param eventId the ID of the event to add
     */
    public void addWaitlistedEvent(String entrantId, String eventId) {
        entrantRef.document(entrantId).update("waitlistedEvents", FieldValue.arrayUnion(eventId));
    }

    /**
     * Returns the arraylist of waitlisted events for a given entrant.
     * @param entrantId the ID of the entrant
     *                  @return the arraylist of waitlisted events
     */
    public ArrayList<String> getWaitlistedEvents(String entrantId) {
        Task<DocumentSnapshot> task = entrantRef.document(entrantId).get();

        // Wait for the task to complete (blocking)
        try {
            Tasks.await(task);

            // Check if the task was successful and the document exists
            if (task.isSuccessful() && task.getResult() != null) {
                Log.d("EntrantsDB", "got the waitlisted events");
                return task.getResult().toObject(Entrant.class).getWaitlistedEvents();
            } else {
                Log.d("EntrantsDB", "failed to get the waitlisted events");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
