package com.example.slacks_lottoevent;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.slacks_lottoevent.databinding.FragmentMyEventsBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;

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
    private EntrantDB entrantDB;
    private EventDB eventDB;
    private Entrant entrant;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentMyEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        entrantRef = db.collection("entrants"); // Reference to entrants collection
        eventsRef = db.collection("events"); // Reference to events collection
        entrantDB = new EntrantDB();

        // Get the current user's ID
        String userId = Settings.Secure.getString(requireActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Get the entrant from the database
        if (entrantDB.entrantExists(userId)) {
            entrant = entrantDB.getEntrant(userId);
        }

        EventList eventList = new EventList();
        myEventsListView = binding.myEventsListView;
        eventArrayAdapter = new EventArrayAdapter(getContext(), eventList);
        myEventsListView.setAdapter(eventArrayAdapter);

        entrantRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null || entrant == null) {
                    return;
                }

                for (int i = 0; i < value.size(); i++) {
                    ArrayList<String> eventIds = entrant.getWaitlistedEvents();

                    // Get the events from the database and display them in the list view
                    for (String eventId : eventIds) {
                        Event event = eventDB.getEvent(eventId);
                        eventList.addEvent(event);
                    }
                }

                // Update the list view
                eventArrayAdapter.notifyDataSetChanged();
            }
        });




//        Profile tempUser = new Profile("John Doe", "123-456-7890", "123@gmail.com");
//        String tempDescription = "Join us for the Tech Innovators Summit 2024, where industry leaders, startups, and tech enthusiasts come together to explore the latest in AI, blockchain, cybersecurity, and more. Enjoy keynote talks, interactive workshops, and networking opportunities designed to inspire and connect innovators. Be part of the future of technology!";
//        String tempUserId = UUID.randomUUID().toString();
//        Organizer tempOrganizer = new Organizer(tempUserId);
//        Facility tempFacility = new Facility("facilityname", "9th Street", "111 Unit", "Edmonton", "Province", "country", "t6m0n5", "orgID", "devID");
//        tempOrganizer.setFacilityId(tempFacility.getFacilityId());
//        Event tempEvent = new Event("Tech Innovators Summit 2024", "2024-01-23", "14:00-15:00", "400",tempDescription, 1000, 0,  null, "randomeventID", false, null, false, false, false);        EventList tempEventList = new EventList();
//        tempEventList.addEvent(tempEvent);
//        tempEventList.addEvent(tempEvent);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
