package com.example.slacks_lottoevent;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

public class OrganizerMainEventsPage extends AppCompatActivity {
    FrameLayout framelayout;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_main_activity);

        framelayout = (FrameLayout) findViewById(R.id.FrameLayout);
        tabLayout = (TabLayout) findViewById(R.id.tab_Layout);

        getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayout, new OrganizerMyEvents())
                .addToBackStack(null)
                .commit();


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selected_fragment = null;
                switch (tab.getPosition()){
                    case 0:
                        selected_fragment = new OrganizerLookAtEvents();
                        break;
                    case 1:
                        selected_fragment = new OrganizerMyEvents();
                        break;
                    case 2:
                        selected_fragment = new OrganizerManageFacility();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayout, selected_fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
