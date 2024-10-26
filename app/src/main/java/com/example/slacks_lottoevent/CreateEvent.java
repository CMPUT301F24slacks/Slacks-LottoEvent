package com.example.slacks_lottoevent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.slacks_lottoevent.databinding.ActivityCreateEventBinding;
import com.example.slacks_lottoevent.databinding.SignUpActivityBinding;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.TextUtils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class CreateEvent extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private ActivityCreateEventBinding binding;

    private EditText eventName;
    private EditText eventDate;
    private EditText eventTime;
    private EditText eventPrice;
    private EditText eventDetails;
    private EditText inputpeopleAccepted;
    private EditText entrantsAccepted;
    private EditText extraDetails;

    private String name;
    private String date;
    private String time;
    private String price;
    private String details;
    private String xtrDetails;
    private Integer pplAccpt;
    private Integer entrantsAccpt;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        binding = ActivityCreateEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");


//        Cancel Button
        Button cancel = findViewById(R.id.cancelBtn);
        cancel.setOnClickListener(v -> finish());


//        Create Button

        Button create = findViewById(R.id.createBtn);

        create.setOnClickListener(v -> {
            if (validateInputs()) {
                createEvent();
            }
        });

//        if(validateInputs()){
//            Toast.makeText(SignUpActivity.this,"Sign-Up Successful",Toast.LENGTH_SHORT).show();
//
//            // Inserting the info Device and DB
//            saveUserInfoToDevice();
//            saveUserInfoToFirebase();
////                    Intent homeIntent = new Intent(SignUpActivity.this, EventHomeActivity.class);
////                    startActivity(homeIntent);
//            finish(); // Closing the SignUpActivity to prevent any possible other Activity navigating back to it.
//        }

    }

    private boolean validateInputs() {
        String name = binding.eventName.getText().toString().trim();
        String date = binding.eventDate.getText().toString().trim();
        String time = binding.eventTime.getText().toString().trim();
        String price = binding.eventPrice.getText().toString().trim();
        String details = binding.eventDetails.getText().toString().trim();
        String pplAccpt = binding.noPeopleAccpt.getText().toString().trim();
        String entrantsAccpt = binding.noEntratAccpt.getText().toString().trim();

//        Event Name validation
        if (TextUtils.isEmpty(name)) {
            binding.eventName.setError("Event name is required");
            binding.eventName.requestFocus();
            return false;
        }
//        Event Date validation
        if (TextUtils.isEmpty(date)) {
            binding.eventDate.setError("Event date is required");
            binding.eventDate.requestFocus();
            return false;
        }

        if (!isValidDate(date)){
            binding.eventTime.setError("Event date needs to be in MM/DD/YY format");
            binding.eventTime.requestFocus();
            return false;
        }

//        time validation
        if (TextUtils.isEmpty(time)) {
            binding.eventTime.setError("Event time is required");
            binding.eventTime.requestFocus();
            return false;
        }
        if (!isValidTimeFormat(time)){
            binding.eventTime.setError("Event time needs to be in hh:mm format");
            binding.eventTime.requestFocus();
            return false;
        }

//        Event Price validation - empty and it has to be a number
        if (TextUtils.isEmpty(price)) {
            binding.eventPrice.setError("Event price is required");
            binding.eventPrice.requestFocus();
            return false;
        }
        if (!price.matches("\\d+")) {
            binding.eventPrice.setError("Event price must be a number");
            binding.eventPrice.requestFocus();
            return false;
        }

//        Event Details validation
        if (TextUtils.isEmpty(details)) {
            binding.eventDetails.setError("Event details are required");
            binding.eventDetails.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(pplAccpt)) {
            binding.eventDetails.setError("Number of People Accepted are required");
            binding.eventDetails.requestFocus();
            return false;
        }

//        if (TextUtils.isEmpty(entrantsAccpt)) {
//            entrantsAccpt = null;
//        } else {
//            entrantsAccpt = String.valueOf(Integer.parseInt(entrantsAccpt));
//        }
        return true;
    }

    private boolean isValidTimeFormat(String time) {
        // Define the expected time format (24-hour format "HH:mm" in this case)
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        timeFormat.setLenient(false); // Ensures strict parsing

        try {
            // Try parsing the input time string
            timeFormat.parse(time);
            return true; // Valid if parsing succeeds
        } catch (ParseException e) {
            return false; // Invalid if parsing fails
        }
    }

    private boolean isValidDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateFormat.setLenient(false); // Ensures strict date parsing

        try {
            dateFormat.parse(date); // Parse the date
            return true; // Parsing succeeded, date is valid
        } catch (ParseException e) {
            return false; // Parsing failed, date is invalid
        }
    }

    private void createEvent() {
        String name = binding.eventName.getText().toString().trim();
        String date = binding.eventDate.getText().toString().trim();
        String time = binding.eventTime.getText().toString().trim();
        String price = binding.eventPrice.getText().toString().trim();
        String details = binding.eventDetails.getText().toString().trim();

//        unique one

        HashMap<String, Object> event = new HashMap<>();
        event.put("name", name);
        event.put("date", date);
        event.put("time", time);
        event.put("price", price);
        event.put("details", details);

        eventsRef.add(event)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(CreateEvent.this, "Event Created", Toast.LENGTH_SHORT).show();
                    generateQRCode(documentReference.getId()); // Generate QR code with the document ID
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CreateEvent.this, "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }




}