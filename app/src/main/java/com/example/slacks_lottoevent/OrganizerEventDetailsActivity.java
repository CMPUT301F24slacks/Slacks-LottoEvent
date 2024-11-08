package com.example.slacks_lottoevent;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.slacks_lottoevent.databinding.ActivityEntrantEventDetailsBinding;
import com.example.slacks_lottoevent.databinding.ActivityOrganizerEventDetailsBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * EntrantEventDetailsActivity is the activity that displays the details of an event for an entrant.
 * The entrant can leave the event from this activity.
 */
public class OrganizerEventDetailsActivity extends AppCompatActivity {
    private ActivityOrganizerEventDetailsBinding binding;
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

    /**
     * onCreate method for EntrantEventDetailsActivity
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrganizerEventDetailsBinding.inflate(getLayoutInflater());
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

        binding.editEventButton.setVisibility(View.VISIBLE);
        // TODO: Implement the edit event button
//        binding.editEventButton.setOnClickListener(view -> {
//        });

        binding.entrantListButton.setVisibility(View.VISIBLE);
        binding.entrantListButton.setOnClickListener(view -> {
            // get the event object corresponding to the qr code
            Intent intent = new Intent(this, OrganizerNotifications.class);
            intent.putExtra("eventID", qrCodeValue);
            startActivity(intent);
        });

    }

}