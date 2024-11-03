package com.example.slacks_lottoevent;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.slacks_lottoevent.databinding.ActivityEventsHomeBinding;
import com.example.slacks_lottoevent.databinding.NotificationsUserBinding;
import com.google.android.material.appbar.MaterialToolbar;


public class User_Notifications extends AppCompatActivity {
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
////                Toast.makeText(User_Notifications.this, "Toolbar title clicked", Toast.LENGTH_SHORT).show();
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
//            Intent intent = new Intent(EventsHomeActivity.this, User_Notifications.class);
//            startActivity(intent);
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }
    }



