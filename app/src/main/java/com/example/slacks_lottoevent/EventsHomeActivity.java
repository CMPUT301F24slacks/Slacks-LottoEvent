package com.example.slacks_lottoevent;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.slacks_lottoevent.databinding.ActivityEventsHomeBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;


/**
 * EventsHomeActivity is the main activity for the Events Home screen.
 */
public class EventsHomeActivity extends AppCompatActivity {

    private ActivityEventsHomeBinding binding;
    private AppBarConfiguration appBarConfiguration;
    private MaterialToolbar toolbar;
    private FacilityViewModel facilityViewModel;
    private Boolean hasFacility = null;

    private NotificationHelper notificationHelper;

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1;



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



        createNotificationChannel();
        notificationHelper = new NotificationHelper(this);
        checkAndRequestNotificationPermission();


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
            Intent intent = new Intent(EventsHomeActivity.this, UserNotifications.class);
//            Intent intent = new Intent(EventsHomeActivity.this, AdminActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Use the appropriate context method
            String channelId = getString(R.string.channel_id); // ensure channel ID is properly defined in strings.xml
            CharSequence name = getString(R.string.channel_name); // name of the channel
            String description = getString(R.string.channel_description); // description of the channel
            int importance = NotificationManager.IMPORTANCE_DEFAULT; // adjust as necessary

            // Create the NotificationChannel
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void grabbingNotifications(String deviceId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("notifications")
                .whereEqualTo("userId", deviceId)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Error listening for notifications", e);
                        return;
                    }

                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String title = document.getString("title");
                            String messageContent = document.getString("message");
                            notificationHelper.sendNotifications(deviceId, title, messageContent);
                        }
                    }
                    new Notifications().removeNotifications(deviceId);
                });
    }

    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
            else {
                startFetchingNotifications();
            }
        }
        else {
            startFetchingNotifications();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        SharedPreferences sharedPreferences = getSharedPreferences("SlacksLottoEventUserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startFetchingNotifications();
                editor.putBoolean("notificationsEnabled", true);

            } else {
                editor.putBoolean("notificationsEnabled", false);
                Log.d("EventsHomeActivity", "Notification permission denied.");

            }
        }
    }

    private void startFetchingNotifications() {
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        grabbingNotifications(deviceId);

        Notifications notification = new Notifications();
        notification.removeNotifications(deviceId);
    }



}