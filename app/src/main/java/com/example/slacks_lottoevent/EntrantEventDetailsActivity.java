package com.example.slacks_lottoevent;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import com.example.slacks_lottoevent.databinding.ActivityEntrantEventDetailsBinding;

public class EntrantEventDetailsActivity extends AppCompatActivity {
    private ActivityEntrantEventDetailsBinding binding;
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
        binding = ActivityEntrantEventDetailsBinding.inflate(getLayoutInflater());
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
                            List<String> finalists = (List<String>) document.get("finalists");
                            List<String> invited = (List<String>) document.get("invited");
                            List<String> cancelled = (List<String>) document.get("cancelled");
                            if (waitlisted != null && waitlisted.contains(userId)) {
                                waitlisted.remove(userId);
                            }
                            if (finalists != null && finalists.contains(userId)) {
                                finalists.remove(userId);
                            }
                            if (invited != null && invited.contains(userId)) {
                                invited.remove(userId);
                            }
                            if (cancelled != null && cancelled.contains(userId)) {
                                cancelled.remove(userId);
                            }
                            db.collection("events").document(qrCodeValue).update("waitlisted", waitlisted, "finalists", finalists, "invited", invited, "cancelled", cancelled);
                        }
                    }
                });

        // Remove the entrant from the event's waitlistedNotifications and selectedNotifications
        db.collection("events").document(qrCodeValue).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            List<String> waitlistedNotifications = (List<String>) document.get("waitlistedNotificationsList");
                            List<String> selectedNotifications = (List<String>) document.get("selectedNotificationsList");
                            if (waitlistedNotifications != null && waitlistedNotifications.contains(userId)) {
                                waitlistedNotifications.remove(userId);
                            }
                            if (selectedNotifications != null && selectedNotifications.contains(userId)) {
                                selectedNotifications.remove(userId);
                            }
                            db.collection("events").document(qrCodeValue).update("waitlistedNotificationsList", waitlistedNotifications, "selectedNotificationsList", selectedNotifications);
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