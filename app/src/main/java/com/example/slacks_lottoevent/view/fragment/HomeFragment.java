package com.example.slacks_lottoevent.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.slacks_lottoevent.model.Entrant;
import com.example.slacks_lottoevent.model.Event;
import com.example.slacks_lottoevent.viewmodel.adapter.EventArrayAdapter;
import com.example.slacks_lottoevent.R;
import com.example.slacks_lottoevent.viewmodel.EntrantViewModel;
import com.example.slacks_lottoevent.viewmodel.EventViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment that displays various events the user is either waitlisted, canceled, unselected and attending.
 * The event data is observed and updated in real-time using ViewModels.
 * */
public class HomeFragment extends Fragment {

    private EntrantViewModel entrantViewModel;
    private EventViewModel eventViewModel;
    private final ArrayList<Event> eventsList = new ArrayList<>();
    private EventArrayAdapter eventsListArrayAdapter;

    // Ui elements
    private ListView eventsListView;

    // Event IDs
    private List<String> waitlistedIds;
    private List<String> unselectedIds;
    private List<String> invitedIds;
    private List<String> attendingIds;
    /**
     * Static factory method to create a new instance of HomeFragment.
     * @return A new instance of HomeFragment.
     */
    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }
    /**
     * Called when the fragment is being created.
     * Can be used to initialize any required resources or variables before the view is created.
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    /**
     * Inflates the layout for the fragment
     * @param inflater The LayoutInflanter object used to inflate views in the fragment.
     * @param container parent view that the fragment's UI should be attached to.
     * @param savedInstanceState fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    /**
     *
     * This method initializes UI elements, sets up ViewModel observers.
     * @param view The view returned by onCreateView
     * @param savedInstanceState If not null, this fragment is being re-constructed from a previously saved state.
     *
     * */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve the ViewModel scoped to the Activity
        entrantViewModel = new ViewModelProvider(requireActivity()).get(EntrantViewModel.class);
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);

        // Initialize the EventArrayAdapter
        eventsListArrayAdapter = new EventArrayAdapter(getContext(), eventsList);

        // Initialize UI elements
        eventsListView = view.findViewById(R.id.events_listview);
        eventsListView.setAdapter(eventsListArrayAdapter);

        // Observe the events the entrant is involved in
        entrantViewModel.getCurrentEntrantLiveData().observe(getViewLifecycleOwner(), entrant -> {
            if (entrant != null) {
                updateEventIDs(entrant);
                eventViewModel.updateEventLists(waitlistedIds, unselectedIds, invitedIds,
                                                attendingIds);
            } else {
                // Clear all event lists
                eventViewModel.updateEventLists(null, null, null, null);
            }
        });

        // Observe any changes to the events list
        eventViewModel.getEventsLiveData().observe(getViewLifecycleOwner(), events -> {
            if (events != null && entrantViewModel.getCurrentEntrantLiveData().getValue() != null) {
                updateEventIDs(entrantViewModel.getCurrentEntrantLiveData().getValue());
                eventViewModel.updateEventLists(waitlistedIds, unselectedIds, invitedIds,
                                                attendingIds);
            }
        });

        // Observe any changes to the entrant events list livedata
        eventViewModel.getEntrantEventsLiveData()
                      .observe(getViewLifecycleOwner(), entrantEvents -> {
                          if (entrantEvents != null) {
                              Log.d("HomeFragment", "Entrant Events: " + entrantEvents);
                              eventsList.clear();
                              eventsList.addAll(entrantEvents);
                              eventsListArrayAdapter.notifyDataSetChanged();
                          } else {
                              Log.d("HomeFragment", "Entrant Events: null");
                              eventsList.clear();
                              eventsListArrayAdapter.notifyDataSetChanged();
                          }
                      });
    }

    private void updateEventIDs(Entrant entrant) {
        waitlistedIds = entrant.getWaitlistedEvents();
        unselectedIds = entrant.getUninvitedEvents();
        invitedIds = entrant.getInvitedEvents();
        attendingIds = entrant.getFinalistEvents();
    }

}
