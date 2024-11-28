package com.example.slacks_lottoevent;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import com.example.slacks_lottoevent.databinding.ActivityEntrantEventDetailsBinding;

/**
 * EntrantEventDetailsActivity is the activity that displays the details of an event for an entrant.
 * The entrant can leave the event from this activity.
 */
public class EntrantEventDetailsActivity extends AppCompatActivity {
    private ActivityEntrantEventDetailsBinding binding;
    private DocumentSnapshot document;
    private String location;
    private String date;
    private String signupDate;
    private String eventName;
    private String time;
    private Boolean usesGeolocation;
    private String description;
    private String eventPosterURL;
    FirebaseFirestore db;
    String qrCodeValue;
    Long spotsRemaining;
    String spotsRemainingText;
    @SuppressLint("HardwareIds") String deviceId;

    /**
     * onCreate method for EntrantEventDetailsActivity
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEntrantEventDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        qrCodeValue = getIntent().getStringExtra("qrCodeValue");

        db = FirebaseFirestore.getInstance();
        db.collection("events").whereEqualTo("eventID", qrCodeValue).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {

                        document = task.getResult().getDocuments().get(0);
                        date = document.getString("eventDate");
                        time = document.getString("time");
                        eventName = document.getString("name");
                        location = document.getString("location");
                        description = document.getString("description");
                        signupDate = document.getString("signupDeadline");
                        eventPosterURL = document.getString("eventPosterURL");

                        List<Object> waitlisted = (List<Object>) document.get("waitlisted");

                        Long capacity = (Long) document.get("eventSlots");
                        Long waitListCapacity = (Long) document.get("waitListCapacity");
                        assert capacity != null;
                        String capacityAsString = capacity.toString();

                        //Shows the waitlist capacity and calculates the spots left on the waitlist and if there is no waitlist capacity it won't show
                        if (waitListCapacity <= 0){
                            binding.waitlistCapacitySection.setVisibility(View.GONE);
                            binding.spotsAvailableSection.setVisibility(View.GONE);
                        }
                        else{
                            spotsRemaining = waitListCapacity - waitlisted.size();
                            spotsRemainingText = "Only " + spotsRemaining.toString() + " spot(s) available on waitlist";
                            binding.spotsAvailable.setText(spotsRemainingText);
                        }
                        if (eventPosterURL != null && !eventPosterURL.isEmpty()) {
                            Glide.with(this) // 'this' refers to the activity context
                                    .load(eventPosterURL)
                                    .into(binding.eventImage);
                        } else {
                            Log.d("EventDetails", "Event poster URL is empty or null");
                        }

                        binding.eventTitle.setText(eventName);
                        binding.eventDate.setText("Event Date: " + date);
                        binding.eventsignupDeadline.setText("Signup Deadline: "+ signupDate);
                        binding.eventLocation.setText(location);
                        binding.eventWaitlistCapacity.setText("Event Slots: " + capacityAsString);
                        binding.eventDescription.setText(description);
                        binding.waitlistCapacity.setText("Waitlist Capacity: " + waitListCapacity.toString());
                        usesGeolocation = (Boolean) document.get("geoLocation");


                    }
                });
        // add a listener to the event details back button, go to the last item in the back stack
        binding.eventDetailsBackButton.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.leaveButton.setVisibility(View.VISIBLE);

        binding.leaveButton.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setTitle("Leave Event")
                    .setMessage("Are you sure you want to leave this event?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        entrantLeaveEvent(); // Call the method to remove the user from the event

                        // Return to the events home activity
                        Intent intent = new Intent(this, EventsHomeActivity.class);
                        startActivity(intent);
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss(); // Cancel and return
                    })
                    .show();
        });

    }

    /**
     * Method to remove the entrant from the event
     */
    private void entrantLeaveEvent() {
        String userId = getIntent().getStringExtra("userId");
        // Remove the entrant from the event's list of entrants
        db.collection("events").document(qrCodeValue).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            List<String> waitlisted = (List<String>) document.get("waitlisted");
                            List<String> waitlistedNotifications = (List<String>) document.get("waitlistedNotificationsList");

                            if (waitlisted != null && waitlisted.contains(userId)) {
                                waitlisted.remove(userId);
                            }

                            if (waitlistedNotifications != null && waitlistedNotifications.contains(userId)) {
                                waitlistedNotifications.remove(userId);
                            }

                            db.collection("events").document(qrCodeValue).update("waitlisted", waitlisted, "waitlistedNotificationsList", waitlistedNotifications);
                        }
                    }
                });

        // Remove the event from the user's list of events
        db.collection("entrants").document(userId).get().addOnSuccessListener(task -> {
            if (task.exists()) {
                Entrant entrant = task.toObject(Entrant.class);
                if (entrant.getWaitlistedEvents().contains(qrCodeValue)) {
                    entrant.getWaitlistedEvents().remove(qrCodeValue);
                }
                if (entrant.getFinalistEvents().contains(qrCodeValue)) {
                    entrant.getFinalistEvents().remove(qrCodeValue);
                }
                if (entrant.getInvitedEvents().contains(qrCodeValue)) {
                    entrant.getInvitedEvents().remove(qrCodeValue);
                }
                if (entrant.getUninvitedEvents().contains(qrCodeValue)) {
                    entrant.getUninvitedEvents().remove(qrCodeValue);
                }
                db.collection("entrants").document(userId).set(entrant);
            }
        });

    }

}