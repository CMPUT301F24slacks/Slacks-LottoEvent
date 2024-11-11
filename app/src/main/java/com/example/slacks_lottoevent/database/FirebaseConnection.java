package com.example.slacks_lottoevent.database;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * FirebaseConnection class is a singleton class that represents a connection to the Firebase database.
 */
public class FirebaseConnection {
    private static FirebaseConnection instance;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseConnection() {
        // Private constructor to prevent instantiation
    }

    public static synchronized FirebaseConnection getInstance() {
        if (instance == null) {
            instance = new FirebaseConnection();
        }
        return instance;
    }

    // Add methods to interact with the Firebase database here
    public FirebaseFirestore getDatabase() {
        return db;
    }

}


