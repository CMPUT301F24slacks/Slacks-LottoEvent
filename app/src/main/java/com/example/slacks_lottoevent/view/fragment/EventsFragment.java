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

import com.example.slacks_lottoevent.view.CreateEventActivity;
import com.example.slacks_lottoevent.model.Event;
import com.example.slacks_lottoevent.viewmodel.adapter.OrganizerEventArrayAdapter;
import com.example.slacks_lottoevent.R;
import com.example.slacks_lottoevent.Utility.SnackbarUtils;
import com.example.slacks_lottoevent.viewmodel.EventViewModel;
import com.example.slacks_lottoevent.viewmodel.OrganizerViewModel;
import com.example.slacks_lottoevent.viewmodel.ProfileViewModel;

import java.util.ArrayList;
import java.util.List;
/**
 * A fragment that displays and manages events hosted by the organizer.
 * This fragment provides functionality to view hosted events and navigate to the event creation page.
 */
public class EventsFragment extends Fragment {
    // UI elements
    private Button create_event_button;
    private ListView eventsListView;
    private OrganizerEventArrayAdapter organizerEventArrayAdapter;
    private final ArrayList<Event> eventsList = new ArrayList<>();

    // ViewModels
    private EventViewModel eventViewModel;
    private OrganizerViewModel organizerViewModel;
    private ProfileViewModel profileViewModel;
    /**
     *
     * Default constructor required for a fragment.
     *
     * */
    public EventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    /**
     * Called to create the view hierarchy associated with the fragment.
     * Inflates the layout for the fragment.
     *
     * @param inflater           The LayoutInflater object used to inflate views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event, container, false);
    }
    /**
     *
     * This method initializes UI elements, sets up ViewModel observers, and handles user interactions.
      @param view The view returned by onCreateView
      @param savedInstanceState If not null, this fragment is being re-constructed from a previously saved state.
     *
     * */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        create_event_button = view.findViewById(R.id.create_event_button);
        eventsListView = view.findViewById(R.id.organizer_events_listview);
        organizerEventArrayAdapter = new OrganizerEventArrayAdapter(requireContext(), eventsList,
                                                                    false);
        eventsListView.setAdapter(organizerEventArrayAdapter);

        // Initialize ViewModels
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        organizerViewModel = new ViewModelProvider(requireActivity()).get(OrganizerViewModel.class);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        // Set a click listener for the create event button
        create_event_button.setOnClickListener(v -> {
            if (profileViewModel.getCurrentProfileLiveData().getValue() ==
                null) { // Check if the user has a profile
                SnackbarUtils.promptSignUp(view, requireContext(), R.id.create_event_button);
            } else if (organizerViewModel.getCurrentOrganizerLiveData().getValue() ==
                       null) { // Check if the user is an organizer
                SnackbarUtils.promptCreateFacility(view, requireContext(),
                                                   R.id.create_event_button);
            } else {
                // Navigate to CreateEventActivity
                Intent intent = new Intent(getActivity(), CreateEventActivity.class);
                startActivity(intent);
            }
        });

        // Observe the organizer object
        organizerViewModel.getCurrentOrganizerLiveData()
                          .observe(getViewLifecycleOwner(), organizer -> {
                              if (organizer != null) {
                                  List<String> eventIds = organizer.getEvents();
                                  // Log all eventIds
                                  eventViewModel.updateOrganizerEvents(eventIds);
                              } else {
                                  // Clear all event lists
                                  eventViewModel.updateOrganizerEvents(null);
                              }
                          });

        // Observe any changes in the events list
        eventViewModel.getEventsLiveData().observe(getViewLifecycleOwner(), events -> {
            if (events != null &&
                organizerViewModel.getCurrentOrganizerLiveData().getValue() != null) {
                List<String> eventIds = organizerViewModel.getCurrentOrganizerLiveData().getValue()
                                                          .getEvents();
                eventViewModel.updateOrganizerEvents(eventIds);
            }
        });

        // Observe the events the organizer is hosting
        eventViewModel.getHostingEventsLiveData()
                      .observe(getViewLifecycleOwner(), hostingEvents -> {
                          if (hostingEvents != null) {
                              eventsList.clear(); // Clear the current list
                              eventsList.addAll(hostingEvents); // Add new events
                              organizerEventArrayAdapter.notifyDataSetChanged(); // Notify adapter of changes
                              Log.d("EventsFragment",
                                    "List updated: " + hostingEvents.size() + " events");
                          }
                      });
    }

}