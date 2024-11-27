package com.example.slacks_lottoevent;

public interface FacilityDeletionCallback {
    /**
     * Called when a facility is successfully deleted.
     *
     * @param facilityId The ID of the deleted facility.
     */
    void onFacilityDeleted(String facilityId);
}
