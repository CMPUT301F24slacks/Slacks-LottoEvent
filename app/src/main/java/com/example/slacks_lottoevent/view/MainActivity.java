package com.example.slacks_lottoevent.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.slacks_lottoevent.R;
import com.example.slacks_lottoevent.model.User;
import com.example.slacks_lottoevent.viewmodel.EntrantViewModel;
import com.example.slacks_lottoevent.viewmodel.EventViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends BaseActivity {
    private EntrantViewModel entrantViewModel;
    private EventViewModel eventViewModel;
    private User user;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate activity_main layout into content_frame of activity_base
        getLayoutInflater().inflate(R.layout.activity_main, findViewById(R.id.content_frame), true);

        user = User.getInstance(this);
        deviceId = user.getDeviceId();

        // Initialize ViewModels
        entrantViewModel = new ViewModelProvider(this).get(EntrantViewModel.class);
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        // Observe the current entrant
        entrantViewModel.observeEntrant(deviceId);

        entrantViewModel.getCurrentEntrant().observe(this, currentEntrant -> {
            if (currentEntrant != null) {
                eventViewModel.setWaitlistedEvents(currentEntrant.getWaitlistedEvents());
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

}
