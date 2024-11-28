package com.example.slacks_lottoevent;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.ExecutionException;

public class FirestoreProfileUtil {

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
    public interface FirestoreCallback {
        void onResult(boolean isSignedUp);
    }


}
