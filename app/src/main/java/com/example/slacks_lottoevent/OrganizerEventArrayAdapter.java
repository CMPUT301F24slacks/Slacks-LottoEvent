package com.example.slacks_lottoevent.refactor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.slacks_lottoevent.model.Event;
import com.example.slacks_lottoevent.OrganizerEventDetailsActivity;
import com.example.slacks_lottoevent.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Custom ArrayAdapter for displaying events in the OrganizerEventsActivity
 */
public class OrganizerEventArrayAdapter extends ArrayAdapter<Event> implements Serializable {
    private Context context;
    private ArrayList<Event> events;

    private FirebaseFirestore db;
    private CollectionReference facilitiesRef;
    private CollectionReference organizersRef;
    private CollectionReference eventsRef;
    private DocumentReference entrantsRef;

    /**
     * Constructor for the OrganzierEventArrayAdapter
     *
     * @param context The context of the activity
     * @param events  The list of events to display
     */
    public OrganizerEventArrayAdapter(@NonNull Context context, ArrayList<Event> events) {
        super(context, 0, events);
        this.context = context;
        this.events = events;
    }

    /**
     * Gets the view for each item in the list
     *
     * @param position    The position of the item in the list
     * @param convertView The view to convert
     * @param parent      The parent view
     * @return The view for the item
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Event event = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.content_organizer_events, parent, false);
        }

        db = FirebaseFirestore.getInstance();
        facilitiesRef = db.collection("facilities");
        organizersRef = db.collection("organizers");

        ImageView qrCode = convertView.findViewById(R.id.qr_code_image);
        String qrData = event.getQRData();


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


        TextView eventName = convertView.findViewById(R.id.event_name);
        TextView eventDate = convertView.findViewById(R.id.event_date);
        TextView eventTime = convertView.findViewById(R.id.event_time);
        TextView eventAddress = convertView.findViewById(R.id.event_address);
        TextView eventDescription = convertView.findViewById(R.id.event_description);
        eventName.setText(event.getName());
        eventDate.setText(event.getEventDate());
        eventTime.setText(event.getTime());

        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        organizersRef.document(deviceId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot organizerDoc = task.getResult();

                // Check if the facilityID field exists and is not empty
                if (organizerDoc.exists() && organizerDoc.contains("facilityId")) {
                    String facilityID = organizerDoc.getString("facilityId");

                    if (facilityID != null && !facilityID.isEmpty()) {
                        // Now query the facilities collection with the retrieved facilityID
                        facilitiesRef.document(facilityID).addSnapshotListener((facilitySnapshot, e) -> {
                            if (e != null) {
                                Log.w("Firestore", "Facility listen failed.", e);
                                return;
                            }

                            if (facilitySnapshot != null && facilitySnapshot.exists()) {
                                // Retrieve facility data
                                String facilityAddress= facilitySnapshot.getString("streetAddress1");
                                eventAddress.setText(facilityAddress);
                            }
                        });
                    }
                }
            }
        });

        Button eventButton = convertView.findViewById(R.id.event_button);
        eventButton.setOnClickListener(v -> {
            // Create an Intent to navigate to the EventDetailsActivity
            Intent intent = new Intent(getContext(), OrganizerEventDetailsActivity.class);
            String userId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            intent.putExtra("userId", userId);
            intent.putExtra("qrCodeValue", event.getEventID());

            // Start the OrganizerDetailsActivity
            getContext().startActivity(intent);
        });

        eventDescription.setText(event.getDescription());

//        //to connect with entrants cancelled, joined, etc.
//        eventButton.setOnClickListener(v -> {
//            // Create an Intent to navigate to OrganizerNotifications
//            Intent intent = new Intent(EventArrayAdapter.this.getContext(), OrganizerNotifications.class);
//
//            // Pass any additional data if needed
//            intent.putExtra("current_event", (Serializable) event);  // Example of passing data
//
//            // Start OrganizerNotifications
//            getContext().startActivity(intent);
//        });
        return convertView;
    }

    /**
     * Deserializes a BitMatrix from a string
     *
     * @param data The string to deserialize
     * @return The deserialized BitMatrix
     * @throws WriterException If the string cannot be deserialized
     */
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

    /**
     * Selects entrants for a lottery
     *
     * @param eventID The ID of the event
     */
    private void selectEntrantsForLottery(String eventID){
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        eventsRef.document(eventID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot eventDoc = task.getResult();

                if (eventDoc.exists()) {
                    // Retrieve the waitlist field
                    ArrayList<String> waitlist = (ArrayList<String>) eventDoc.get("waitlisted");
                    Long eventSlotsLong = (Long) eventDoc.get("eventSlots");
                    Integer eventSlots = eventSlotsLong != null ? eventSlotsLong.intValue() : 0; // Handle null case if needed
                    Integer numOfSelectedEntrants = waitlist.size() >= eventSlots ? eventSlots : waitlist.size();

                    if (waitlist != null && !waitlist.isEmpty()) {
                        // Shuffle the waitlist to get a random order
                        Collections.shuffle(waitlist);
                        List<String> selectedEntrants = waitlist.subList(0, numOfSelectedEntrants);
                        List<String> unselectedEntrants = waitlist.subList(numOfSelectedEntrants, waitlist.size());

                        // Reference to the specific event document
                        DocumentReference eventRef = eventsRef.document(eventID);

//                        Inputting entrantId in event so organizer knows who to send notifications for getting selected and who is selected
                        for (String entrant : selectedEntrants) {
                            eventRef.update("selected", FieldValue.arrayUnion(entrant),
                                            "selectedNotificationsList", FieldValue.arrayUnion(entrant));

                            entrantsRef = db.collection("entrants").document(entrant);
                            entrantsRef.get().addOnSuccessListener(entrantDoc -> {
                                if (entrantDoc.exists()) {
                                    // Assuming `notifications` is an array field in the entrant document
                                    entrantsRef.update("invitedEventsNotis", FieldValue.arrayUnion(eventID))
                                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Notification added for entrant: " + entrant))
                                            .addOnFailureListener(e -> Log.e("Firestore", "Error updating notification for entrant: " + entrant, e));
                                }
                            }).addOnFailureListener(e -> {
                                Log.e("Firestore", "Failed to retrieve entrant document: " + entrant, e);
                            });
                        }

//                        Inputting eventID into the unselected entrants so they know they aren't picked for that event (uninvited) - checking if it is not null
                        if (unselectedEntrants!= null && !unselectedEntrants.isEmpty()) {
                            for (String entrant : unselectedEntrants) {
                                entrantsRef = db.collection("entrants").document(entrant);
                                entrantsRef.get().addOnSuccessListener(entrantDoc -> {
                                    if (entrantDoc.exists()) {
                                        // Assuming `notifications` is an array field in the entrant document
                                        entrantsRef.update("uninvitedEventsNotis", FieldValue.arrayUnion(eventID),
                                                        "uninvitedEvents", FieldValue.arrayUnion(eventID))
                                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Notification added for entrant: " + entrant))
                                                .addOnFailureListener(e -> Log.e("Firestore", "Error updating notification for entrant: " + entrant, e));
                                    }
                                }).addOnFailureListener(e -> {
                                    Log.e("Firestore", "Failed to retrieve entrant document: " + entrant, e);
                                });
                            }
                        }
                    }
                }
            } else {
                Log.e("LotteryEntrant", "Failed to retrieve event document", task.getException());
            }
        });
    }
}
