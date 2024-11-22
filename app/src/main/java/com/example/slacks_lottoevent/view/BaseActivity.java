package com.example.slacks_lottoevent.view;

import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.slacks_lottoevent.R;
import com.google.android.material.appbar.MaterialToolbar;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base); // Load the base layout with the top app bar

        // Set up the top app bar
        MaterialToolbar topAppBar = findViewById(R.id.top_app_bar);
        setSupportActionBar(topAppBar);

        // Optional: Handle menu item clicks if needed
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
