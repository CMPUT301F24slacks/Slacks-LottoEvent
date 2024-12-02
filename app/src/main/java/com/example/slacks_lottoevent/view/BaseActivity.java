package com.example.slacks_lottoevent.view;

import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.slacks_lottoevent.R;
import com.google.android.material.appbar.MaterialToolbar;
/**
 * BaseActivity serves as a base class for activities that use a common layout
 * with a top app bar. This class provides functionality to initialize and manage
 * the app bar, including handling menu item clicks and inflating menu options.
 */
public class BaseActivity extends AppCompatActivity {
    /**
     * Called when the activity is starting.
     * Sets up the activity layout and initializes the top app bar with menu functionality.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied. Otherwise, it is null.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        MaterialToolbar topAppBar = findViewById(R.id.top_app_bar);
        setSupportActionBar(topAppBar);

        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_notifications) {
                // Handle notification icon click
                return true;
            }
            return false;
        });
    }

    /**
     * Inflate the menu items into the top app bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }
}
