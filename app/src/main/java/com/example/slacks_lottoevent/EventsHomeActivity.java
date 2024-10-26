package com.example.slacks_lottoevent;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.viewpager2.widget.ViewPager2;
import androidx.lifecycle.Lifecycle;

import com.example.slacks_lottoevent.databinding.ActivityEventsHomeBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;


public class EventsHomeActivity extends AppCompatActivity {

    private ActivityEventsHomeBinding binding;
    private AppBarConfiguration appBarConfiguration;
    private MaterialToolbar toolbar;

    private TabLayout tablayout;
    private ViewPager2 viewPager2;
    private MyFragmentAdapter adapter;
    private int currentTabPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEventsHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        /*
         * Tab Layout Selector Functionality
         */

        tablayout = findViewById(R.id.events_home_tab_layout);
        viewPager2 = findViewById(R.id.viewPager2);


        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new MyFragmentAdapter(this);
        viewPager2.setAdapter(adapter);

        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabPosition = tab.getPosition();
                viewPager2.setCurrentItem(tab.getPosition());
                updateFabIcon(currentTabPosition);
                updateFabFunctionality(currentTabPosition);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tablayout.selectTab(tablayout.getTabAt(position));
                currentTabPosition = position; // Update the current tab position
                updateFabIcon(position);
                updateFabFunctionality(position);
            }
        });

        updateFabIcon(0);
        updateFabFunctionality(0);


        /*
         * QR code scanner button, opens the QR code scanner.
         */
        binding.qrCodeScannerFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performFabAction(currentTabPosition);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAnchorView(R.id.qr_code_scanner_FAB)
//                        .setAction("Action", null).show();
            }
        });

        /*
         * Handle app bar title clicks here.
         */
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EventsHomeActivity.this, "Toolbar title clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Update FAB icon based on selected tab position
    private void updateFabIcon(int position) {
        if (position == 0) {
            binding.qrCodeScannerFAB.setImageResource(R.drawable.baseline_qr_code_scanner_36); // Replace with your QR code icon resource
        } else if (position == 1) {
            binding.qrCodeScannerFAB.setImageResource(R.drawable.baseline_add_event_36); // Replace with your plus icon resource
        }
    }

    // Update FAB functionality based on selected tab position
    private void updateFabFunctionality(int position) {
        // You can add any initialization or functionality here that should be set on tab change
        // For now, it’s empty as the action is determined in onClickListener
    }

    // Perform different actions based on the selected tab
    private void performFabAction(int position) {
        if (position == 0) {
            // Action for "Entrant Events" tab (e.g., open a dialog to add an entrant event)
            Toast.makeText(this, "Scanning QR code", Toast.LENGTH_SHORT).show();
            // Add code to launch an event entry form or dialog
        } else if (position == 1) {
            // Action for "Organizer Events" tab (e.g., open a QR code scanner)
            Toast.makeText(this, "Creating an Event", Toast.LENGTH_SHORT).show();
            // Add code to initiate QR code scanning
        }
    }

    /*
     * Inflate the menu; this adds items to the action bar if it is present.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_events_home, menu);
        return true;
    }

    /*
     * Handle action bar item clicks here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.profile) {
            return true;
        }
        if (id == R.id.notifications) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
