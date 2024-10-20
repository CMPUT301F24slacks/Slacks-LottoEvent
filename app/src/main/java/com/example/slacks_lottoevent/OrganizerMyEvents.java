package com.example.slacks_lottoevent;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.slacks_lottoevent.databinding.FragmentFirstBinding;
import com.example.slacks_lottoevent.databinding.FragmentOrganizerManageEventsBinding;

public class OrganizerMyEvents extends Fragment {
//    Purpose of this class is too look at events, and when clicked on event edit you are taken to that event and when you press button in bottom corner you add event

//    TODO: After make card class of events you need to click on edit button and be redirected to edit event

//    TODO: Press plus button in bottom corner to go to create event - can only do if there is a Facility

    private FragmentOrganizerManageEventsBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentOrganizerManageEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.createEvent.setOnClickListener(v -> {
            NavHostFragment.findNavController(OrganizerMyEvents.this)
                    .navigate(R.id.action_ManageEvents_to_CreateEvents);

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}
