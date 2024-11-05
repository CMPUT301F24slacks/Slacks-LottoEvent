package com.example.slacks_lottoevent;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.slacks_lottoevent.databinding.ActivityEventsHomeBinding;
import com.example.slacks_lottoevent.databinding.NotificationsUserBinding;
import com.google.android.material.appbar.MaterialToolbar;


public class UserNotifications extends AppCompatActivity {
    private NotificationsUserBinding binding1;
    private ActivityEventsHomeBinding binding2;
    private AppBarConfiguration appBarConfiguration;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set Up ArrayAdapter, do getUsername() for every user in said category, change it in the case-by-case tabLayout system
        Intent intent = getIntent();

        binding1 = NotificationsUserBinding.inflate(getLayoutInflater());
        setContentView(binding1.getRoot());

//        take from the entrant.invited array, then for loop through that and make the object of accepting/declining
//        with fields using getters on that specific id of that event already in the firestore, then
//        make if its accepted, we add that to our finalist events in our entrant class, and if declined
//        we remove ourselves from the entrant.invited array whilst removing ourselves from the events.invited
//        array, and then updating the count (if there is one) of how many people are invited.

//        for the organizer lists of cancelled joined and etc, take it from the event associated when clicked using intent
//        then replace that dummy data with the real one, also fix leylas implementation


//        binding2 = ActivityEventsHomeBinding.inflate(getLayoutInflater());
//        setContentView(binding2.getRoot());
//
//        toolbar = binding2.toolbar;
//        setSupportActionBar(toolbar);

//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_events_home); // Get the navigation controller
//        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build(); // Build the app bar configuration

        /*
         * Handle app bar title clicks here.
         */
//        setSupportActionBar(toolbar);
//        toolbar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
////                Toast.makeText(UserNotifications.this, "Toolbar title clicked", Toast.LENGTH_SHORT).show();
//            }
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
//        if (id == R.id.notifications) {
//            Intent intent = new Intent(EventsHomeActivity.this, UserNotifications.class);
//            startActivity(intent);
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }
    }



