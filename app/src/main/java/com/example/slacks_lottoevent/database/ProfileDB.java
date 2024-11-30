package com.example.slacks_lottoevent.database;

import com.example.slacks_lottoevent.Profile;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;

public class ProfileDB {
    private static ProfileDB instance;
    private final FirebaseFirestore db;
    private final HashMap<String, Profile> profilesCache;
    private ListenerRegistration listenerRegistration;
    private ProfileChangeListener profileChangeListener;

    private ProfileDB() {
        db = FirebaseFirestore.getInstance();
        profilesCache = new HashMap<>();
    }

    // Singleton pattern
    public static synchronized ProfileDB getInstance() {
        if (instance == null) {
            instance = new ProfileDB();
        }
        return instance;
    }

    // Start listening to the "profiles" collection for changes
    public void startListening() {
        if (listenerRegistration == null) {
            listenerRegistration = db.collection("profiles")
                    .addSnapshotListener((snapshots, e) -> {
                        if (e != null) {
                            e.printStackTrace();
                            return;
                        }
                        if (snapshots != null) {
                            profilesCache.clear();
                            for (DocumentSnapshot doc : snapshots.getDocuments()) {
                                profilesCache.put(doc.getId(), doc.toObject(Profile.class));
                            }
                            notifyProfileChangeListener();
                        }
                    });
        }
    }

    // Stop listening to Firestore updates
    public void stopListening() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }

    // Set a listener to notify when profiles change
    public void setProfileChangeListener(ProfileChangeListener listener) {
        this.profileChangeListener = listener;
        startListening();
    }

    // Notify the ViewModel about the updated profiles
    private void notifyProfileChangeListener() {
        if (profileChangeListener != null) {
            profileChangeListener.onProfilesChanged(new HashMap<>(profilesCache));
        }
    }

    // Interface for notifying profile changes
    public interface ProfileChangeListener {
        void onProfilesChanged(HashMap<String, Profile> updatedProfiles);
    }

    // Update a profile in Firestore
    public void updateProfile(Profile profile) {
        db.collection("profiles").document(profile.getDeviceId()).set(profile);
    }
}
