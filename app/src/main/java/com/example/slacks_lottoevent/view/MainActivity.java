package com.example.slacks_lottoevent.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.slacks_lottoevent.FacilityViewModel;
import com.example.slacks_lottoevent.model.Profile;
import com.example.slacks_lottoevent.R;
import com.example.slacks_lottoevent.model.User;
import com.example.slacks_lottoevent.viewmodel.EntrantViewModel;
import com.example.slacks_lottoevent.viewmodel.EventViewModel;
import com.example.slacks_lottoevent.viewmodel.OrganizerViewModel;
import com.example.slacks_lottoevent.viewmodel.ProfileViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MainActivity extends BaseActivity {
    private EntrantViewModel entrantViewModel;
    private EventViewModel eventViewModel;
    private ProfileViewModel profileViewModel;
    private FacilityViewModel facilityViewModel;
    private OrganizerViewModel organizerViewModel;
    private User user;
    private String deviceId;
    private FirebaseFirestore db;
    private CollectionReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();

        usersRef = db.collection("profiles");

        super.onCreate(savedInstanceState);

        // Inflate activity_main layout into content_frame of activity_base
        getLayoutInflater().inflate(R.layout.activity_main, findViewById(R.id.content_frame), true);

        User.initialize(this);
        user = User.getInstance();
        deviceId = user.getDeviceId();

        // Initialize ViewModels
        entrantViewModel = new ViewModelProvider(this).get(EntrantViewModel.class);
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        facilityViewModel = new ViewModelProvider(this).get(FacilityViewModel.class);
        organizerViewModel = new ViewModelProvider(this).get(OrganizerViewModel.class);

        // Observe the events the entrant is involved in
        entrantViewModel.getCurrentEntrantLiveData().observe(this, entrant -> {
            if (entrant != null) {
                List<String> waitlistedIds = entrant.getWaitlistedEvents();
                List<String> unselectedIds = entrant.getUninvitedEvents();
                List<String> invitedIds = entrant.getInvitedEvents();
                List<String> attendingIds = entrant.getFinalistEvents();
                eventViewModel.updateEventLists(waitlistedIds, unselectedIds, invitedIds,
                                                attendingIds);
            } else {
                // Clear all event lists
                eventViewModel.updateEventLists(null, null, null, null);
            }
        });

        // Observe the events the organizer is involved in
        organizerViewModel.getCurrentOrganizerLiveData().observe(this, organizer -> {
            if (organizer != null) {
                List<String> eventIds = organizer.getEvents();
                eventViewModel.updateOrganizerEvents(eventIds);
            } else {
                // Clear all event lists
                eventViewModel.updateOrganizerEvents(null);
            }
        });

        // Set up the Toolbar
        setSupportActionBar(findViewById(R.id.top_app_bar));

        // Set up the BottomNavigationView and FloatingActionButton
        FloatingActionButton fab = findViewById(R.id.fab);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set up the NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_main);
        NavController navController = navHostFragment.getNavController();

        // Set up NavigationUI for the BottomNavigationView
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Set a click listener for BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                navController.navigate(R.id.homeFragment);
                return true;
            } else if (itemId == R.id.nav_manage) {
                navController.navigate(R.id.manageFragment);
                return true;
            } else if (itemId == R.id.nav_inbox) {
                navController.navigate(R.id.invitesFragment);
                return true;
            } else if (itemId == R.id.nav_profile) {
                navController.navigate(R.id.profileFragment);
                return true;
            }
            return false;
        });

        // Set a click listener for the FAB
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, QRScannerActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Iterate through all menu items and make them visible
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            // if the item is not the admin item, make it visible
            if (item.getItemId() != R.id.action_admin)
                item.setVisible(true); // Set visibility to tru
            profileViewModel.getCurrentProfileLiveData().observe(this, profile -> {
                if (profile != null) {
                    if (profile.getAdmin()) {
                        item.setVisible(true);
                    }
                    if (item.getItemId() != R.id.action_admin) {
                        if (profile.getAdminNotifications()) {
                            item.setIcon(R.drawable.baseline_notifications_active_24);
                        } else {
                            item.setIcon(R.drawable.baseline_notifications_off_24);
                        }
                    }
                }
            });
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("MainActivity", "Menu item selected: " + item.getItemId());
        int itemId = item.getItemId();
        if (itemId == R.id.action_notifications) {
            Profile profile = profileViewModel.getCurrentProfileLiveData().getValue();
            if (profile != null) {
                profile.setAdminNotifications(!profile.getAdminNotifications());
                profileViewModel.updateProfile(profile);
                if (profile.getAdminNotifications()) {
                    item.setIcon(R.drawable.baseline_notifications_active_24);
                } else {
                    item.setIcon(R.drawable.baseline_notifications_off_24);
                }
            }
            return true;
        } else if (itemId == R.id.action_admin) {
            // Handle Admin click
            Log.d("MainActivity", "Admin clicked");
            Intent intent = new Intent(this, AdminActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = ((NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_main))
                .getNavController();
        return NavigationUI.navigateUp(navController, (AppBarConfiguration) null) ||
               super.onSupportNavigateUp();
    }
}