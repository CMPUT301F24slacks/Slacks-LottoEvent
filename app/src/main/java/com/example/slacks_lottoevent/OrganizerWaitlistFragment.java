package com.example.slacks_lottoevent;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class OrganizerWaitlistFragment extends Fragment {

    private ListView listViewEntrantsWaitlisted;
    private ArrayList<String> entrantNames;

    private static final String ARG_WAITLISTED = "waitlisted";

    public OrganizerWaitlistFragment() {
        // Required empty public constructor
    }

    // Factory method to create new instance with waitlisted entrants
    public static OrganizerWaitlistFragment newInstance(EntrantList waitlisted) {
        OrganizerWaitlistFragment fragment = new OrganizerWaitlistFragment();
        Bundle args = new Bundle();
        ArrayList<String> names = new ArrayList<>();

        // Convert EntrantList to ArrayList of String names for display
        for (Entrant entrant : waitlisted.getEntrants()) {
            names.add(entrant.getUser().getName()); // assuming Entrant has a getName() method
        }

        args.putStringArrayList(ARG_WAITLISTED, names);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            entrantNames = getArguments().getStringArrayList(ARG_WAITLISTED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_organizer_waitlist, container, false);

        // Setup ListView
        listViewEntrantsWaitlisted = view.findViewById(R.id.listViewEntrantsWaitlisted);

        // Set adapter with passed entrant names
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, entrantNames);
        listViewEntrantsWaitlisted.setAdapter(adapter);

        return view;
    }
}
