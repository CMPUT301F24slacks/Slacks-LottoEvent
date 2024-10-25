package com.example.slacks_lottoevent;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

public class OrganizerMainEventsPageAcitvity extends AppCompatActivity {
    FrameLayout framelayout;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_main_activity);

        framelayout = (FrameLayout) findViewById(R.id.FrameLayout);
        tabLayout = (TabLayout) findViewById(R.id.tab_Layout);

        // Set default activity on start
        startActivity(new Intent(OrganizerMainEventsPageAcitvity.this, OrganizerManageEventsActivity.class));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Intent intent = null;
                switch (tab.getPosition()){
                    case 0:
                        intent = new Intent(OrganizerMainEventsPageAcitvity.this, OrganizerLookAtEventsActivity.class);
                        break;
                    case 1:
                        intent = new Intent(OrganizerMainEventsPageAcitvity.this, OrganizerManageEventsActivity.class);
                        break;
                    case 2:
                        intent = new Intent(OrganizerMainEventsPageAcitvity.this, OrganizerManageFacilityActivity.class);
                        break;
                }
                if (intent != null) {
                    startActivity(intent);
                }
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

