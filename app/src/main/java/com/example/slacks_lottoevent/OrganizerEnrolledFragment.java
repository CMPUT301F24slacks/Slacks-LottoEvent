package com.example.slacks_lottoevent;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class OrganizerEnrolledFragment extends Fragment {

    private ListView listViewEntrantsEnrolled;
    private ArrayList<String> entrantNames;

    private static final String ARG_ENROLLED = "enrolled";

    public OrganizerEnrolledFragment() {
        // Required empty public constructor
    }

    // Factory method to create new instance with enrolled entrants list
    public static OrganizerEnrolledFragment newInstance(EntrantList enrolled) {
        OrganizerEnrolledFragment fragment = new OrganizerEnrolledFragment();
        Bundle args = new Bundle();
        ArrayList<String> names = new ArrayList<>();

        for (Entrant entrant : enrolled.getEntrants()) {
            names.add(entrant.getUser().getName());
        }

        args.putStringArrayList(ARG_ENROLLED, names);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            entrantNames = getArguments().getStringArrayList(ARG_ENROLLED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_enrolled, container, false);

        listViewEntrantsEnrolled = view.findViewById(R.id.listViewEntrantsEnrolled);

        // Set adapter with passed entrant names
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, entrantNames);
        listViewEntrantsEnrolled.setAdapter(adapter);

        return view;
    }
}
