package com.example.slacks_lottoevent.model;

import com.example.slacks_lottoevent.data.Organizer;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * OrganizerDB is a class that represents the database for organizers.
 */
public class OrganizerDB {
    private FirebaseFirestore db;
    private CollectionReference organizersRef;

    OrganizerDB() {
        db = FirebaseFirestore.getInstance();
        organizersRef = db.collection("organizers");
    }

    /**
     * Updates an organizer on the database. If the organizer does not exist, it will be created.
     * @param organizer the organizer to add/update
     */
    public void updateOrganizer(Organizer organizer) {
        organizersRef.document(organizer.getUserId()).set(organizer);
    }

    /**
     * Deletes an organizer from the database.
     * @param organizer the organizer to delete
     */
    public void deleteOrganizer(Organizer organizer) {
        organizersRef.document(organizer.getUserId()).delete();
    }

    /**
     * Gets an organizer from the database.
     * @param userId the user ID of the organizer to get
     * @return the organizer with the given user ID
     */
    public Organizer getOrganizer(String userId) {
        return organizersRef.document(userId).get().getResult().toObject(Organizer.class);
    }

    /**
     * Returns a bool if the organizer exists in the database.
     * @param userId the user ID of the organizer to check
     */
    public boolean organizerExists(String userId) {
        return organizersRef.document(userId).get().isSuccessful();
    }


}
