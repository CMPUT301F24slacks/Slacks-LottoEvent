package com.example.slacks_lottoevent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.slacks_lottoevent.databinding.ActivityEntrantEventDetailsBinding;
import com.example.slacks_lottoevent.databinding.ActivityOrganizerEventDetailsBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private String signupDate;
    private String time;
    private Boolean usesGeolocation;
    private String description;
    private Event event;
    private String eventPosterURL;
    private String qrData;
    private String eventID;
    private CollectionReference eventsRef;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    Uri selectedImageUri;
    FirebaseFirestore db;
    String qrCodeValue;
    Long spotsRemaining;
    String spotsRemainingText;
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    Date currentDate = new Date();
    Date signup = null;
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
                        event = document.toObject(Event.class);
                        date = document.getString("eventDate");
                        signupDate = document.getString("signupDeadline");
                        time = document.getString("time");
                        eventName = document.getString("name");
                        location = document.getString("location");
                        description = document.getString("description");
                        eventPosterURL = document.getString("eventPosterURL");
                        qrData = document.getString("qrdata");
                        eventID = document.getString("eventID");
                        try {
                            signup = sdf.parse(signupDate);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            // Format the current date to "MM/dd/yyyy" and parse it back into a Date object to remove time
                            currentDate = sdf.parse(sdf.format(currentDate));
                        } catch (ParseException e) {
                            e.printStackTrace();
                            throw new RuntimeException("Error truncating current date", e);
                        }

                        List<Object> finalists = (List<Object>) document.get("finalists");

                        if (eventPosterURL != null && !eventPosterURL.isEmpty()) {
                            Glide.with(this) // 'this' refers to the activity context
                                    .load(eventPosterURL)
                                    .into(binding.eventImage);
                        } else {
                            Log.d("EventDetails", "Event poster URL is empty or null");
                        }
                        binding.eventTitle.setText(eventName);
                        binding.eventDate.setText(date);
                        List<Object> waitlisted = (List<Object>) document.get("waitlisted");
                        Long capacity = (Long) document.get("eventSlots");
                        Long waitListCapacity = (Long) document.get("waitListCapacity");
                        assert capacity != null;
                        String capacityAsString = capacity.toString();

                        //Shows the waitlist capacity and calculates the spots left on the waitlist and if there is no waitlist capacity it won't show
                        if (waitListCapacity <= 0){
                            binding.eventWaitlistCapacitySection.setVisibility(View.GONE);
                            binding.spotsAvailableSection.setVisibility(View.GONE);
                        }
                        else{
                            spotsRemaining = waitListCapacity - waitlisted.size();
                            spotsRemainingText = "Only " + spotsRemaining.toString() + " spot(s) available on waitlist";
                            binding.spotsAvailable.setText(spotsRemainingText);
                        }

                        binding.eventTitle.setText(eventName);
                        binding.eventDate.setText("Event Date: " + date);
                        binding.signupDate.setText("Signup Deadline: " + signupDate);
                        binding.eventWaitlistCapacity.setText("Waitlist Capacity: " +  waitListCapacity.toString());
                        binding.eventLocation.setText(location);
                        binding.eventSlotsCapacity.setText("Event Slots: " + capacityAsString);
                        binding.eventDescription.setText(description);
                        usesGeolocation = (Boolean) document.get("geoLocation");

                        event.setFinalists((ArrayList<String>) document.get("finalists"));
                        event.setWaitlisted((ArrayList<String>) document.get("waitlisted"));
                        event.setSelected((ArrayList<String>) document.get("selected"));
                        event.setCancelled((ArrayList<String>) document.get("cancelled"));
                        event.setReselected((ArrayList<String>) document.get("reselected"));

                        event.setWaitlistedNotificationsList((ArrayList<String>) document.get("waitlistedNotificationsList"));
                        event.setWaitlistedNotificationsList((ArrayList<String>) document.get("joinedNotificationsList"));
                        event.setWaitlistedNotificationsList((ArrayList<String>) document.get("cancelledNotificationsList"));
                        event.setWaitlistedNotificationsList((ArrayList<String>) document.get("selectedNotificationsList"));

                    }
                });
        eventsRef = db.collection("events");
        // add a listener to the event details back button, go to the last item in the back stack
        binding.eventDetailsBackButton.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.editEventButton.setVisibility(View.VISIBLE);

        binding.editEventButton.setOnClickListener(view -> {
            selectImage();
        });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null && data.getData() != null) {
                                selectedImageUri = data.getData();
                            }
                            if (selectedImageUri != null){
                                editImage(selectedImageUri);
                            }
                        }
                    }


                });

        binding.lotterySystemButton.setVisibility(View.VISIBLE);
        binding.lotterySystemButton.setOnClickListener(view -> {
            if (event == null) {
                // Show a message that the event data is not available yet
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Event data is not loaded yet. Please try again later.")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();
                return;
            }

            if (event.getWaitlisted().size() == 0){
                new AlertDialog.Builder(this)
                        .setTitle("Cannot Select")
                        .setMessage("There is no one in the waitlist.")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();
                return;

            }

            if (!currentDate.after(signup)){
                new AlertDialog.Builder(this)
                        .setTitle("Cannot Select")
                        .setMessage("Cannot select entrants until after the signup date")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();

            }

            if (event.getEntrantsChosen()){
                new AlertDialog.Builder(this)
                        .setTitle("Cannot Sample Again")
                        .setMessage("Cannot sample entrants again. You can reselect them in the List of Entrants tab.")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();
                binding.lotterySystemButton.setVisibility(View.GONE);
            }

            if (binding.lotterySystemButton.isEnabled() && !event.getEntrantsChosen() && currentDate.after(signup)) {
                event.lotterySystem();
                updateUnSelectedEntrants(event);
                updateSelectedEntrants(event);
                updateInvitedEntrants(event);
                updateUninvitedEntrants(event);

                new AlertDialog.Builder(this)
                        .setTitle("Entrants Selected")
                        .setMessage("Entrants were selected for the event.")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();

//                TODO: Send notifications for here
            }
        });


        binding.entrantListButton.setVisibility(View.VISIBLE);
        binding.entrantListButton.setOnClickListener(view -> {
            // get the event object corresponding to the qr code - huh is this not the wrong value?
            Intent intent = new Intent(this, OrganizerNotifications.class);
            intent.putExtra("eventID", qrCodeValue);
            startActivity(intent);
        });

        binding.showQrCodeButton.setOnClickListener(view -> {
            showQRCodePopup(qrData);
        });

    }

    
    private void editImage(Uri newImageUri) {
        // Step 1: Delete the old image from Google Cloud Storage
        deleteOldImage(eventPosterURL, () -> {
            // Step 2: Upload the new image
            uploadNewImage(newImageUri, newImageUrl -> {
                // Step 3: Update Firestore with the new image URL
                updateFirestoreWithNewImage(newImageUrl, success -> {
                    if (success) {
                        refreshUI(newImageUrl); // Update the UI
                    } else {
                        Toast.makeText(this, "Failed to update Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }

    private void refreshUI(String newImageUrl) {
        Glide.with(this).load(newImageUrl).into(binding.eventImage); // Update your ImageView
        Toast.makeText(this, "Image updated successfully", Toast.LENGTH_SHORT).show();
    }

    private void updateFirestoreWithNewImage(String newImageUrl, Callback<Boolean> callback) {
        eventsRef.document(eventID).update("eventPosterURL", newImageUrl)
                .addOnSuccessListener(aVoid -> {
                    callback.onComplete(true); // Notify success
                })
                .addOnFailureListener(e -> {
                    callback.onComplete(false); // Notify failure
                });
    }


    private void uploadNewImage(Uri newImageUri, Callback<String> callback) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        String fileName = formatter.format(now);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("event_posters/" + fileName);

        storageRef.putFile(newImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d("Storage", "Successfully uploaded");

                    // Retrieve the download URL
                    storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String eventPosterURL = uri.toString();
                                callback.onComplete(eventPosterURL); // Pass the URL to the callback
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Error", "Failed to get download URL", e);
                                callback.onComplete(null); // Return null to indicate failure
                            });
                })
                .addOnFailureListener(e -> {
                    Log.d("Storage", "Failed to upload");
                    callback.onComplete(null); // Return null to indicate failure
                });
    }

    private void deleteOldImage(String oldImageUrl, Runnable onSuccess) {
        if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl);
            storageReference.delete()
                    .addOnSuccessListener(aVoid -> onSuccess.run())
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to delete old image", Toast.LENGTH_SHORT).show();
                    });
        } else {
            onSuccess.run(); // No old image to delete, continue
        }
    }



    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickerLauncher.launch(intent);
    }

    private void showQRCodePopup(String qrData) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View popupView = inflater.inflate(R.layout.dialog_qr_code, null);

        ImageView qrCode = popupView.findViewById(R.id.qr_code_image);
        if (qrData != null && !qrData.isEmpty()) {
            try {
                BitMatrix bitMatrix = deserializeBitMatrix(qrData); // Convert back to BitMatrix
                BarcodeEncoder encoder = new BarcodeEncoder();
                Bitmap bitmap = encoder.createBitmap(bitMatrix); // Create Bitmap from BitMatrix
                qrCode.setImageBitmap(bitmap); // Set the QR code image
            } catch (WriterException e) {
                Log.e("QRCodeError", "Error converting QR code string to BitMatrix");
            }
        } else {
            qrCode.setImageBitmap(null); // Clear the image if QR data is null or empty
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupView)
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                .create()
                .show();

    }


    private BitMatrix deserializeBitMatrix(String data) throws WriterException {
        String[] lines = data.split("\n");
        int width = lines[0].length();
        int height = lines.length;
        BitMatrix bitMatrix = new BitMatrix(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Set pixel based on whether the character is '1' (black) or '0' (white)
                if (lines[y].charAt(x) == '1') {
                    bitMatrix.set(x, y); // Set pixel to black
                }
            }
        }
        return bitMatrix;
    }

    private void updateSelectedEntrants(Event event) {
        db.collection("events").whereEqualTo("eventID", event.getEventID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                DocumentReference eventRef = db.collection("events").document(document.getId());

                for (String entrant : event.getSelected()) {
                    eventRef.update("selected", FieldValue.arrayUnion(entrant),
                            "selectedNotificationsList", FieldValue.arrayUnion(entrant)); //TODO: when doing notifications get rid of this line
                }

                eventRef.update("waitlisted", event.getReselected()); // DB update
                eventRef.update("reselected", event.getReselected());
                eventRef.update("entrantsChosen", event.getEntrantsChosen());
            }
        });
    }

    private void updateUnSelectedEntrants(Event event){
        Log.d("Event Cancelled: ", event.getCancelled().toString());
        db.collection("events").whereEqualTo("eventID", event.getEventID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                DocumentReference eventRef = db.collection("events").document(document.getId());

                for (String entrant : event.getCancelled()) {
                    eventRef.update("cancelled", FieldValue.arrayUnion(entrant),
                            "cancelledNotificationsList", FieldValue.arrayUnion(entrant)); //TODO: when doing notifications get rid of this line
                }
            }
        });
    }

    private void updateInvitedEntrants(Event event){
        for(String entrant: event.getSelected()) {
            DocumentReference entrantsRef = db.collection("entrants").document(entrant);
            entrantsRef.get().addOnSuccessListener(entrantDoc -> {
                if (entrantDoc.exists()) {
                    entrantsRef.update("invitedEvents", FieldValue.arrayUnion(event.getEventID()),
                                    "waitlistedEvents", FieldValue.arrayRemove(event.getEventID()))
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event added for entrant"))
                            .addOnFailureListener(e -> Log.e("Firestore", "Error updating invitedEvents for entrant"));
                }
            });
        }
    }

//    Updating everyone who did not get selected and who do not want too be reselected
    private void updateUninvitedEntrants(Event event){
        for(String entrant: event.getWaitlisted()) {
            DocumentReference entrantsRef = db.collection("entrants").document(entrant);
            entrantsRef.get().addOnSuccessListener(entrantDoc -> {
                if (entrantDoc.exists()) {
                    // Assuming `notifications` is an array field in the entrant document
                    entrantsRef.update("uninvitedEvents", FieldValue.arrayUnion(event.getEventID()),
                                    "waitlistedEvents", FieldValue.arrayRemove(event.getEventID()))
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Notification added for entrant"))
                            .addOnFailureListener(e -> Log.e("Firestore", "Error updating uninvitedEvents for entrant"));

//                    TODO: Sent notification if DID NOT get selected and did not want to get reselected for the first time

                }

            });
        }
        event.setWaitlisted(event.getReselected());

    }

}