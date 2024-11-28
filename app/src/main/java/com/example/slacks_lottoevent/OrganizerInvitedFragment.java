package com.example.slacks_lottoevent;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganizerInvitedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganizerInvitedFragment extends Fragment {

    private ListView listViewEntrantsInvited;
    private Event event;
    private static final String ARG_EVENT = "current_event";
    private FirebaseFirestore db;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     //     * @param param1 Parameter 1.
     //     * @param param2 Parameter 2.
     * @return A new instance of fragment OrganizerFirstFragment.
     */
    public OrganizerInvitedFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     //     * @param param1 Parameter 1.
     //     * @param param2 Parameter 2.
     * @return A new instance of fragment OrganizerFirstFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrganizerInvitedFragment newInstance(Event event) {
        OrganizerInvitedFragment fragment = new OrganizerInvitedFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT, event);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     //     * @param param1 Parameter 1.
     //     * @param param2 Parameter 2.
     * @return A new instance of fragment OrganizerFirstFragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable(ARG_EVENT);
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     //     * @param param1 Parameter 1.
     //     * @param param2 Parameter 2.
     * @return A new instance of fragment OrganizerFirstFragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_organizer_invited, container, false);
        listViewEntrantsInvited = view.findViewById(R.id.listViewEntrantsInvited);

        ArrayList<String> entrantNames = new ArrayList<>(); // List to hold retrieved names
        EntrantListsArrayAdapter adapter = new EntrantListsArrayAdapter(getContext(), entrantNames);
        listViewEntrantsInvited.setAdapter(adapter); // Set the adapter once

        //test event ID 0c781495-f91e-4648-9bb0-c390f558db10, This is the event!ve

        if (event != null) {
            ArrayList<String> deviceIds = event.getSelected();

            for (String deviceId : deviceIds) {
                // Query Firestore for each profile by device ID
                db.collection("profiles").document(deviceId).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String name = document.getString("name"); // Adjust to match your document structure
                            entrantNames.add(name);

                            // Notify the adapter that the data has changed
                            adapter.notifyDataSetChanged();                        }
                    } else {
                        Log.d("Firestore", "Error getting document: ", task.getException());
                    }
                });
            }
        }
        return view;
    }

}