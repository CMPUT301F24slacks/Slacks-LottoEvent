package com.example.slacks_lottoevent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
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

import com.bumptech.glide.Glide;
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

        TextView eventName = convertView.findViewById(R.id.event_name);
        TextView eventDate = convertView.findViewById(R.id.event_date);
        TextView eventTime = convertView.findViewById(R.id.event_time);
        TextView eventAddress = convertView.findViewById(R.id.event_address);
        TextView eventDescription = convertView.findViewById(R.id.event_description);
        ImageView eventPoster = convertView.findViewById(R.id.event_image);
        eventName.setText(event.getName());
        eventDate.setText(event.getEventDate());
        eventTime.setText(event.getTime());
//        eventAddress.setText(event.getLocation());

//        TODO: Can delete the below code once we know that the event location and facility are lined up.

        if (event.getEventPosterURL() != null && !event.getEventPosterURL().isEmpty()) {
            Glide.with(this.getContext()) // 'this' refers to the activity context
                    .load(event.getEventPosterURL())
                    .into(eventPoster);
        } else {
            Log.e("EventDetails", "Event poster URL is empty or null");
        }
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

        return convertView;
    }

}
