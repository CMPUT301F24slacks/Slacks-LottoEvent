package com.example.slacks_lottoevent;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.slacks_lottoevent.databinding.ActivityEventsHomeBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

public class EventsHomeActivity extends AppCompatActivity {

    private ActivityEventsHomeBinding binding;
    private AppBarConfiguration appBarConfiguration;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEventsHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        TabLayout eventsTabs = findViewById(R.id.events_home_tab_layout); // Get the tab layout in EventsHomeActivity

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_events_home); // Get the navigation controller
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build(); // Build the app bar configuration

        eventsTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("My Events")) {
                    navController.navigate(R.id.MyEventsFragment);
                    // show the QR code scanner button
                    binding.qrCodeScannerFAB.setVisibility(View.VISIBLE);
                    // hide the create event button
                    binding.createEventFAB.setVisibility(View.GONE);
                }
                if (tab.getText().equals("Manage My Events")) {
                    navController.navigate(R.id.ManageMyEventsFragment);
                    // hide the QR code scanner button
                    binding.qrCodeScannerFAB.setVisibility(View.GONE);
                    // show the create event button
                    binding.createEventFAB.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Do nothing
            }
        });

        /*
         * QR code scanner button, opens the QR code scanner.
         */
        binding.qrCodeScannerFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventsHomeActivity.this,EventQrScanner.class);
                startActivity(intent);
            }
        });

        binding.createEventFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create event button
                Intent intent = new Intent(EventsHomeActivity.this, CreateEvent.class);
                startActivity(intent);

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
