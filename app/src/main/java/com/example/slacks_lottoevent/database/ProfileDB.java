package com.example.slacks_lottoevent.database;

import com.example.slacks_lottoevent.model.Profile;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;

/**
 * ProfileDB is a singleton class that manages the Firestore database for profiles.
 */
public class ProfileDB {
    private static ProfileDB instance;
    private final FirebaseFirestore db;
    private final HashMap<String, Profile> profilesCache;
    private ListenerRegistration listenerRegistration;
    private ProfileChangeListener profileChangeListener;

    /**
     * Private constructor to prevent instantiation from outside the class.
     */
    private ProfileDB() {
        db = FirebaseFirestore.getInstance();
        profilesCache = new HashMap<>();
    }

    /**
     * Get the singleton instance of ProfileDB.
     * @return The singleton instance of ProfileDB.
     */
    public static synchronized ProfileDB getInstance() {
        if (instance == null) {
            instance = new ProfileDB();
        }
        return instance;
    }

    /**
     * Start listening to Firestore updates for the profiles collection.
     */
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
                                                 profilesCache.put(doc.getId(),
                                                                   doc.toObject(Profile.class));
                                             }
                                             notifyProfileChangeListener();
                                         }
                                     });
        }
    }

    /**
     * Stop listening to Firestore updates.
     */
    public void stopListening() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }

    /**
     * Set the listener for profile changes.
     * @param listener the listener for profile changes
     */
    public void setProfileChangeListener(ProfileChangeListener listener) {
        this.profileChangeListener = listener;
        startListening();
    }

    /**
     * Notify the profile change listener of changes in the profiles collection.
     */
    private void notifyProfileChangeListener() {
        if (profileChangeListener != null) {
            profileChangeListener.onProfilesChanged(new HashMap<>(profilesCache));
        }
    }

    /**
     * Get the profiles cache.
     * @param profile the profile to add to the cache
     */
    public void updateProfile(Profile profile) {
        db.collection("profiles").document(profile.getDeviceId()).set(profile);
    }

    /**
     * Interface for listening to changes in the profiles cache.
     */
    public interface ProfileChangeListener {

        /**
         * Called when the profiles have changed.
         * @param updatedProfiles the updated profiles
         */
        void onProfilesChanged(HashMap<String, Profile> updatedProfiles);
    }
}
