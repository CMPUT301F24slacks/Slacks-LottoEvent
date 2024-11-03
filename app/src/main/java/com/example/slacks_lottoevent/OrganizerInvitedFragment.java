package com.example.slacks_lottoevent;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganizerInvitedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganizerInvitedFragment extends Fragment {

    private ListView listViewEntrantsInvited;
    private ArrayList<String> dummyEntrants;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrganizerInvitedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrganizerInvitedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrganizerInvitedFragment newInstance(String param1, String param2) {
        OrganizerInvitedFragment fragment = new OrganizerInvitedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_invited, container, false);

        // Setup ListView
        listViewEntrantsInvited = view.findViewById(R.id.listViewEntrantsInvited);

        // Dummy data
        dummyEntrants = new ArrayList<>();
        dummyEntrants.add("Mike");
        dummyEntrants.add("November");

        // Adapter to populate ListView
        EntrantListsArrayAdapter adapter = new EntrantListsArrayAdapter(getContext(), dummyEntrants);
        listViewEntrantsInvited.setAdapter(adapter);

        return view;
    }
}