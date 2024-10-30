package com.example.slacks_lottoevent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.slacks_lottoevent.databinding.ActivityEventsHomeBinding;
import com.example.slacks_lottoevent.databinding.FragmentManageMyEventsBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EventsHomeActivity extends AppCompatActivity {

/**
 * EventsHomeActivity is the main activity for the Events Home screen.
 */

    private ActivityEventsHomeBinding bindingEventsHome;
    private FragmentManageMyEventsBinding bindingManageMyEvents;
    private AppBarConfiguration appBarConfiguration;
    private MaterialToolbar toolbar;

    private TabLayout eventTabLayout;

    private FirebaseFirestore db;
    private CollectionReference facilitiesRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        bindingEventsHome = ActivityEventsHomeBinding.inflate(getLayoutInflater());
        bindingManageMyEvents = FragmentManageMyEventsBinding.inflate(getLayoutInflater());
        setContentView(bindingEventsHome.getRoot());
        setContentView(bindingManageMyEvents.getRoot());

        toolbar = bindingEventsHome.toolbar;
        setSupportActionBar(toolbar);



        // initially hide the button since it loads my events page first
//        createFacilitiesButton.setVisibility(View.GONE);
        //facilityCreated = findViewById(R.id.facility_created);

        // initially hide the textview since it loads my events page first
        // facilityCreated.setVisibility(View.GONE);

        eventTabLayout = findViewById(R.id.events_home_tab_layout);
        TabLayout eventsTabs = findViewById(R.id.events_home_tab_layout); // Get the tab layout in EventsHomeActivity

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_events_home); // Get the navigation controller
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build(); // Build the app bar configuration

        eventsTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("My Events")) {
                    navController.navigate(R.id.MyEventsFragment);
                    // show the QR code scanner button
                    bindingEventsHome.qrCodeScannerFAB.setVisibility(View.VISIBLE);
                    // hide the create event button
                    bindingEventsHome.createEventFAB.setVisibility(View.GONE);
                    // hide the create facility button
                    //createFacilitiesButton.setVisibility(View.GONE);
                    // hides the facility textview
                    //facilityCreated.setVisibility(View.GONE);
                }
                if (tab.getText().equals("Manage My Events")) {
                    navController.navigate(R.id.ManageMyEventsFragment);
                    // hide the QR code scanner button
                    bindingEventsHome.qrCodeScannerFAB.setVisibility(View.GONE);
                    // show the create event button
                    bindingEventsHome.createEventFAB.setVisibility(View.VISIBLE);
                    // Shows the facility textview
                    //facilityCreated.setVisibility(View.VISIBLE);

//                    facilitiesRef.addSnapshotListener((querySnapshot, e) -> {
//                        if (e != null) {
//                            Log.w("Firestore", "Listen failed.", e);
//                            return;
//                        }
//
//                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
//                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
//                            String facilityName = document.getString("name");
//                            if (facilityName != null && !facilityName.isEmpty()) {
//                                facilityCreated.setText(facilityName);
//                                facilityCreated.setVisibility(View.VISIBLE);
//                            } else {
//                                facilityCreated.setVisibility(View.GONE);
//                            }
//                        } else {
//                            facilityCreated.setVisibility(View.GONE);
//                            createFacilitiesButton.setVisibility(View.VISIBLE);
//                        }
//                    });
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
        bindingEventsHome.qrCodeScannerFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventsHomeActivity.this,EventQrScanner.class);
                startActivity(intent);
            }
        });

        /*
         * Create event button, opens the create event screen upon being clicked.
         */
        bindingEventsHome.createEventFAB.setOnClickListener(new View.OnClickListener() {
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

//        createFacilitiesButton.setOnClickListener(new Button.OnClickListener(){
//            public void onClick(View v){
//                new AddFacilityFragment().show(getSupportFragmentManager(), "Add Facility");
//            }
//        });
//
//        facilityCreated.setOnClickListener(v -> {
//            new AddFacilityFragment(existingFacility, true).show(getSupportFragmentManager(), "Edit Facility");
//        });


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

