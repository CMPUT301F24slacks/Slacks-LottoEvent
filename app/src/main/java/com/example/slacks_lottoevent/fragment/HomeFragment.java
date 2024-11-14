package com.example.slacks_lottoevent.fragment;

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
import com.example.slacks_lottoevent.model.User;
import com.example.slacks_lottoevent.viewmodel.EntrantViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private EntrantViewModel entrantViewModel;
    private User user;
    private String deviceId;

    // Ui elements
    private TextView instructions;
    private ListView eventsList;

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

        // Initialize UI elements
        instructions = view.findViewById(R.id.instructions_textview);
        eventsList = view.findViewById(R.id.events_listview);

        user = User.getInstance(getContext());
        deviceId = user.getDeviceId();

        // Checks if the entrant in entrantViewModel exists
        if (entrantViewModel.getCurrentEntrant() == null) {
            // The entrant does not exist
            Log.d("HomeFragment", "Entrant does not exist.");

            // UI updates
            instructions.setVisibility(View.VISIBLE);
            eventsList.setVisibility(View.GONE);
        } else {
            // The entrant exists
            Log.d("HomeFragment", "Entrant exists.");

            // UI updates
            instructions.setVisibility(View.GONE);
        }
    }
}
