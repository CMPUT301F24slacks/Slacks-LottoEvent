package com.example.slacks_lottoevent;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.bumptech.glide.Glide;
import com.example.slacks_lottoevent.databinding.ActivityJoinEventDetailsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.provider.Settings;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * JoinEventDetailsActivity is the activity for the Join Event Details screen.
 */
public class JoinEventDetailsActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private ActivityJoinEventDetailsBinding binding;
    private DocumentSnapshot document;
    private String location;
    private String date;
    private String signupDeadline;
    private String eventName;
    private String time;
    private Boolean usesGeolocation;
    private String description;
    private String eventPosterURL;

    private Boolean entrantsChosen;
    FirebaseFirestore db;
    CollectionReference usersRef;
    String qrCodeValue;
    Integer spotsRemaining;
    String spotsRemainingText;

    SharedPreferences sharedPreferences;

    @SuppressLint("HardwareIds") String deviceId;
    @Override

    /**
     * onCreate method for the JoinEventDetailsActivity.
     * This method initializes the activity and sets up the event details.
     *
     * @param savedInstanceState The saved instance state
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJoinEventDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        qrCodeValue = getIntent().getStringExtra("qrCodeValue");
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        db = FirebaseFirestore.getInstance();
        db.collection("events").whereEqualTo("eventID", qrCodeValue).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {

                        document = task.getResult().getDocuments().get(0);
                        Boolean isDisabled = document.getBoolean("disabled");
                        if (isDisabled){
                            showInvalidQRCodeDialog();
                            binding.joinButton.setVisibility(View.GONE);
                        }
                        else {
                            date = document.getString("eventDate");
                            time = document.getString("time");
                            eventName = document.getString("name");
                            location = document.getString("location");
                            description = document.getString("description");
                            signupDeadline = document.getString("signupDeadline");
                            eventPosterURL = document.getString("eventPosterURL");
                            entrantsChosen = document.getBoolean("entrantsChosen");
                            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                            Date signup = null;
                            try {
                                signup = sdf.parse(signupDeadline);
                            } catch (ParseException e) {
                            }
                            Date currentDate = new Date();
                            try {
                                // Format the current date to "MM/dd/yyyy" and parse it back into a Date object to remove time
                                currentDate = sdf.parse(sdf.format(currentDate));
                            } catch (ParseException e) {
                            }


                            List<Object> waitlisted = (List<Object>) document.get("waitlisted");
                            Long capacity = (Long) document.get("eventSlots");
                            Long waitListCapacity = (Long) document.get("waitListCapacity");
                            assert capacity != null;
                            String capacityAsString = capacity.toString();

                        spotsRemaining = waitListCapacity.intValue() - waitlisted.size();

                        if (waitListCapacity == 0){
//                            Does not show badge if there is no waitlistCapacity section
                            binding.waitlistCapacitySection.setVisibility(View.GONE);
                            binding.spotsAvailableSection.setVisibility(View.GONE);
                        }

                        else if (waitListCapacity > 0){
//                            There is a waitlist capacity and shows the spots left
                            spotsRemaining = spotsRemaining > 0 ? spotsRemaining : 0;
                            spotsRemainingText = "Only " + spotsRemaining.toString() + " spot(s) available on waitlist";
                            binding.spotsAvailable.setText(spotsRemainingText);
                            if (spotsRemaining <= 0){
                                binding.waitlistFullBadge.setVisibility(View.VISIBLE);
                            }
                            else if (entrantsChosen) {
                                spotsRemainingText = "Only 0 spots available on waitlist";
                                binding.spotsAvailable.setText(spotsRemainingText);
                            }
                        }

                            if (eventPosterURL != null && !eventPosterURL.isEmpty()) {
                                Glide.with(this) // 'this' refers to the activity context
                                        .load(eventPosterURL)
                                        .into(binding.eventImage);
                            }


                        if (spotsRemaining <= 0 && waitListCapacity > 0 && !(currentDate.after(signup) )) {
                            // Capacity is full show we want to show the waitlist badge and no join
                            binding.joinButton.setVisibility(View.GONE);
                            binding.waitlistFullBadge.setVisibility(View.VISIBLE);
                        }

                        else if (spotsRemaining <= 0 && waitListCapacity > 0 && currentDate.after(signup)){
//                            Capacity is full and after sign up deadline
                            binding.joinButton.setVisibility(View.GONE);
                            binding.waitlistFullBadge.setVisibility(View.VISIBLE);
                            binding.signupPassed.setVisibility(View.VISIBLE);

                        }

                        else if (currentDate.after(signup) && spotsRemaining > 0 && waitListCapacity > 0 ){
//                            Sign up passed but waitlist was not full
                            binding.joinButton.setVisibility(View.GONE);
                            binding.signupPassed.setVisibility(View.VISIBLE);

                        }
                        else {
                            binding.joinButton.setVisibility(View.VISIBLE);
                            binding.waitlistFullBadge.setVisibility(View.GONE);
                        }

                            binding.eventTitle.setText(eventName);
                            binding.eventDate.setText("Event Date: " + date);
                            binding.signupDate.setText("Sign up deadline: " + signupDeadline);
                            binding.eventLocation.setText(location);
                            binding.eventSlots.setText("Event Slots: " + capacityAsString);
                            binding.eventDescription.setText(description);
                            binding.waitlistCapacity.setText("Waitlist Capacity: " + waitListCapacity.toString());

                            usesGeolocation = (Boolean) document.get("geoLocation");
                            // The reason to add the onClickListener in here is because we don't want the join button to do anything unless this event actually exists in the firebase
                            binding.joinButton.setOnClickListener(view -> {
                                FirestoreProfileUtil.checkIfSignedUp(deviceId, isSignedUp -> {
                                    if (isSignedUp) {
                                        if (usesGeolocation) {
                                            checkAndRequestGeolocation();
                                        } else {
                                            showRegistrationDialog();
                                        }
                                    } else {
                                        new AlertDialog.Builder(this)
                                                .setTitle("Sign-Up Required")
                                                .setMessage("In order to join an event, we need to collect some information about you.")
                                                .setPositiveButton("Proceed", (dialog, which) -> {
                                                    Intent signUpIntent = new Intent(JoinEventDetailsActivity.this, SignUpActivity.class);
                                                    startActivity(signUpIntent);
                                                })
                                                .setNegativeButton("Cancel", (dialog, which) -> {
                                                    dialog.dismiss();
                                                })
                                                .show();
                                    }
                                });

                            });
                        }
                    }
                    else {
                        showInvalidQRCodeDialog();
                    }
                });
        // add a listener to the event details back button, go to the last item in the back stack
        binding.eventDetailsBackButton.setOnClickListener(v -> {
            onBackPressed();
        });

    }
    private void showInvalidQRCodeDialog(){
        new AlertDialog.Builder(this) // Apply your custom theme here
                .setTitle("Event Unavailable")
                .setMessage("This event either doesn't exist or the QR code has been disabled by a Admin. Please check back later or scan a different Event QR Code!")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    navigateToEventsHome();

                })
                .show();
    }

    /**
     * showRegistrationDialog method for the JoinEventDetailsActivity.
     * This method shows the registration dialog for the event.
     */
    private void showRegistrationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_registration, null);
        builder.setView(dialogView);
        CheckBox declineCheckbox = dialogView.findViewById(R.id.declineCheckbox);
        Button confirmButton = dialogView.findViewById(R.id.confirm_button);
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);
        TextView eventDetailsTextView = dialogView.findViewById(R.id.eventDetails);
        TextView geolocationBadge = dialogView.findViewById(R.id.geolocationBadge);
        geolocationBadge.setVisibility(usesGeolocation ? View.VISIBLE : View.GONE);
        String eventDetailsText = "Date: " + date + "\nTime: " + time + "\nLocation: " + location;
        eventDetailsTextView.setText(eventDetailsText);
        AlertDialog dialog = builder.create();
        cancelButton.setOnClickListener(view -> dialog.dismiss());
        confirmButton.setOnClickListener(view -> {
            Boolean isDeclined = declineCheckbox.isChecked();
            // Get the user's unique device ID
            String userId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

            // Check if the entrant exists in Firestore
            db.collection("entrants").document(userId).get().addOnSuccessListener(task -> {
                if (task.exists()) {
                    // Entrant exists, check their events
                    Entrant entrant = task.toObject(Entrant.class);

                    // Check if the entrant is already associated with this event
                    if (entrant.getWaitlistedEvents().contains(qrCodeValue)
                            || entrant.getFinalistEvents().contains(qrCodeValue)
                            || entrant.getInvitedEvents().contains(qrCodeValue)
                            || entrant.getUninvitedEvents().contains(qrCodeValue)) {

                        // Show a toast message and dismiss the dialog
                        Toast.makeText(this, "You are already in the event", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        // Entrant is not in the event, add them to the event
                        handleEntrantActions(isDeclined, usesGeolocation, dialog);

                    }
                } else {
                    // Entrant does not exist, create a new one and add them
                    Log.d("JoinEventDetails", "Entrant does not exist. Creating a new entrant...");
                    createNewEntrant(userId);
                    handleEntrantActions(isDeclined, usesGeolocation, dialog);
                }
            }).addOnFailureListener(e -> {
                // Handle any errors in fetching the entrant document
                Log.e("JoinEventDetails", "Error fetching entrant document: " + e.getMessage());
                // Create a new entrant in case of a failure
                createNewEntrant(userId);
                handleEntrantActions(isDeclined, usesGeolocation, dialog);
            });
        });


        dialog.show();

    }



    /**
     *
     * Retrieves the current location of the device at the time this method is called.
     * Uses the Google Fused Location Provider API to get the current location if the user has enabled geolocation/given
     * the required location permission. Otherwise asks them to enable the geolocation permission. Once the location
     * has been fetched calls the
     *
     *
     * Relevant Documentation:
     * https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderClient
     * @param usesGeolocation a argument that indicates whether or not the event the user is joining uses geolocation.
     * */
    private void getJoinLocation(Boolean usesGeolocation){
        if (usesGeolocation){
            System.out.println("event does use join location line 263");
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                        .addOnSuccessListener(currentLocation ->{
                           if (currentLocation != null){
                               System.out.println("Getting location line 268");
                               double longitude = currentLocation.getLongitude();
                               double latitude = currentLocation.getLatitude();
                               System.out.println(latitude + longitude);
                               storeJoinLocation(latitude,longitude);
                           }
                        });

            }
            else {
                requestGeolocationPermission();
            }
        }
    }
    /**
     * Updates the Firestore database with the join location of a device/user for a specific event.
     * This function takes the latitude and longitude as parameters, creates a hashmap
     *  of the device ID to the provided location, and appends this hashmap to the
     * "joinLocations" array field of the event document.
     * @param latitude  The latitude of the device's location.
     * @param longitude The longitude of the device's location.
     * */
    private void storeJoinLocation(Double latitude,Double longitude){
        System.out.println("qrcode val"+ qrCodeValue);
        db.collection("events").whereEqualTo("eventID",qrCodeValue)
                .get()
                .addOnSuccessListener(task -> {
                    DocumentSnapshot eventDocumentSnapshot = task.getDocuments().get(0);
                    DocumentReference eventRef = eventDocumentSnapshot.getReference();
                    HashMap<String, List<Double>> joinLocation = new HashMap<>();
                    joinLocation.put(deviceId, Arrays.asList(latitude, longitude));
                    System.out.println("joinLocations " + joinLocation);
                    eventRef.update("joinLocations", FieldValue.arrayUnion(joinLocation))
                            .addOnSuccessListener(update ->{
                                System.out.println("Updated join locations");
                            });

                })
                .addOnFailureListener(task -> {
                    System.err.println("Error fetching event document in storeJoinLocation: " + task);
                });
    }






    /**
     * createNewEntrant method for the JoinEventDetailsActivity.
     * This method creates a new entrant in the Firestore database.
     *
     * @param userId The unique user ID
     */

    private void createNewEntrant(String userId) {
        Entrant newEntrant = new Entrant();
        newEntrant.addWaitlistedEvents(qrCodeValue); // Add the event to the waitlist
        db.collection("entrants").document(userId)
                .set(newEntrant)
                .addOnSuccessListener(aVoid -> Log.d("JoinEventDetails", "New entrant created successfully"))
                .addOnFailureListener(e -> Log.e("JoinEventDetails", "Error creating new entrant: " + e.getMessage()));
    }

    /**
     * navigateToEventsHome method for the JoinEventDetailsActivity.
     * This method navigates to the Events Home screen.
     */
    private void navigateToEventsHome() {
        Intent intent = new Intent(JoinEventDetailsActivity.this, EventsHomeActivity.class);
        startActivity(intent);
    }

    /**
     * addEntrantToWaitlist method for the JoinEventDetailsActivity.
     * This method adds the entrant to the waitlist for the event.
     */
    private void addEntrantToWaitlist(Boolean isReselected){
        db.collection("events").whereEqualTo("eventID",qrCodeValue)
                .get()
                .addOnSuccessListener(task -> {
                    DocumentSnapshot eventDocumentSnapshot = task.getDocuments().get(0);
                    DocumentReference eventRef = eventDocumentSnapshot.getReference();
                    eventRef.update("waitlisted", FieldValue.arrayUnion(deviceId),
                            "waitlistedNotificationsList", FieldValue.arrayUnion(deviceId));
                    if(isReselected){
                        eventRef.update("reselected", FieldValue.arrayUnion(deviceId));
                    }


                })
                .addOnFailureListener(task -> {
                    System.err.println("Error fetching event document: " + task);
                });
    }

    /**
     * addEventToEntrant method for the JoinEventDetailsActivity and updates the notification preferences for said entrant.
     * This method adds the event to the entrant.
     */
    private void addEventToEntrant(){
        DocumentReference entrantDocRef = db.collection("entrants").document(deviceId);
        entrantDocRef.get().addOnSuccessListener(task -> {
            if (task.exists()){
                Entrant entrant = task.toObject(Entrant.class);
                entrant.addWaitlistedEvents(qrCodeValue);
                entrantDocRef.set(entrant);
            }
            else {
                // Entrant not already in the database
                Entrant newEntrant = new Entrant();
                newEntrant.getWaitlistedEvents().add(qrCodeValue);
                entrantDocRef.set(newEntrant);
            }
        });
    }


    /**
     * Function that checks if the users have enabled location permissions for the app and depending on if they do
     * redirects to the registration dialog and if they don't redirects them to the enable geolocation dialog. .
     *
     * */
    private void checkAndRequestGeolocation(){
        if (hasGeolocationEnabled()){
            showRegistrationDialog();
        }
        else {
            showEnableGeolocationAlertDialog();
        }
    }
    /**
     * Creates a Dialog box that explains to the user why they need geolocation for this event and gives them
     * a button to enable it.
     * */
    private void showEnableGeolocationAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_enable_geolocation, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        Button cancelBtn = dialogView.findViewById(R.id.cancel_button);
        Button confirmBtn = dialogView.findViewById(R.id.confirm_button);

        cancelBtn.setOnClickListener(view -> dialog.dismiss());
        confirmBtn.setOnClickListener(view ->{
            requestGeolocationPermission();
            dialog.dismiss();
        });

        dialog.show();
    }
    /**
     * Fynction that returns a boolean value on whether or not the user has enabled geolocation permissions.
     * */
    private boolean hasGeolocationEnabled(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    /**
     *  This function checks whether the geolocation permission (ACCESS_FINE_LOCATION) has been granted.
     *  If the permission has not been granted it uses the shouldShowRequestPermissionRationale before requesting the geolocation
     *  permission to determine if we need to provide rationale and redirect the user to the device settings. If the user has denied the permission multiply times
     *  android disables the permission pop up so this method creates a dialog box that redirects the user to the apps settings page where they can
     *  manually enable the location permission.
     * */
    private void requestGeolocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // The shouldShowRequestPermissionRationale is used to determine whether or not the app needs to add additional reationale
                // Before requesting the users permissions again. If the user clicks dont allow too many times the pop up asking for permission won't appear.
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE
                );
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Permission Required")
                        .setMessage("Geolocation is required to join this event. Please enable it in the app settings.")
                        .setPositiveButton("Open Settings", (dialog, which) -> {

                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            // https://stackoverflow.com/questions/19517417/opening-android-settings-programmatically
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            // Setting the URI to be for our apps setting page.
                            intent.setData(uri);
                            startActivity(intent); // launching the settings for the app. Here users will have to manually add permissions if they denied too many times
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        }

    }

    private void handleEntrantActions(boolean isDeclined, boolean usesGeolocation, DialogInterface dialog) {
        addEntrantToWaitlist(isDeclined);
        addEventToEntrant();
        getJoinLocation(usesGeolocation);
        navigateToEventsHome();
        dialog.dismiss();
    }
}