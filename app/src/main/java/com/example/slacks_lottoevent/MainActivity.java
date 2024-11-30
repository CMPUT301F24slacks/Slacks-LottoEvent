package com.example.slacks_lottoevent;

import android.content.Intent;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
//testing purposes
import androidx.appcompat.app.AppCompatActivity;

import com.example.slacks_lottoevent.model.User;
import com.example.slacks_lottoevent.view.BaseActivity;
import com.example.slacks_lottoevent.viewmodel.EntrantViewModel;
import com.example.slacks_lottoevent.viewmodel.EventViewModel;
import com.example.slacks_lottoevent.viewmodel.OrganizerViewModel;
import com.example.slacks_lottoevent.viewmodel.ProfileViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.android.libraries.places.api.Places;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;
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
                eventViewModel.updateEventLists(waitlistedIds, unselectedIds, invitedIds, attendingIds);
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


        FloatingActionButton fab = findViewById(R.id.fab);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

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
            } else if (itemId == R.id.nav_invites) {
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
            item.setVisible(true); // Set visibility to true
        }
        return super.onPrepareOptionsMenu(menu);
    }

}