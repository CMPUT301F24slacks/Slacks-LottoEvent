package com.example.slacks_lottoevent.viewmodel.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.slacks_lottoevent.view.fragment.EventsFragment;
import com.example.slacks_lottoevent.view.fragment.FacilityFragment;
/**
* TabsAdapter used to go back and fourth between the facility and manage my events fragment within the manage fragment.
 * */
public class TabsAdapter extends FragmentStateAdapter {

    public TabsAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new EventsFragment();
            case 1:
                return new FacilityFragment();
            default:
                return new EventsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Number of tabs
    }
}
