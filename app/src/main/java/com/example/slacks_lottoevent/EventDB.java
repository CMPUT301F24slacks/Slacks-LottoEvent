package com.example.slacks_lottoevent;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDB {
    private FirebaseFirestore db;
    private CollectionReference eventRef;

    public EventDB() {
        db = FirebaseFirestore.getInstance();
        eventRef = db.collection("events"); // Reference to events collection
    }

    /**
     * Updates an event in the database. If the event does not exist, it will be created.
     * @param event the event to update
     */
    public void updateEvent(Event event) {
        eventRef.document(event.getEventID()).set(event);
    }

    /**
     * Deletes an event from the database.
     * @param event the event to delete
     */
    public void deleteEvent(Event event) {
        eventRef.document(event.getEventID()).delete();
    }

    /**
     * Retrieves an event from the database and returns it as an Event object.
     * @param eventId the ID of the event to retrieve
     * @return the event object
     */
    public Event getEvent(String eventId) {
        Task<DocumentSnapshot> task = eventRef.document(eventId).get();

        // Wait for the task to complete (blocking)
        try {
            Tasks.await(task);

            // Check if the task was successful and the document exists
            if (task.isSuccessful() && task.getResult() != null) {
                return task.getResult().toObject(Event.class);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Updates
     */
}
