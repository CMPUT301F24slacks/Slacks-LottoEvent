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
import androidx.navigation.ui.NavigationUI;

import com.example.slacks_lottoevent.databinding.ActivityEventsHomeBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EventsHomeActivity extends AppCompatActivity implements AddFacilityFragment.AddFacilityDialogListener {

    private ActivityEventsHomeBinding binding;
    private AppBarConfiguration appBarConfiguration;
    private MaterialToolbar toolbar;
    private Button createFacilitiesButton;
    private TabLayout eventTabLayout;
    private TextView facilityCreated;
    private Facility existingFacility;
    private FirebaseFirestore db;
    private CollectionReference facilitiesRef;

    @Override
    public void addFacility(Facility facility) {
        // Retrieve the facility name and set it to display on the screen
        String facilityName = facility.getFacilityName();
        facilityCreated.setText(facilityName);
        existingFacility = facility;

        // Create a map or directly use facility if it is serializable for Firestore
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("name", facilityName);
        // Add other attributes as needed, like location, type, etc.
        facilityData.put("streetAddress1", facility.getStreetAddress1());
        facilityData.put("streetAddress2", facility.getStreetAddress2());
        facilityData.put("city", facility.getCity());
        facilityData.put("province", facility.getProvince());
        facilityData.put("country", facility.getCountry());
        facilityData.put("postalCode", facility.getProvince());

        // Add the facility data to Firestore
        facilitiesRef.add(facilityData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("addFacility", "Facility added with ID: " + documentReference.getId());
                    existingFacility.setDocumentId(documentReference.getId()); // Set document ID here
                })
                .addOnFailureListener(e -> Log.w("addFacility", "Error adding facility", e));

    }

    @Override
    public void updateFacility() {
        String facilityName = existingFacility.getFacilityName();
        facilityCreated.setText(facilityName);

        if (existingFacility == null) {
            Log.w("updateFacility", "No facility selected to update");
            return;
        }

        // Retrieve the document ID for the existing facility
        String documentId = existingFacility.getDocumentId();
        if (documentId == null || documentId.isEmpty()) {
            Log.w("updateFacility", "No document ID provided for update");
            return;
        }

        // Prepare the updated facility data
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("name", existingFacility.getFacilityName());
        facilityData.put("streetAddress1", existingFacility.getStreetAddress1());
        facilityData.put("streetAddress2", existingFacility.getStreetAddress2());
        facilityData.put("city", existingFacility.getCity());
        facilityData.put("province", existingFacility.getProvince());
        facilityData.put("country", existingFacility.getCountry());
        facilityData.put("postalCode", existingFacility.getPostalCode());

        // Update the document in Firestore
        facilitiesRef.document(documentId).update(facilityData)
                .addOnSuccessListener(aVoid -> Log.d("updateFacility", "Facility updated successfully"))
                .addOnFailureListener(e -> Log.w("updateFacility", "Error updating facility", e));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        facilitiesRef = db.collection("facilities");
        binding = ActivityEventsHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        createFacilitiesButton = findViewById(R.id.create_facility_button);
        // initially hide the button since it loads my events page first
        createFacilitiesButton.setVisibility(View.GONE);
        facilityCreated = findViewById(R.id.facility_created);

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
                    binding.qrCodeScannerFAB.setVisibility(View.VISIBLE);
                    // hide the create event button
                    binding.createEventFAB.setVisibility(View.GONE);
                    // hide the create facility button
                    createFacilitiesButton.setVisibility(View.GONE);
                }
                if (tab.getText().equals("Manage My Events")) {
                    navController.navigate(R.id.ManageMyEventsFragment);
                    // hide the QR code scanner button
                    binding.qrCodeScannerFAB.setVisibility(View.GONE);
                    // show the create event button
                    binding.createEventFAB.setVisibility(View.VISIBLE);
                    // Show the create facility button

                    createFacilitiesButton.setVisibility(View.VISIBLE);
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

        /*
         * TODO: Create event button. Organizers clicks this buttons to create an event.
         */
        binding.createEventFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create event button
                Toast.makeText(EventsHomeActivity.this, "Create Event Button Clicked", Toast.LENGTH_SHORT).show();
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

        createFacilitiesButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                new AddFacilityFragment().show(getSupportFragmentManager(), "Add Facility");
            }
        });

        facilityCreated.setOnClickListener(v -> {
            new AddFacilityFragment(existingFacility, true).show(getSupportFragmentManager(), "Edit Facility");
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
