package com.example.slacks_lottoevent;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.example.slacks_lottoevent.databinding.ActivityJoinEventDetailsBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.provider.Settings;
import android.widget.Toast;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * JoinEventDetailsActivity is the activity for the Join Event Details screen.
 */
public class JoinEventDetailsActivity extends AppCompatActivity {
    private ActivityJoinEventDetailsBinding binding;
    private DocumentSnapshot document;
    private String location;
    private String date;
    private String signupDeadline;
    private String eventName;
    private String time;
    private Boolean usesGeolocation;
    private String description;
    FirebaseFirestore db;
    String qrCodeValue;
    Long spotsRemaining;
    String spotsRemainingText;
    @SuppressLint("HardwareIds") String deviceId;
    @Override

    /**
     * onCreate method for the JoinEventDetailsActivity.
     * This method initializes the activity and sets up the event details.
     *
     * @param savedInstanceState The saved instance state
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJoinEventDetailsBinding.inflate(getLayoutInflater());
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
                        signupDeadline = document.getString("signupDeadline");
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                        Date signup = null;
                        try {
                            signup = sdf.parse(signupDeadline);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        Date currentDate = new Date();
                        try {
                            // Format the current date to "MM/dd/yyyy" and parse it back into a Date object to remove time
                            currentDate = sdf.parse(sdf.format(currentDate));
                        } catch (ParseException e) {
                            e.printStackTrace();
                            throw new RuntimeException("Error truncating current date", e);
                        }


                        List<Object> waitlisted = (List<Object>) document.get("waitlisted");
                        Long capacity = (Long) document.get("eventSlots");
                        Long waitListCapacity = (Long) document.get("waitListCapacity");
                        String waitlistCapacityAsString = "";
                        assert capacity != null;
                        String capacityAsString = capacity.toString();

//                        Shows the waitlist capacity and calculates the spots left on the waitlist and if there is no waitlist capacity it won't show
                        if (waitListCapacity <= 0){
                            binding.waitlistCapacitySection.setVisibility(View.GONE);
                            binding.spotsAvailableSection.setVisibility(View.GONE);
                        }
                        else{
                            spotsRemaining = waitListCapacity - waitlisted.size();
                            spotsRemainingText = "Only " + spotsRemaining.toString() + " spot(s) available on waitlist";
                            binding.spotsAvailable.setText(spotsRemainingText);
                        }

                        binding.eventTitle.setText(eventName);
                        binding.eventDate.setText("Event Date: " + date);
                        binding.signupDate.setText("Sign up deadline: " + signupDeadline);
                        binding.eventLocation.setText(location);
                        binding.eventSlots.setText("Event Slots: " + capacityAsString);
                        binding.eventDescription.setText(description);
                        binding.waitlistCapacity.setText("Waitlist Capacity: " + waitlistCapacityAsString);


                        usesGeolocation = (Boolean) document.get("geoLocation");


                        if (capacity.equals((long) waitlisted.size())) {
                            // Capacity is full show we want to show the waitlist badge
                            binding.joinButton.setVisibility(View.GONE);
                            binding.waitlistFullBadge.setVisibility(View.VISIBLE);
                        }
                        else {
                            binding.joinButton.setVisibility(View.VISIBLE);
                            binding.waitlistFullBadge.setVisibility(View.GONE);
                        }

                        if (currentDate.after(signup)) {
                            binding.joinButton.setVisibility(View.GONE);
                            binding.signupPassed.setVisibility(View.VISIBLE);
                        } else {
                            binding.joinButton.setVisibility(View.VISIBLE);
                            binding.signupPassed.setVisibility(View.GONE);
                        }

                        // The reason to add the onClickListener in here is because we don't want the join button to do anything unless this event actually exists in the firebase
                        binding.joinButton.setOnClickListener(view -> {
                            SharedPreferences sharedPreferences = getSharedPreferences("SlacksLottoEventUserInfo", MODE_PRIVATE);
                            boolean isSignedUp = sharedPreferences.getBoolean("isSignedUp", false);
                            if (isSignedUp){
                                showRegistrationDialog();
                            }
                            else {
                                new AlertDialog.Builder(this)
                                        .setTitle("Sign-Up Required")
                                        .setMessage("In order to join an event, we need to collect some information about you.")
                                        .setPositiveButton("Proceed", (dialog, which) -> {
                                            Intent signUpIntent = new Intent(JoinEventDetailsActivity.this, SignUpActivity.class);
                                            startActivity(signUpIntent);
                                        })
                                        .setNegativeButton("Cancel", (dialog, which) -> {
                                            dialog.dismiss();
                                        })
                                        .show();
                            }

                        });
                    }
                });
        // add a listener to the event details back button, go to the last item in the back stack
        binding.eventDetailsBackButton.setOnClickListener(v -> {
            onBackPressed();
        });

    }

    /**
     * showRegistrationDialog method for the JoinEventDetailsActivity.
     * This method shows the registration dialog for the event.
     */
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
            bellChosen.setImageResource(chosenForLottery.get() ? R.drawable.baseline_notifications_active_24 : R.drawable.baseline_circle_notifications_24);
        });

        bellNotChosen.setOnClickListener(v -> {
            boolean negation = !notChosenForLottery.get();
            notChosenForLottery.set(negation);
            bellNotChosen.setImageResource(notChosenForLottery.get() ? R.drawable.baseline_notifications_active_24 : R.drawable.baseline_circle_notifications_24);
        });


        cancelButton.setOnClickListener(view -> dialog.dismiss());
        confirmButton.setOnClickListener(view -> {
            // Get the user's unique device ID
            String userId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

            // Check if the entrant exists in Firestore
            db.collection("entrants").document(userId).get().addOnSuccessListener(task -> {
                if (task.exists()) {
                    // Entrant exists, check their events
                    Entrant entrant = task.toObject(Entrant.class);

                    // Check if the entrant is already associated with this event
                    if (entrant.getWaitlistedEvents().contains(qrCodeValue)
                            || entrant.getFinalistEvents().contains(qrCodeValue)
                            || entrant.getInvitedEvents().contains(qrCodeValue)
                            || entrant.getUninvitedEvents().contains(qrCodeValue)) {

                        // Show a toast message and dismiss the dialog
                        Toast.makeText(this, "You are already in the event", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        // Entrant is not in the event, add them to the event
                        addEntrantToWaitlist();
                        addEntrantToNotis(chosenForLottery, notChosenForLottery);
                        addEventToEntrant();
                        navigateToEventsHome();
                        dialog.dismiss();
                    }
                } else {
                    // Entrant does not exist, create a new one and add them
                    Log.d("JoinEventDetails", "Entrant does not exist. Creating a new entrant...");
                    createNewEntrant(userId);
                    addEntrantToWaitlist();
                    addEntrantToNotis(chosenForLottery, notChosenForLottery); // TODO: fix this field
                    navigateToEventsHome();
                    dialog.dismiss();
                }
            }).addOnFailureListener(e -> {
                // Handle any errors in fetching the entrant document
                Log.e("JoinEventDetails", "Error fetching entrant document: " + e.getMessage());
                // Create a new entrant in case of a failure
                createNewEntrant(userId);
                addEntrantToWaitlist();
                addEntrantToNotis(chosenForLottery, notChosenForLottery);
                navigateToEventsHome();
                dialog.dismiss();
            });
        });


        dialog.show();

    }

    /**
     * createNewEntrant method for the JoinEventDetailsActivity.
     * This method creates a new entrant in the Firestore database.
     *
     * @param userId The unique user ID
     */
    private void createNewEntrant(String userId) {
        Entrant newEntrant = new Entrant();
        newEntrant.addWaitlistedEvents(qrCodeValue); // Add the event to the waitlist
        db.collection("entrants").document(userId)
                .set(newEntrant)
                .addOnSuccessListener(aVoid -> Log.d("JoinEventDetails", "New entrant created successfully"))
                .addOnFailureListener(e -> Log.e("JoinEventDetails", "Error creating new entrant: " + e.getMessage()));
    }

    /**
     * navigateToEventsHome method for the JoinEventDetailsActivity.
     * This method navigates to the Events Home screen.
     */
    private void navigateToEventsHome() {
        Intent intent = new Intent(JoinEventDetailsActivity.this, EventsHomeActivity.class);
        startActivity(intent);
    }

    /**
     * addEntrantToWaitlist method for the JoinEventDetailsActivity.
     * This method adds the entrant to the waitlist for the event.
     */
    private void addEntrantToWaitlist(){
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        db.collection("events").whereEqualTo("eventID",qrCodeValue)
                .get()
                .addOnSuccessListener(task -> {
                    DocumentSnapshot eventDocumentSnapshot = task.getDocuments().get(0);
                    DocumentReference eventRef = eventDocumentSnapshot.getReference();
                    eventRef.update("waitlisted", FieldValue.arrayUnion(deviceId));

                })
                .addOnFailureListener(task -> {
                    System.err.println("Error fetching event document: " + task);
                });
    }

    /**
     * addEventToEntrant method for the JoinEventDetailsActivity.
     * This method adds the event to the entrant.
     */
    private void addEventToEntrant(){
        DocumentReference entrantDocRef = db.collection("entrants").document(deviceId);
        entrantDocRef.get().addOnSuccessListener(task -> {
            if (task.exists()){
                Entrant entrant = task.toObject(Entrant.class);
                entrant.addWaitlistedEvents(qrCodeValue);
                entrantDocRef.set(entrant);
            }
            else {
                // Entrant not already in the database
                Entrant newEntrant = new Entrant();
                newEntrant.getWaitlistedEvents().add(qrCodeValue);
                entrantDocRef.set(newEntrant);
            }
        });
    }

    /**
     * addEntrantToNotis method for the JoinEventDetailsActivity.
     * This method adds the entrant to the notifications for the event.
     *
     * @param chosenForLottery The boolean value for chosen for lottery
     * @param notChosenForLottery The boolean value for not chosen for lottery
     */
    private void addEntrantToNotis(AtomicBoolean chosenForLottery, AtomicBoolean notChosenForLottery){
        // Query the Firestore for the event based on the QR code value
        db.collection("events").whereEqualTo("eventID", qrCodeValue)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                        Event event = document.toObject(Event.class); // Convert the document to an Event object


//                       IF that entrant wants notifications then we add that entrant too the notifications for selected and or cancelled depending on what they want
                        event.addWaitlistedNotification(deviceId);
                        if (chosenForLottery.get()) { event.addSelectedNotification(deviceId); System.out.println("SelectedNotis List updated successfully.");}
                        if (notChosenForLottery.get()) { event.addCancelledNotification(deviceId); System.out.println("CancelledNotis List updated successfully."); }


//                        We update the lists that may have been changed
                        db.collection("events").document(event.getEventID())
                                .update("waitlistedNotificationsList", event.getWaitlistedNotificationsList(), // Assuming this method returns the list
                                        "selectedNotificationsList", event.getSelectedNotificationsList(),      // Assuming this method returns the list
                                        "cancelledNotificationsList", event.getCancelledNotificationsList())
                                .addOnSuccessListener(aVoid -> {
                                    // Successfully updated Firestore
                                    System.out.println("Notifications updated successfully.");
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure
                                    System.err.println("Error updating notifications: " + e.getMessage());
                                });
                    } else {
                        // Handle case where no events were found
                        Toast.makeText(this, "No event found with the specified ID.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure in retrieving the event document
                    Toast.makeText(this, "Error fetching event document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}