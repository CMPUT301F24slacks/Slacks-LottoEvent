package com.example.slacks_lottoevent;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.example.slacks_lottoevent.databinding.ActivityEventDetailsBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.provider.Settings;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventDetails extends AppCompatActivity {
    private ActivityEventDetailsBinding binding;
    private DocumentSnapshot document;
    private String location;
    private String date;
    private String time;
    private Boolean usesGeolocation;
    FirebaseFirestore db;
    String qrCodeValue;
    String deviceId;
    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        qrCodeValue = getIntent().getStringExtra("qrCodeValue");
//        Log.d("EventDetails", "QR Code Value: " + qrCodeValue);
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        db = FirebaseFirestore.getInstance();

        db.collection("events").whereEqualTo("eventID", qrCodeValue).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        Event event = document.toObject(Event.class); // Converts the document to an Event object
                        binding.eventTitle.setText(event.getName());
                        date = String.valueOf(event.getDate());
                        binding.eventDate.setText(event.getDate());
                        binding.eventDescription.setText(event.getDescription());

                        binding.eventWaitlistCapacity.setText("Waitlist Capacity " + event.getWaitListCapacity());
                        binding.eventTime.setText(event.getTime());
                        time = String.valueOf(event.getTime());

                        Long spotsRemaining = (long) (event.getEventSlots() - event.getFinalists().size());
                        binding.spotsAvailable.setText("Only " + spotsRemaining + " spots available");
                        usesGeolocation = event.getgeoLocation();

                        if (event.getEventSlots() == event.getFinalists().size()) {
                            // Capacity is full; show the waitlist badge
                            binding.joinButton.setVisibility(View.GONE);
                            binding.waitlistFullBadge.setVisibility(View.VISIBLE);
                        } else {
                            binding.joinButton.setVisibility(View.VISIBLE);
                            binding.waitlistFullBadge.setVisibility(View.GONE);
                        }

                        db.collection("organizers").document(deviceId) // Replace with the actual organizer ID field name in Event class
                                .get()
                                .addOnSuccessListener(organizerDoc -> {
                                    if (organizerDoc.exists()) {
                                        String facilityId = organizerDoc.getString("facilityId");

                                        // Now fetch the facility details using the facilityId
                                        db.collection("facilities").document(facilityId)
                                                .get()
                                                .addOnSuccessListener(facilityDoc -> {
                                                    if (facilityDoc.exists()) {
                                                        location = facilityDoc.getString("address");
                                                        binding.eventLocation.setText(location);
                                                    } else {
                                                        binding.eventLocation.setText("Facility not found");
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    binding.eventLocation.setText("Error loading facility");
                                                });
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    binding.eventLocation.setText("Error loading organizer");
                                });

                        binding.joinButton.setOnClickListener(view -> {
                            SharedPreferences sharedPreferences = getSharedPreferences("SlacksLottoEventUserInfo", MODE_PRIVATE);
                            boolean isSignedUp = sharedPreferences.getBoolean("isSignedUp", false);
                            if (isSignedUp) {
                                showRegistrationDialog();
                            } else {
                                new AlertDialog.Builder(this)
                                        .setTitle("Sign-Up Required")
                                        .setMessage("In order to join an event, we need to collect some information about you.")
                                        .setPositiveButton("Proceed", (dialog, which) -> {
                                            Intent signUpIntent = new Intent(EventDetails.this, SignUpActivity.class);
                                            startActivity(signUpIntent);
                                        })
                                        .setNegativeButton("Cancel", (dialog, which) -> {
                                            dialog.dismiss();
                                        })
                                        .show();
                            }
                        });

                    }
                });


        // add a listener to the event details back button, go to the last item in the back stack
        binding.eventDetailsBackButton.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void showRegistrationDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_registration, null);
        builder.setView(dialogView);

        ImageView bellChosen = dialogView.findViewById(R.id.bellChosen);
        ImageView bellNotChosen = dialogView.findViewById(R.id.bellNotChosen);
        CheckBox declineCheckbox = dialogView.findViewById(R.id.declineCheckbox);
        Button confirmButton = dialogView.findViewById(R.id.confirm_button);
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);
        TextView eventDetailsTextView = dialogView.findViewById(R.id.eventDetails);
        TextView geolocationBadge = dialogView.findViewById(R.id.geolocationBadge);


        geolocationBadge.setVisibility(usesGeolocation ? View.VISIBLE : View.GONE);

        String eventDetailsText = "Date: " + date + "\nTime: " + time + "\nLocation: " + location;
        eventDetailsTextView.setText(eventDetailsText);

        AlertDialog dialog = builder.create();

        AtomicBoolean chosenForLottery = new AtomicBoolean(false);
        AtomicBoolean notChosenForLottery = new AtomicBoolean(false);


        bellChosen.setOnClickListener(v -> {
            boolean negation = !chosenForLottery.get();
            chosenForLottery.set(negation);
            bellChosen.setImageResource(chosenForLottery.get() ? R.drawable.baseline_notifications_active_24 : R.drawable.baseline_circle_notifications_24);
        });

        bellNotChosen.setOnClickListener(v -> {
            boolean negation = !notChosenForLottery.get();
            notChosenForLottery.set(negation);
            bellNotChosen.setImageResource(notChosenForLottery.get() ? R.drawable.baseline_notifications_active_24 : R.drawable.baseline_circle_notifications_24);
        });


        cancelButton.setOnClickListener(view -> dialog.dismiss());
        confirmButton.setOnClickListener(view -> {
            addEntrantToWaitlist();
            addEventToEntrant(chosenForLottery, notChosenForLottery);
            addEntrantToNotis(chosenForLottery,notChosenForLottery);
            dialog.dismiss();
            Intent eventsHome = new Intent(this,EventsHomeActivity.class);
            startActivity(eventsHome);
        });

        dialog.show();

    }

    private void addEntrantToWaitlist() {
        // Get the device ID
        @SuppressLint("HardwareIds")
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Query the Firestore for the event based on the QR code value
        db.collection("events").whereEqualTo("eventID", qrCodeValue)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                        Event event = document.toObject(Event.class); // Convert the document to an Event object

                        event.addWaitlisted(deviceId);

                        db.collection("events").document(event.getEventID())
                                .update("waitlisted", event.getWaitlisted())
                                .addOnSuccessListener(aVoid -> {
                                    // Successfully updated Firestore
                                    System.out.println("Waitlisted updated successfully.");
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure
                                    System.err.println("Error updating waitlisted: " + e.getMessage());
                                });
                    } else {
                        // Handle case where no events were found
                        Toast.makeText(this, "No event found with the specified ID.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure in retrieving the event document
                    Toast.makeText(this, "Error fetching event document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void addEventToEntrant(AtomicBoolean chosenForLottery, AtomicBoolean notChosenForLottery){
        DocumentReference entrantDocRef = db.collection("entrants").document(deviceId);

        entrantDocRef.get().addOnSuccessListener(task -> {
            if (task.exists()){
                entrantDocRef.update("waitlistedEntrants", FieldValue.arrayUnion(qrCodeValue));
                entrantDocRef.update("waitlistedEventsNotis", FieldValue.arrayUnion(qrCodeValue));
                entrantDocRef.update("finalistEventsNotis", FieldValue.arrayUnion(qrCodeValue));

//                Adding to entrants notifications events if they want notifications for that event
                if (chosenForLottery.get()){entrantDocRef.update("invitedEventsNotis", FieldValue.arrayUnion(qrCodeValue));}

                if (notChosenForLottery.get()){entrantDocRef.update("uninvitedEventsNotis", FieldValue.arrayUnion(qrCodeValue));}
            }
            else {
                // Get the current user's ID
                String userId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                // Entrant not already in the database
                Entrant newEntrant = new Entrant(userId);
                newEntrant.addWaitlistedEvents(qrCodeValue);

                newEntrant.addWaitlistedEventsNotis(qrCodeValue);
                newEntrant.addFinalistEventsNotis(qrCodeValue);

                if(chosenForLottery.get()){
                    newEntrant.addInvitedEventsNotis(qrCodeValue);
                }

                if(notChosenForLottery.get()){
                    newEntrant.addUninvitedEventsNotis(qrCodeValue);
                }

                entrantDocRef.set(newEntrant);
            }
        });
    }

    private void addEntrantToNotis(AtomicBoolean chosenForLottery, AtomicBoolean notChosenForLottery){
        // Get the device ID
        @SuppressLint("HardwareIds")
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Query the Firestore for the event based on the QR code value
        db.collection("events").whereEqualTo("eventID", qrCodeValue)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                        Event event = document.toObject(Event.class); // Convert the document to an Event object


//                       IF that entrant wants notifications then we add that entrant too the notifications for selected and or cancelled depending on what they want
                        event.addWaitlistedNotification(deviceId);
                        if (chosenForLottery.get()) { event.addSelectedNotification(deviceId); System.out.println("SelectedNotis List updated successfully.");}
                        if (notChosenForLottery.get()) { event.addCancelledNotification(deviceId); System.out.println("CancelledNotis List updated successfully."); }


//                        We update the lists that may have been changed
                        db.collection("events").document(event.getEventID())
                                .update("waitlistedNotificationsList", event.getWaitlistedNotificationsList(), // Assuming this method returns the list
                                        "selectedNotificationsList", event.getSelectedNotificationsList(),      // Assuming this method returns the list
                                        "cancelledNotificationsList", event.getCancelledNotificationsList())
                                .addOnSuccessListener(aVoid -> {
                                    // Successfully updated Firestore
                                    System.out.println("Notifications updated successfully.");
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure
                                    System.err.println("Error updating notifications: " + e.getMessage());
                                });
                    } else {
                        // Handle case where no events were found
                        Toast.makeText(this, "No event found with the specified ID.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure in retrieving the event document
                    Toast.makeText(this, "Error fetching event document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


}
