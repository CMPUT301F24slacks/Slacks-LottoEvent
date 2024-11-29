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
import com.example.slacks_lottoevent.viewmodel.ProfileViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.android.libraries.places.api.Places;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
public class MainActivity extends BaseActivity {
    private EntrantViewModel entrantViewModel;
    private EventViewModel eventViewModel;
    private ProfileViewModel profileViewModel;
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

        user = User.getInstance();
        deviceId = user.getDeviceId();

        // Initialize ViewModels
        entrantViewModel = new ViewModelProvider(this).get(EntrantViewModel.class);
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

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