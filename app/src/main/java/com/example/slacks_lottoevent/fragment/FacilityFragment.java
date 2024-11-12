package com.example.slacks_lottoevent.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.slacks_lottoevent.R;
import com.example.slacks_lottoevent.model.User;
import com.example.slacks_lottoevent.viewmodel.FacilityViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FacilityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FacilityFragment extends Fragment {

    private User user;
    private FacilityViewModel facilityViewModel;

    // UI elements
    private Button create_button;
    private Button edit_button;

    public FacilityFragment() {
        // Required empty public constructor
    }

    public static FacilityFragment newInstance(String param1, String param2) {
        FacilityFragment fragment = new FacilityFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_facility, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = User.getInstance(getContext());
        facilityViewModel = new FacilityViewModel(user.getDeviceId());

        // UI elements
        create_button = view.findViewById(R.id.create_facility_button);
        edit_button = view.findViewById(R.id.edit_facility_button);


        if (facilityViewModel.getFacility().getValue() == null) {
            // Facility object is not set
            create_button.setVisibility(View.VISIBLE);
            edit_button.setVisibility(View.GONE);
        } else {
            // Facility object is set
            create_button.setVisibility(View.GONE);
            edit_button.setVisibility(View.VISIBLE);
        }

        // click listener for create facility button
        create_button.setOnClickListener(v -> {
            // Create a new facility
        });

        // click listener for edit facility button
        edit_button.setOnClickListener(v -> {
            // Edit the existing facility
        });
    }

}