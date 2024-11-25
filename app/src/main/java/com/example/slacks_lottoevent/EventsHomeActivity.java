package com.example.slacks_lottoevent;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.slacks_lottoevent.databinding.ActivityEventsHomeBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;


/**
 * EventsHomeActivity is the main activity for the Events Home screen.
 */
public class EventsHomeActivity extends AppCompatActivity {

    private ActivityEventsHomeBinding binding;
    private AppBarConfiguration appBarConfiguration;
    private MaterialToolbar toolbar;
    private FacilityViewModel facilityViewModel;
    private Boolean hasFacility = null;

    /**
     * onCreate method for the EventsHomeActivity.
     * This method initializes the activity and sets up the toolbar.
     *
     * @param savedInstanceState The saved instance state
     */
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
        facilityViewModel = new ViewModelProvider(this).get(FacilityViewModel.class);


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

        facilityViewModel.getFacilityStatus().observe(this, observedHasFacility -> {
            if (observedHasFacility != null) {
                // Update the class-level variable with the observed value
                this.hasFacility = observedHasFacility;
            } else {
                // Handle the case where observedHasFacility is null (optional)
                this.hasFacility = false;
            }
        });

        /*
         * Create event button, opens the create event screen upon being clicked.
         */
        binding.createEventFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("SlacksLottoEventUserInfo", MODE_PRIVATE);
                boolean isSignedUp = sharedPreferences.getBoolean("isSignedUp", false);
                if (!isSignedUp){
                    new AlertDialog.Builder(EventsHomeActivity.this)
                            .setTitle("Sign-Up Required")
                            .setMessage("In order to create an event, we first need to collect some information about you.")
                            .setPositiveButton("Proceed", (dialog, which) -> {
                                Intent signUpIntent = new Intent(EventsHomeActivity.this, SignUpActivity.class);
                                startActivity(signUpIntent);
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .show();
                }
                else if(hasFacility == null || !hasFacility){
                    //Toast.makeText(this, "Please create a facility first!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(EventsHomeActivity.this, "Please create a facility first!", Toast.LENGTH_SHORT).show();
                }
                else{
                    // open the create event page
                    Intent intent = new Intent(EventsHomeActivity.this, CreateEvent.class);
                    startActivity(intent);
                }

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
     * @param menu The menu to inflate
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_events_home, menu);
        return true;
    }

    /*
     * Handle action bar item clicks here.
     * @param item The menu item that was clicked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.profile) {
            // if the user is not signed up, redirect to the sign up page
            SharedPreferences sharedPreferences = getSharedPreferences("SlacksLottoEventUserInfo", MODE_PRIVATE);
            if (!sharedPreferences.getBoolean("isSignedUp", false)) {
                new AlertDialog.Builder(this)
                        .setTitle("Sign-Up Required")
                        .setMessage("In order to join an event, we need to collect some information about you.")
                        .setPositiveButton("Proceed", (dialog, which) -> {
                            Intent signUpIntent = new Intent(this, SignUpActivity.class);
                            startActivity(signUpIntent);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
                return true;
            } else {
                Intent intent = new Intent(EventsHomeActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }
        }
        if (id == R.id.notifications) {
//            Intent intent = new Intent(EventsHomeActivity.this, UserNotifications.class);
            Intent intent = new Intent(EventsHomeActivity.this, AdminActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}