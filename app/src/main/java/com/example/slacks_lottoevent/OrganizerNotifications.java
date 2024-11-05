package com.example.slacks_lottoevent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

public class OrganizerNotifications extends AppCompatActivity {

    FrameLayout frameLayout;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications_organizer);

        //Set Up ArrayAdapter, do getUsername() for every user in said category, change it in the case-by-case tabLayout system
        Intent intent = getIntent();
        frameLayout = (FrameLayout) findViewById(R.id.FrameLayout);
        tabLayout = (TabLayout) findViewById(R.id.tab_Layout);

        getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayout, new OrganizerWaitlistFragment())
                .addToBackStack(null)
                .commit();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selected_fragment = null;
                switch (tab.getPosition()){
                    case 0:
                        selected_fragment = new OrganizerWaitlistFragment();
                        break;
                    case 1:
                        selected_fragment = new OrganizerInvitedFragment();
                        break;
                    case 2:
                        selected_fragment = new OrganizerCancelledFragment();
                        break;
                    case 3:
                        selected_fragment = new OrganizerEnrolledFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayout, selected_fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
//                // Change the background color of the tab to white
//                tab.view.setBackgroundColor(Color.WHITE);
//
//                // Change the text color of the tab to pink
//                TextView tabTextView = (TextView) ((LinearLayout) tab.view).getChildAt(1);
//                if (tabTextView != null) {
//                    tabTextView.setTextColor(Color.parseColor("#FFC0CB")); // Pink color
//                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
}
