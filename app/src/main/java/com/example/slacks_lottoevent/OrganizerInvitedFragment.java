package com.example.slacks_lottoevent;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class OrganizerInvitedFragment extends Fragment {

    private ListView listViewEntrantsInvited;
    private ArrayList<String> entrantNames;

    private static final String ARG_INVITED = "invited";

    public OrganizerInvitedFragment() {
        // Required empty public constructor
    }

    // Factory method to create new instance with invited entrants list
    public static OrganizerInvitedFragment newInstance(EntrantList invited) {
        OrganizerInvitedFragment fragment = new OrganizerInvitedFragment();
        Bundle args = new Bundle();
        ArrayList<String> names = new ArrayList<>();

        for (Entrant entrant : invited.getEntrants()) {
            names.add(entrant.getUser().getName());
        }

        args.putStringArrayList(ARG_INVITED, names);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            entrantNames = getArguments().getStringArrayList(ARG_INVITED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_invited, container, false);

        listViewEntrantsInvited = view.findViewById(R.id.listViewEntrantsInvited);

        // Set adapter with passed entrant names
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, entrantNames);
        listViewEntrantsInvited.setAdapter(adapter);

        return view;
    }
}
