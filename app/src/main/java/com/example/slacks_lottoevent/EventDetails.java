package com.example.slacks_lottoevent;


import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.example.slacks_lottoevent.databinding.ActivityEventDetailsBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class EventDetails extends AppCompatActivity {
    private ActivityEventDetailsBinding binding;



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
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        Map<String, Object> eventDetails = (Map<String, Object>) document.get("eventDetails");
                        List<Object>  entrants = (List<Object>) document.get("eventDetails.finalists.entrants");
                        String eventName = (String) eventDetails.get("name");
                        String date = (String) eventDetails.get("date");
                        String description = (String) eventDetails.get("description");
                        Long capacity = (Long) eventDetails.get("capacity");
                        Long pplSelected = (Long) eventDetails.get("pplSelected");
                        String time = document.getString("time");
                        binding.eventTitle.setText(eventName);
                        binding.eventDate.setText(date);
                        assert capacity != null;
                        String capacityAsString = capacity.toString();
                        Map<String, Object> facilityDetails =  (Map<String, Object>) eventDetails.get("facility");
                        String location = (String) facilityDetails.get("streetAddress1") + ", "+ facilityDetails.get("city") + ", " + facilityDetails.get("country") + ", " + facilityDetails.get("postalCode");
                        binding.eventLocation.setText(location);
                        String waitlistCapcity = "Waitlist Capacity " + capacityAsString;
                        binding.eventWaitlistCapacity.setText(waitlistCapcity);
                        binding.eventDescription.setText(description);
                        System.out.println("Enterants Array: " + entrants + "People Selected" + pplSelected);
                        Long spotsRemaining = pplSelected - entrants.size();
                        String spotsRemainingText = "Only " + spotsRemaining.toString() + " spots available";
                        binding.spotsAvailable.setText(spotsRemainingText);


                    }
                });

                


    }
}
