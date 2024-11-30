package com.example.slacks_lottoevent.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.slacks_lottoevent.R;
import com.example.slacks_lottoevent.viewmodel.adapter.TabsAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ManageFragment extends Fragment {

    public ManageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage, container, false);

        // Access TabLayout and ViewPager2 from the inflated fragment layout
        TabLayout tabLayout = view.findViewById(R.id.manage_tab_layout);
        ViewPager2 viewPager = view.findViewById(R.id.manage_view_pager);

        // Set up the adapter for ViewPager2
        viewPager.setAdapter(new TabsAdapter(requireActivity()));

        // Link TabLayout and ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Events");
                    tab.setIcon(R.drawable.baseline_calendar_today_20);
                    break;
                case 1:
                    tab.setText("Facility");
                    tab.setIcon(R.drawable.outline_business_24);
                    break;
            }
        }).attach();

        return view;
    }
}
