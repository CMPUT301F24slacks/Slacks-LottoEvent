package com.example.slacks_lottoevent.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.slacks_lottoevent.model.Entrant;
import com.example.slacks_lottoevent.model.Event;
import com.example.slacks_lottoevent.databinding.ActivityEntrantEventDetailsBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * EntrantEventDetailsActivity is the activity that displays the details of an event for an entrant.
 * The entrant can leave the event from this activity.
 */
public class EntrantEventDetailsActivity extends AppCompatActivity {
    FirebaseFirestore db;
    String qrCodeValue;
    Integer spotsRemaining;
    String spotsRemainingText;
    @SuppressLint("HardwareIds")
    String deviceId;
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
    private Boolean entrantsChosen;

    /**
     * onCreate method for EntrantEventDetailsActivity
     *
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
                  entrantsChosen = document.getBoolean("entrantsChosen");

                  List<Object> waitlisted = (List<Object>) document.get("waitlisted");
                  List<Object> finalist = (List<Object>) document.get("finalists");

                  Long capacity = (Long) document.get("eventSlots");
                  Long waitListCapacity = (Long) document.get("waitListCapacity");
                  assert capacity != null;
                  Integer eventSlots = capacity.intValue() - finalist.size();
                  String capacityAsString = eventSlots.toString();

                  spotsRemaining = waitListCapacity.intValue() - waitlisted.size();

                  if (waitListCapacity == 0) {
//                            Does not show badge if there is no waitlistCapacity section
                      binding.waitlistCapacitySection.setVisibility(View.GONE);
                      binding.spotsAvailableSection.setVisibility(View.GONE);
                  } else if (waitListCapacity > 0) {
//                            There is a waitlist capacity and shows the spots left
                      spotsRemaining = spotsRemaining > 0 ? spotsRemaining : 0;
                      spotsRemainingText = "Only " + spotsRemaining +
                                           " spot(s) available on waitlist";
                      binding.spotsAvailable.setText(spotsRemainingText);

                      if (spotsRemaining <= 0) {
                          binding.waitlistFullBadge.setVisibility(View.VISIBLE);
                      } else if (entrantsChosen) {
                          spotsRemainingText = "Only 0 spots available on waitlist";
                          binding.spotsAvailable.setText(spotsRemainingText);
                      }
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
                  binding.eventTime.setText("Event Time: " + time);
                  binding.eventsignupDeadline.setText("Signup Deadline: " + signupDate);
                  binding.eventLocation.setText(location);
                  binding.eventWaitlistCapacity.setText("Event Slots: " + capacityAsString);
                  binding.eventDescription.setText(description);
                  binding.waitlistCapacity.setText(
                          "Waitlist Capacity: " + waitListCapacity);
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
                        Intent intent = new Intent(this, MainActivity.class);
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
        db.collection("events").document(qrCodeValue).get().addOnSuccessListener(task -> {
            if (task.exists()) {
                Event event = task.toObject(Event.class);
                if (userId != null && event != null) {
                    event.removeEntrant(userId);
                    // print the event joinLocations
                    Log.d("EntrantEventDetails",
                          "EntrantEventDetails: " + event.getJoinLocations());
                    db.collection("events").document(qrCodeValue).set(event);
                }
            }
        });

        // Remove the event from the user's list of events
        db.collection("entrants").document(userId).get().addOnSuccessListener(task -> {
            if (task.exists()) {
                Entrant entrant = task.toObject(Entrant.class);
                entrant.getWaitlistedEvents().remove(qrCodeValue);
                entrant.getFinalistEvents().remove(qrCodeValue);
                entrant.getInvitedEvents().remove(qrCodeValue);
                entrant.getUninvitedEvents().remove(qrCodeValue);
                db.collection("entrants").document(userId).set(entrant);
            }
        });

    }

}