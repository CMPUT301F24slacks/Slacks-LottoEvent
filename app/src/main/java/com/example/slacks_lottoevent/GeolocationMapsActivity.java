package com.example.slacks_lottoevent;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.tabs.TabLayout;

public class GeolocationMapsActivity extends AppCompatActivity {

    private GoogleMap googleMap;
    private TabLayout tabLayout;
    private String eventAddress;
    private String eventLatitude;
    private String eventLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_geolocation_map);



    }
}
