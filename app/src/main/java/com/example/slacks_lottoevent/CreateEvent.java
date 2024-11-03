package com.example.slacks_lottoevent;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

/**
 * This class is responsible for creating an event and adding it to the database.
 */
public class CreateEvent extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private ActivityCreateEventBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        binding = ActivityCreateEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");


//        Check in real time if event date is validated
        binding.eventDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Real-time validation for Date format
                if (!isValidDate(s.toString().trim())) {
                    binding.eventDate.setError("Date must be in MM/DD/YY format");
                } else {
                    binding.eventDate.setError(null); // Clear error if valid
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

//        Check in real time if eventTime is validated
        binding.eventTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Real-time validation for Time format
                if (!isValidTimeFormat(s.toString().trim())) {
                    binding.eventTime.setError("Time must be in hh:mm format");
                } else {
                    binding.eventTime.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

//        Check in real time if event price is validated
        binding.eventPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Real-time validation for Price format
                if (!s.toString().matches("\\d+(\\.\\d{0,2})?")) { // Allow up to 2 decimal places
                    binding.eventPrice.setError("Price must be a valid number (e.g., 29.99)");
                } else {
                    binding.eventPrice.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

//        Check in realtime if event slot is a number
        binding.eventSlots.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Real-time validation for Slots
                if (!s.toString().matches("\\d+")) {
                    binding.eventSlots.setError("Slots must be a number");
                } else {
                    binding.eventSlots.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

//        check in real time if waitList capacity is a number
        binding.waitListCapacity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Real-time validation for Waitlist Capacity
                String eventSlot = binding.eventSlots.getText().toString().trim();

                if (TextUtils.isEmpty(s)) {
                    // Clear any error if the waitlist capacity is empty
                    binding.waitListCapacity.setError(null);
                } else if (!s.toString().matches("\\d+")) {
                    binding.waitListCapacity.setError("Waitlist capacity must be a number");
                } else if (!TextUtils.isEmpty(eventSlot) && Integer.parseInt(s.toString()) < Integer.parseInt(eventSlot)) {
                    binding.waitListCapacity.setError("Waitlist capacity must be greater than the event slots");
                } else {
                    binding.waitListCapacity.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


//        Cancel Button
        Button cancel = findViewById(R.id.cancelBtn);
        cancel.setOnClickListener(v -> finish());

//        Create Button
//        TODO:  CHECK IF Facilities is not null, if it is null then you cannot add an event and add a pop up for it if there is one then add it and continue on with the functionality (need to get the organizers ID tho)
        Button create = findViewById(R.id.createBtn);
        create.setOnClickListener(v -> {
            if (validateInputs()) {
                createEvent();
                finish();
            }
        });
    }

    /**
     * This method validates the inputs for creating an event.
     * @return true if all inputs are valid, false otherwise
     */
    private boolean validateInputs() {
        String name = binding.eventName.getText().toString().trim();
        String date = binding.eventDate.getText().toString().trim();
        String time = binding.eventTime.getText().toString().trim();
        String price = binding.eventPrice.getText().toString().trim();
        String details = binding.eventDetails.getText().toString().trim();
        String eventSlot = binding.eventSlots.getText().toString().trim();

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

//        time validation
        if (TextUtils.isEmpty(time)) {
            binding.eventTime.setError("Event time is required");
            binding.eventTime.requestFocus();
            return false;
        }

//        Event Price validation - empty and it has to be a number
        if (TextUtils.isEmpty(price)) {
            binding.eventPrice.setError("Event price is required");
            binding.eventPrice.requestFocus();
            return false;
        }

//        Event Details validation
        if (TextUtils.isEmpty(details)) {
            binding.eventDetails.setError("Event details are required");
            binding.eventDetails.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(eventSlot)) {
            binding.eventSlots.setError("Number of People Accepted to the event are required");
            binding.eventSlots.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * This method checks if the time is in the correct format.
     * @param time the time to be checked
     * @return true if the time is in the correct format, false otherwise
     */
    private boolean isValidTimeFormat(String time) {
        // Define the expected time format (24-hour format "HH:mm" in this case)
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        timeFormat.setLenient(false); // Ensures strict parsing

        try {
            timeFormat.parse(time);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * This method checks if the date is in the correct format.
     * @param date the date to be checked
     * @return true if the date is in the correct format, false otherwise
     */
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

    /**
     * This method creates an event and adds it to the database.
     */
    private void createEvent() {
        String name = binding.eventName.getText().toString().trim();
        String date = binding.eventDate.getText().toString().trim();
        String time = binding.eventTime.getText().toString().trim();
        String price = binding.eventPrice.getText().toString().trim();
        String details = binding.eventDetails.getText().toString().trim();
        Boolean geoLoc = binding.checkBoxGeo.isChecked() ? false: true;
        Boolean waitListNotis = binding.waitingList.isChecked()? true: false;
        Boolean cancelledNotis = binding.cancelled.isChecked() ? true: false;
        Boolean selectedNotis = binding.selected.isChecked() ? true: false;

        Integer eventSlots = Integer.valueOf(binding.eventSlots.getText().toString().trim());
        String waitingListCapacity = binding.waitListCapacity.getText().toString().trim();

        if (TextUtils.isEmpty(waitingListCapacity) || Integer.parseInt(waitingListCapacity) == 0) {
            waitingListCapacity = "-1";
        }

        String eventId = UUID.randomUUID().toString();

//        TODO: after adding event, add the eventID to the event list for this specific organizer


//        QR Code Creation
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(eventId, BarcodeFormat.QR_CODE, 300, 300);
            String qrData = serializeBitMatrix(bitMatrix);
            String qrHash = generateHash(qrData);

            Event eventData =  new Event(name, date, time, price, details, eventSlots, Integer.parseInt(waitingListCapacity), qrData, eventId, geoLoc, qrHash, waitListNotis, selectedNotis, cancelledNotis);
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

    /**
     * This method generates a hash for the QR code.
     * @param data the string data to be hashed
     * @return the string hash of the data
     */
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

    /**
     * This method serializes the BitMatrix to a string.
     * @param bitMatrix the BitMatrix to be serialized
     * @return the string representation of the BitMatrix
     */
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