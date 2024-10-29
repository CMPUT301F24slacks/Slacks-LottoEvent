package com.example.slacks_lottoevent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.slacks_lottoevent.databinding.FragmentMyEventsBinding;

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

        String tempDescription = "Join us for the Tech Innovators Summit 2024, where industry leaders, startups, and tech enthusiasts come together to explore the latest in AI, blockchain, cybersecurity, and more. Enjoy keynote talks, interactive workshops, and networking opportunities designed to inspire and connect innovators. Be part of the future of technology!";
        Organizer tempOrganizer = new Organizer();
        Facility tempFacility = new Facility(tempOrganizer, "Facility Name", "111 9th St, Edmonton, Canada");
        tempOrganizer.setFacility(tempFacility);
        Event tempEvent = new Event(tempOrganizer, tempOrganizer.getFacility(), "Tech Innovators Summit 2024", "2024-01-23", "14:00-15:00", "400",tempDescription, 1000, 0,  null, "randomeventID", false);
        EventList tempEventList = new EventList();
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
