package com.example.slacks_lottoevent.model;

import com.example.slacks_lottoevent.data.Facility;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * FacilityDB is a class that represents the database for facilities.
 */
public class FacilityDB {
    private FirebaseFirestore db;
    private CollectionReference facilitiesRef;

    FacilityDB() {
        db = FirebaseFirestore.getInstance();
        facilitiesRef = db.collection("facilities");
    }

    /**
     * Updates a facility on the database. If the facility does not exist, it will be created.
     * @param facility the facility to add/update
     */
    public void updateFacility(Facility facility) {
        facilitiesRef.document(facility.getFacilityId()).set(facility);
    }

    /**
     * Deletes a facility from the database.
     * @param facility the facility to delete
     */
    public void deleteFacility(Facility facility) {
        facilitiesRef.document(facility.getFacilityId()).delete();
    }

    /**
     * Gets a facility from the database.
     * @param facilityId the facility ID of the facility to get
     * @return the facility with the given facility ID
     */
    public Facility getFacility(String facilityId) {
        return facilitiesRef.document(facilityId).get().getResult().toObject(Facility.class);
    }
}
