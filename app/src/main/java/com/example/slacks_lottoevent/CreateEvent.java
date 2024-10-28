package com.example.slacks_lottoevent;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.slacks_lottoevent.databinding.ActivityCreateEventBinding;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.TextUtils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.UUID;

public class CreateEvent extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private ActivityCreateEventBinding binding;

    private String name;
    private String date;
    private String time;
    private String price;
    private String details;
    private String xtrDetails;
    private Integer pplAccpt;
    private Integer entrantsAccpt;
    ImageView eventPoster;


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
                finish();
            }
        });
    }

    private boolean validateInputs() {
        name = binding.eventName.getText().toString().trim();
        date = binding.eventDate.getText().toString().trim();
        time = binding.eventTime.getText().toString().trim();
        price = binding.eventPrice.getText().toString().trim();
        details = binding.eventDetails.getText().toString().trim();
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

        if (!isValidDate(date)) {
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
        if (!isValidTimeFormat(time)) {
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

        if (TextUtils.isEmpty(entrantsAccpt)) {
            entrantsAccpt = "0";
        } else {
            entrantsAccpt = String.valueOf(Integer.parseInt(entrantsAccpt));
        }
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
        name = binding.eventName.getText().toString().trim();
        date = binding.eventDate.getText().toString().trim();
        time = binding.eventTime.getText().toString().trim();
        price = binding.eventPrice.getText().toString().trim();
        details = binding.eventDetails.getText().toString().trim();
        xtrDetails = binding.extraDetails.getText().toString().trim();
//        Gets the amount of people that can get put on the waitlist
        pplAccpt = Integer.valueOf(binding.noPeopleAccpt.getText().toString().trim());
//        Number of entrants actually chosen on the waitlist
        entrantsAccpt = Integer.valueOf(binding.noEntratAccpt.getText().toString().trim());

//        unique one
        String eventId = UUID.randomUUID().toString();

        Organizer organizer = new Organizer();
        Facility facility = new Facility(organizer, "Facility Name", "2021 95th St SW");

//        QR Code Creation

        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(eventId, BarcodeFormat.QR_CODE, 300, 300);
            String hash = serializeBitMatrix(bitMatrix);

            Event event = new Event(organizer, facility, name, date, time, price, details, entrantsAccpt, pplAccpt, xtrDetails, hash);

            HashMap<String, Object> eventData = new HashMap<>();
            eventData.put("eventId", eventId);
            eventData.put("eventDetails",event);

            eventsRef.document(eventId).set(eventData)
                    .addOnSuccessListener(nothing -> {
                        System.out.println("Added to DB");
                        Toast.makeText(CreateEvent.this, "Event created successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(nothing -> {
                        System.out.println("failed");
                        Toast.makeText(CreateEvent.this, "Failed to create event", Toast.LENGTH_SHORT).show();
                    });

        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256"); // or any hashing algorithm
            byte[] hashBytes = digest.digest(data.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String serializeBitMatrix(BitMatrix bitMatrix) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < bitMatrix.getHeight(); y++) {
            for (int x = 0; x < bitMatrix.getWidth(); x++) {
                sb.append(bitMatrix.get(x, y) ? '1' : '0'); // Use '1' for black and '0' for white
            }
            sb.append('\n'); // New line for each row
        }
        return sb.toString();
    }
}