package com.example.slacks_lottoevent;


import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.slacks_lottoevent.databinding.FragmentMyEventsBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

/**
 * This class is a fragment that displays the list of events the user has signed up for.
 */
public class MyEventsFragment extends Fragment {

    private FragmentMyEventsBinding binding;
    private EventArrayAdapter eventArrayAdapter;
    private ListView myEventsListView;
    private FirebaseFirestore db;
    private CollectionReference entrantRef;
    private CollectionReference eventsRef;
    private Entrant entrant;
    private ListenerRegistration entrantListener;

    /**
     * This method is called when the fragment is created.
     * It inflates the layout for this fragment and returns the root view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container          The parent view that the fragment's UI should be attached to
     * @param savedInstanceState A Bundle object that contains the saved state of the fragment
     * @return The root view of the fragment
     */
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentMyEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * This method is called when the fragment's view is created.
     * It initializes the Firestore database and fetches the entrant object from the database.
     *
     * @param view               The root view of the fragment
     * @param savedInstanceState A Bundle object that contains the saved state of the fragment
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        entrantRef = db.collection("entrants"); // Reference to entrants collection
        eventsRef = db.collection("events"); // Reference to events collection

        // Get the current user's ID
        String userId = Settings.Secure.getString(requireActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Check if the entrant exists in the database
        entrantExists(userId, exists -> {
            if (exists) {
                // Fetch the entrant from the database asynchronously
                entrantRef.document(userId).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Convert the document snapshot to the Entrant object
                        entrant = documentSnapshot.toObject(Entrant.class);
                        Log.d("MyEventsFragment", "Fetched entrant: " + entrant);
                        // Proceed with using the entrant object (e.g., populate UI or data structures)
                    } else {
                        Log.e("MyEventsFragment", "Entrant document does not exist for userId: " + userId);
                    }
                }).addOnFailureListener(e -> {
                    Log.e("MyEventsFragment", "Error fetching entrant for userId: " + userId, e);
                });
            } else {
                Log.d("MyEventsFragment", "No entrant found for userId: " + userId);
            }
        });


        ArrayList<Event> eventList = new ArrayList<>();
        myEventsListView = binding.myEventsListView;
        eventArrayAdapter = new EventArrayAdapter(getContext(), eventList);
        myEventsListView.setAdapter(eventArrayAdapter);

        entrantListener = entrantRef.addSnapshotListener((value, error) -> {
            if (error != null) return;

            eventList.clear();
            entrantRef.document(userId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Entrant entrant = documentSnapshot.toObject(Entrant.class);
                    if (entrant != null) {
                        ArrayList<String> eventIds = entrant.getWaitlistedEvents();
                        if (eventIds != null) {
                            for (String eventId : eventIds) {
                                eventsRef.document(eventId).get().addOnSuccessListener(eventSnapshot -> {
                                    if (eventSnapshot.exists()) {
                                        Event event = eventSnapshot.toObject(Event.class);
                                        if (event != null && !eventList.contains(event)) {
                                            eventList.add(event);
                                            eventArrayAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });
        });
        eventArrayAdapter.notifyDataSetChanged();
    }

    /**
     * This method is called when the fragment is destroyed.
     * It removes the Firestore listener and sets the binding object to null.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (entrantListener != null) {
            entrantListener.remove(); // Remove the Firestore listener
        }
        binding = null;
    }

    /**
     * This method is called when the fragment is destroyed.
     * It sets the binding object to null.
     */
    public void entrantExists(String userId, Callback<Boolean> callback) {
        entrantRef.document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        boolean exists = task.getResult().exists();
                        Log.d("EntrantsDB", "Entrant exists for userId: " + userId + " -> " + exists);
                        callback.onComplete(exists);
                    } else {
                        Log.e("EntrantsDB", "Failed to fetch entrant for userId: " + userId, task.getException());
                        callback.onComplete(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("EntrantsDB", "Error fetching entrant for userId: " + userId, e);
                    callback.onComplete(false);
                });
    }
}
