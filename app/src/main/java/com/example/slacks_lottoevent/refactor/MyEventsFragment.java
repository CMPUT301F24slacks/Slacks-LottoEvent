package com.example.slacks_lottoevent.refactor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.slacks_lottoevent.databinding.FragmentMyEventsBinding;
import com.example.slacks_lottoevent.model.Facility;
import com.example.slacks_lottoevent.model.Organizer;

import java.util.UUID;

/**
 * This class is a fragment that displays the list of events the user has signed up for.
 */
public class MyEventsFragment extends Fragment {

    private FragmentMyEventsBinding binding;
    private EventArrayAdapter eventArrayAdapter;
    private ListView myEventsListView;

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


        Profile tempUser = new Profile("John Doe", "123-456-7890", "123@gmail.com");
        String tempDescription = "Join us for the Tech Innovators Summit 2024, where industry leaders, startups, and tech enthusiasts come together to explore the latest in AI, blockchain, cybersecurity, and more. Enjoy keynote talks, interactive workshops, and networking opportunities designed to inspire and connect innovators. Be part of the future of technology!";
        String tempUserId = UUID.randomUUID().toString();
        Organizer tempOrganizer = new Organizer(tempUserId);
        Facility tempFacility = new Facility("facilityname", "9th Street", "111 Unit", "Edmonton", "Province", "country", "t6m0n5", "orgID", "devID");
        tempOrganizer.setFacilityId(tempFacility.getFacilityId());
        Event tempEvent = new Event("Tech Innovators Summit 2024", "2024-01-23", "14:00-15:00", "400",tempDescription, 1000, 0,  null, "randomeventID", false, null, false, false, false);        EventList tempEventList = new EventList();
        tempEventList.addEvent(tempEvent);
        tempEventList.addEvent(tempEvent);

        myEventsListView = binding.myEventsListView;
        eventArrayAdapter = new EventArrayAdapter(getContext(), tempEventList);
        myEventsListView.setAdapter(eventArrayAdapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}