package com.example.slacks_lottoevent;


import android.app.AlertDialog;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String qrCodeValue = getIntent().getStringExtra("qrCodeValue");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").whereEqualTo("eventDetails.eventID", qrCodeValue).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {

                        document = task.getResult().getDocuments().get(0);
                        Map<String, Object> eventDetails = (Map<String, Object>) document.get("eventDetails");
                        List<Object>  entrants = (List<Object>) document.get("eventDetails.finalists.entrants");
                        String eventName = (String) eventDetails.get("name");
                        date = (String) eventDetails.get("date");
                        String description = (String) eventDetails.get("description");
                        Long capacity = (Long) eventDetails.get("capacity");
                        Long pplSelected = (Long) eventDetails.get("pplSelected");
                        time = document.getString("time");
                        binding.eventTitle.setText(eventName);
                        binding.eventDate.setText(date);
                        assert capacity != null;
                        String capacityAsString = capacity.toString();
                        Map<String, Object> facilityDetails =  (Map<String, Object>) eventDetails.get("facility");
                        location = (String) facilityDetails.get("streetAddress1") + ", "+ facilityDetails.get("city") + ", " + facilityDetails.get("country") + ", " + facilityDetails.get("postalCode");
                        binding.eventLocation.setText(location);
                        String waitlistCapcity = "Waitlist Capacity " + capacityAsString;
                        binding.eventWaitlistCapacity.setText(waitlistCapcity);
                        binding.eventDescription.setText(description);

                        Long spotsRemaining = pplSelected - entrants.size();
                        String spotsRemainingText = "Only " + spotsRemaining.toString() + " spots available";
                        binding.spotsAvailable.setText(spotsRemainingText);
                        usesGeolocation = (Boolean) document.get("eventDetails.geoLocation");


                        if (capacity.equals((long) entrants.size())) {
                            // Capacity is full show we want to show the waitlist badge
                            binding.joinButton.setVisibility(View.GONE);
                            binding.waitlistFullBadge.setVisibility(View.VISIBLE);
                        } else {
                            binding.joinButton.setVisibility(View.VISIBLE);
                            binding.waitlistFullBadge.setVisibility(View.GONE);
                        }
                        // The reason to add the onClickListener in here is because we don't want the join button to do anything unless this event actually exists in the firebase
                        binding.joinButton.setOnClickListener(view -> {
                            showRegistrationDialog();



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
            bellChosen.setColorFilter(chosenForLottery.get() ? Color.parseColor("#8E24AA") : Color.parseColor("#BDBDBD"));
        });

        bellNotChosen.setOnClickListener(v -> {
            boolean negation = !notChosenForLottery.get();
            notChosenForLottery.set(negation);
            bellNotChosen.setColorFilter(notChosenForLottery.get() ? Color.parseColor("#8E24AA") : Color.parseColor("#BDBDBD"));
        });


        cancelButton.setOnClickListener(view -> dialog.dismiss());
        confirmButton.setOnClickListener(view -> {



        });

        dialog.show();

    }
}
