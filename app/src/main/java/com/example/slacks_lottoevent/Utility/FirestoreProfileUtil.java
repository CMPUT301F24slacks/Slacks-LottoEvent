package com.example.slacks_lottoevent.Utility;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * FirestoreProfileUtil is a utility class that provides a method to check if a user has signed up
 * for the app.
 */
public class FirestoreProfileUtil {

    /**
     * Check if a user has signed up for the app.
     * @param deviceId The device ID of the user.
     * @param callback The callback to be called when the result is available.
     */
    public static void checkIfSignedUp(String deviceId, FirestoreCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("profiles");
        collectionReference.whereEqualTo("deviceId", deviceId).limit(1).get()
                           .addOnCompleteListener(task -> {
                               if (task.isSuccessful()) {
                                   boolean isSignedUp = !task.getResult().isEmpty();
                                   callback.onResult(isSignedUp);
                               } else {
                                   callback.onResult(false); // Return false if there is an error
                               }
                           });
    }

    /**
     * The callback interface to be called when the result is available.
     */
    public interface FirestoreCallback {
        void onResult(boolean isSignedUp);
    }

}
