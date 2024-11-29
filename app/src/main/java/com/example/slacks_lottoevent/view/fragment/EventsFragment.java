package com.example.slacks_lottoevent.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.slacks_lottoevent.CreateEvent;
import com.example.slacks_lottoevent.Event;
import com.example.slacks_lottoevent.OrganizerEventArrayAdapter;
import com.example.slacks_lottoevent.R;
import com.example.slacks_lottoevent.Utility.SnackbarUtils;
import com.example.slacks_lottoevent.viewmodel.EventViewModel;
import com.example.slacks_lottoevent.viewmodel.FacilityViewModel;
import com.example.slacks_lottoevent.viewmodel.OrganizerViewModel;

import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment {
    // UI elements
    private Button create_event_button;
    private ListView eventsListView;
    private OrganizerEventArrayAdapter organizerEventArrayAdapter;
    private ArrayList<Event> eventsList = new ArrayList<>();

    // ViewModels
    private EventViewModel eventViewModel;
    private FacilityViewModel facilityViewModel;
    private OrganizerViewModel organizerViewModel;

    public EventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        create_event_button = view.findViewById(R.id.create_event_button);
        eventsListView = view.findViewById(R.id.organizer_events_listview);
        organizerEventArrayAdapter = new OrganizerEventArrayAdapter(requireContext(), eventsList, false);
        eventsListView.setAdapter(organizerEventArrayAdapter);

        // Initialize ViewModels
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        facilityViewModel = new ViewModelProvider(requireActivity()).get(FacilityViewModel.class);
        organizerViewModel = new ViewModelProvider(requireActivity()).get(OrganizerViewModel.class);

        // Set a click listener for the create event button
        create_event_button.setOnClickListener(v -> {
            if (facilityViewModel.getCurrentFacilityLiveData().getValue() == null) {
                SnackbarUtils.promptSignUp(view, requireContext(), R.id.create_event_button);
            } else {
                // Navigate to CreateEventActivity
                Intent intent = new Intent(getActivity(), CreateEvent.class);
                startActivity(intent);
            }
        });

        // Observe the organizer object
        organizerViewModel.getCurrentOrganizerLiveData().observe(getViewLifecycleOwner(), organizer -> {
            if (organizer != null) {
                List<String> eventIds = organizer.getEvents();
                // Log all eventIds
                eventViewModel.updateOrganizerEvents(eventIds);
            } else {
                // Clear all event lists
                eventViewModel.updateOrganizerEvents(null);
            }
        });

        eventViewModel.getEventsLiveData().observe(getViewLifecycleOwner(), events -> {
            eventViewModel.updateOrganizerEvents(organizerViewModel.getCurrentOrganizerLiveData().getValue().getEvents());
        });

        // Observe the events the organizer is hosting
        eventViewModel.getHostingEventsLiveData().observe(getViewLifecycleOwner(), hostingEvents -> {
            if (hostingEvents != null) {
                eventsList.clear(); // Clear the current list
                eventsList.addAll(hostingEvents); // Add new events
                organizerEventArrayAdapter.notifyDataSetChanged(); // Notify adapter of changes
                Log.d("EventsFragment", "List updated: " + hostingEvents.size() + " events");
            }
        });
    }

}