package com.example.slacks_lottoevent;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

public class AdminActivity extends AppCompatActivity {
    private FrameLayout frameLayout;
    private TabLayout tabLayout;

    /**
     * This method is called when the activity is first created.
     * It sets up the tab layout for the organizer to view the waitlist, invited, cancelled, and enrolled users.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

//        onBackPressed();

        frameLayout = (FrameLayout) findViewById(R.id.FrameLayoutAdmin);
        tabLayout = (TabLayout) findViewById(R.id.tab_Layout_Admin);

        getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayout, AdminProfiles.newInstance()) //AdminEvents()
                .addToBackStack(null)
                .commit();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selected_fragment = null;
                switch (tab.getPosition()){
                    case 0:
//                        selected_fragment = AdminEvents.newInstance(event);
                        break;
                    case 1:
//                        selected_fragment = AdminImages.newInstance(event);;
                        break;
                    case 2:
                        selected_fragment = AdminFacilities.newInstance();;
                        break;
                    case 3:
                        selected_fragment = AdminProfiles.newInstance();;
                        break;
                }
                if (selected_fragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.FrameLayout, selected_fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }
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


//        ImageView back = findViewById(R.id.back_button);
//        back.setOnClickListener(v -> {
//            onBackPressed();
//        });

    }
}
