package com.example.slacks_lottoevent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

public class Organizer_MainActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    TabLayout tabLayout;
    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_main_activity);

        // Retrieve the Event object from the Intent
        Intent intent = getIntent();
        event = (Event) intent.getSerializableExtra("event");

        frameLayout = findViewById(R.id.FrameLayout);
        tabLayout = findViewById(R.id.tab_Layout);

        // Load OrganizerWaitlistFragment with waitlisted EntrantList data
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.FrameLayout, OrganizerWaitlistFragment.newInstance(event.getWaitlisted()))
                .addToBackStack(null)
                .commit();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selected_fragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        selected_fragment = OrganizerWaitlistFragment.newInstance(event.getWaitlisted());
                        break;
                    case 1:
                        selected_fragment = OrganizerInvitedFragment.newInstance(event.getInvited());
                        break;
                    case 2:
                        selected_fragment = OrganizerCancelledFragment.newInstance(event.getUnselected());
                        break;
                    case 3:
                        selected_fragment = OrganizerEnrolledFragment.newInstance(event.getFinalists());
                        break;
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.FrameLayout, selected_fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
}
