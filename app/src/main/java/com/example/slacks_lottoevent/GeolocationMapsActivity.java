package com.example.slacks_lottoevent;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
/**
 *
 *
 *
 * Relevant Documentation
 * https://developers.google.com/android/reference/com/google/android/gms/maps/MapFragment
 * https://developers.google.com/android/reference/com/google/android/gms/maps/OnMapReadyCallback
 * https://developers.google.com/maps/documentation/android-sdk/map (Setting Up the Map in onCreate)
 * */
public class GeolocationMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private TabLayout tabLayout;
    private LatLng eventLocation;
    private String eventID;
    private FirebaseFirestore db;
    private ArrayList<Map<String, List<Double>>> joinLocations;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_geolocation_map);
        tabLayout = findViewById(R.id.distance_tab_layout);
        setupTabs();
        Intent intent = getIntent();
        eventID = intent.getStringExtra("eventID");
        db = FirebaseFirestore.getInstance();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    private void setupTabs(){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        // less than 5km radius case
                        break;
                    case 1:
                        // 5-10km radius case
                        break;
                    case 2:
                        // 10-20km radius case.
                        break;
                    case 3:
                        // greater than 20km radius case



                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
    /**
     *
     *
     * Relevant Documentation:
     * https://developers.google.com/maps/documentation/android-sdk/reference/com/google/android/libraries/maps/model/Circle
     * https://developers.google.com/android/reference/com/google/android/gms/maps/model/LatLng
     *
     * */
    private void updateMapRadius(double radiusInKm){

        googleMap.clear(); // Clearing the map from stuff that is currently place on it.

        googleMap.addCircle(new CircleOptions()
                .center(eventLocation)
                .radius(radiusInKm * 1000)
                .strokeColor()


        );

    }
    private void fetchAddressAndWaitlistLocations(){
        db.collection("events").whereEqualTo("eventID",eventID)
                .get()
                .addOnCompleteListener(task -> {
                   if (task.isSuccessful() && !task.getResult().isEmpty()){
                       DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        joinLocations = (ArrayList<Map<String, List<Double>>>) documentSnapshot.get("joinLocations");
                        setEventLocationInLatLng(String.valueOf(documentSnapshot.get("location")));
                   }
                });

    }
    /**
     * Relevant Documentation
     * https://developer.android.com/reference/android/location/Geocoder
     * https://developer.android.com/reference/android/location/Geocoder#getFromLocationName(java.lang.String,%20int)
     * */
    private void setEventLocationInLatLng(String eventAddress){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(eventAddress,1);
            // Deprecated method only way to use newer one is to increase minSdk in build.gradle
            if(addressList != null && !addressList.isEmpty()){
                Address address = addressList.get(0);
                eventLocation = new LatLng(address.getLatitude(),address.getLongitude());
            }

        } catch (IOException e) {
            System.out.println("An error occured trying to convert the event location to LatLng");
        }


    }
    /**
     *
     *
     * Relevant Documentation
     * (changing the camera position)
     * https://developers.google.com/android/reference/com/google/android/gms/maps/CameraUpdateFactory#newLatLngZoom(com.google.android.gms.maps.model.LatLng,%20float)

     * */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation,12)); // Default of 10km zoom level.
        updateMapRadius(10);
    }

}
