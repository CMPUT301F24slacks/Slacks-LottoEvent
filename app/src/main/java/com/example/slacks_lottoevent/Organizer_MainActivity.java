package com.example.slacks_lottoevent;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

public class Organizer_MainActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_main_activity);

        frameLayout = findViewById(R.id.FrameLayout);
        tabLayout = findViewById(R.id.tab_Layout);

        // Set up the custom back button
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // This will close Organizer_MainActivity and go back to the previous screen
            }
        });

        // Load the first fragment by default
        getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayout, new OrganizerWaitlistFragment())
                .addToBackStack(null)
                .commit();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selected_fragment = null;
                switch (tab.getPosition()) {
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
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
}
