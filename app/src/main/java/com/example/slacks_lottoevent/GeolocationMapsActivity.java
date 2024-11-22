package com.example.slacks_lottoevent;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
    public interface OnDataFetchCompleteListener {
        void onDataFetchComplete();
    }

    private GoogleMap googleMap;
    private TabLayout tabLayout;
    private LatLng eventLocation;
    private String eventID;
    private FirebaseFirestore db;
    private ArrayList<Map<String, List<Double>>> joinLocations;

    /**
     *
     *
     *
     * */
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
        fetchAddressAndWaitlistLocations(() ->{
            mapFragment.getMapAsync(googleMap ->{
                this.googleMap = googleMap;
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation,15));
                updateMapRadius(5);
            });

        });

    }

    private void setupTabs(){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        updateMapRadius(5);
                        break;
                    case 1:
                        updateMapRadius(10);
                        break;
                    case 2:
                        updateMapRadius(20);
                        break;
                    case 3:
                       updateMapRadius(50);



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
                .strokeColor(0xFF7B24EB)
                .strokeWidth(2)
        );
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        boundsBuilder.include(eventLocation);
        for (Map<String, List<Double>> location : joinLocations) {
            for (Map.Entry<String, List<Double>> entry : location.entrySet()) {
                List<Double> coords = entry.getValue();
                LatLng position = new LatLng(coords.get(0), coords.get(1));

                double distance = calculateDistanceBetweenTwoLatLng(eventLocation, position);
                if (distance <= radiusInKm) {
                    googleMap.addMarker(new MarkerOptions().position(position)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                    );
                    boundsBuilder.include(position);
                }
            }
        }
        googleMap.addMarker(new MarkerOptions()
                .position(eventLocation)
                .title("Event Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        googleMap.setOnMarkerClickListener(marker ->{
            LatLng position = marker.getPosition();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position,15));
            return true;
        });
        LatLngBounds bounds = boundsBuilder.build();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));


    }
    private void fetchAddressAndWaitlistLocations(OnDataFetchCompleteListener listener){
        db.collection("events").whereEqualTo("eventID",eventID)
                .get()
                .addOnCompleteListener(task -> {
                   if (task.isSuccessful() && !task.getResult().isEmpty()){
                       DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        joinLocations = (ArrayList<Map<String, List<Double>>>) documentSnapshot.get("joinLocations");
                        setEventLocationInLatLng(String.valueOf(documentSnapshot.get("location")));
                        listener.onDataFetchComplete();
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

        updateMapRadius(10);
    }
    /**
     *
     * Relevant Documentation
     * https://stackoverflow.com/questions/6981916/how-to-calculate-distance-between-two-locations-using-their-longitude-and-latitu
     * https://developer.android.com/reference/android/location/Location.html#distanceBetween(double,%20double,%20double,%20double,%20float[])
     * */
    private double calculateDistanceBetweenTwoLatLng(LatLng latLng1,LatLng latLng2){
        float[] results = new float[1];
        Location.distanceBetween(latLng1.latitude,latLng1.longitude,latLng2.latitude,latLng2.longitude,results);
        return (double) results[0] / 1000.0; // Convert to km since we need to compare with radius but cast to double for extra precision
    }

}
