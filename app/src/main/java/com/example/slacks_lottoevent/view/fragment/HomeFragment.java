package com.example.slacks_lottoevent.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.slacks_lottoevent.R;
import com.example.slacks_lottoevent.Event;
import com.example.slacks_lottoevent.model.User;
import com.example.slacks_lottoevent.viewmodel.EntrantViewModel;
import com.example.slacks_lottoevent.viewmodel.EventViewModel;
import com.example.slacks_lottoevent.EventArrayAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private EntrantViewModel entrantViewModel;
    private EventViewModel eventViewModel;
    private ArrayList<Event> eventsList = new ArrayList<>();
    private EventArrayAdapter eventsListArrayAdapter;

    private User user;
    private String deviceId;

    // Ui elements
    private TextView instructions;
    private ListView eventsListView;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve the ViewModel scoped to the Activity
        entrantViewModel = new ViewModelProvider(requireActivity()).get(EntrantViewModel.class);
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);

        // Initialize the EventArrayAdapter
        eventsListArrayAdapter = new EventArrayAdapter(getContext(), eventsList);

        // Initialize UI elements
        instructions = view.findViewById(R.id.instructions_textview);
        eventsListView = view.findViewById(R.id.events_listview);
        eventsListView.setAdapter(eventsListArrayAdapter);

        user = User.getInstance();
        deviceId = user.getDeviceId();

        // Checks if the entrant in entrantViewModel exists
        if (entrantViewModel.getCurrentEntrant() == null) {
            // The entrant does not exist
            Log.d("HomeFragment", "Entrant does not exist.");

            // UI updates
            instructions.setVisibility(View.VISIBLE);
            eventsListView.setVisibility(View.GONE);
        } else {
            // The entrant exists
            Log.d("HomeFragment", "Entrant exists.");

            // UI updates
            instructions.setVisibility(View.GONE);
            updateEventList();
        }
    }

    public void updateEventList() {
        eventViewModel.getWaitlistedEvents().observe(getViewLifecycleOwner(), waitlistedEvents -> {
            if (waitlistedEvents != null && !waitlistedEvents.isEmpty()) {
                eventsList.clear(); // Clear the list before adding new events
                eventsList.addAll(waitlistedEvents); // Add all events from LiveData
                eventsListArrayAdapter.notifyDataSetChanged(); // Notify the adapter of the changes
            }
        });
    }




}
