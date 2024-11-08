package com.example.slacks_lottoevent;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

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

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventDetails extends AppCompatActivity {
    private ActivityEventDetailsBinding binding;
    private DocumentSnapshot document;
    private String location;
    private String date;
    private String eventName;
    private String time;
    private Boolean usesGeolocation;
    private String description;
    FirebaseFirestore db;
    String qrCodeValue;
    @SuppressLint("HardwareIds") String deviceId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        qrCodeValue = getIntent().getStringExtra("qrCodeValue");

        db = FirebaseFirestore.getInstance();
        db.collection("events").whereEqualTo("eventID", qrCodeValue).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {

                        document = task.getResult().getDocuments().get(0);
                        date = document.getString("date");
                        time = document.getString("time");
                        eventName = document.getString("name");
                        location = document.getString("location");
                        description = document.getString("description");

                        List<Object> finalists = (List<Object>) document.get("finalists");
                        binding.eventTitle.setText(eventName);
                        binding.eventDate.setText(date);
                        Long capacity = (Long) document.get("eventSlots");
                        Long waitListCapacity = (Long) document.get("waitListCapacity");
                        assert capacity != null;
                        String capacityAsString = capacity.toString();

                        binding.eventLocation.setText(location);
                        String waitlistCapacity = "Waitlist Capacity " + capacityAsString;
                        binding.eventWaitlistCapacity.setText(waitlistCapacity);
                        binding.eventDescription.setText(description);

                        Long spotsRemaining = capacity - finalists.size();
                        String spotsRemainingText = "Only " + spotsRemaining.toString() + " spots available";
                        binding.spotsAvailable.setText(spotsRemainingText);
                        usesGeolocation = (Boolean) document.get("geoLocation");


                        if (capacity.equals((long) finalists.size())) {
                            // Capacity is full show we want to show the waitlist badge
                            binding.joinButton.setVisibility(View.GONE);
                            binding.waitlistFullBadge.setVisibility(View.VISIBLE);
                        } else {
                            binding.joinButton.setVisibility(View.VISIBLE);
                            binding.waitlistFullBadge.setVisibility(View.GONE);
                        }
                        // The reason to add the onClickListener in here is because we don't want the join button to do anything unless this event actually exists in the firebase
                        binding.joinButton.setOnClickListener(view -> {
                            SharedPreferences sharedPreferences = getSharedPreferences("SlacksLottoEventUserInfo", MODE_PRIVATE);
                            boolean isSignedUp = sharedPreferences.getBoolean("isSignedUp", false);
                            if (isSignedUp){
                                showRegistrationDialog();
                            }
                            else {
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
            addEntrantToNotis(chosenForLottery,notChosenForLottery);
            addEventToEntrant();
            Intent intent = new Intent(EventDetails.this,EventsHomeActivity.class);
            startActivity(intent);
            dialog.dismiss();

        });

        dialog.show();

    }


    private void addEntrantToWaitlist(){
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        db.collection("events").whereEqualTo("eventID",qrCodeValue)
                .get()
                .addOnSuccessListener(task -> {
                    DocumentSnapshot eventDocumentSnapshot = task.getDocuments().get(0);
                    DocumentReference eventRef = eventDocumentSnapshot.getReference();
                    eventRef.update("waitlisted", FieldValue.arrayUnion(deviceId));

                })
                .addOnFailureListener(task -> {
                    System.err.println("Error fetching event document: " + task);
                });
    }
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


    private void addEntrantToNotis(AtomicBoolean chosenForLottery, AtomicBoolean notChosenForLottery){



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