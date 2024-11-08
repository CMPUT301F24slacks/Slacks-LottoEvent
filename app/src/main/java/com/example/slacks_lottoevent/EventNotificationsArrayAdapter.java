package com.example.slacks_lottoevent;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Custom ArrayAdapter for displaying event notifications in a ListView.
 */
public class EventNotificationsArrayAdapter extends ArrayAdapter<UserEventNotifications> {
    private final Context context;
    private final FirebaseFirestore db;
    private final String deviceId;

    /**
     * Constructor for the ArrayAdapter.
     *
     * @param context The current context.
     * @param events The list of events to display.
     */
    public EventNotificationsArrayAdapter(@NonNull Context context, ArrayList<UserEventNotifications> events) {
        super(context, 0, events);
        this.context = context;
        this.db = FirebaseFirestore.getInstance();

        // Retrieve device ID for Firestore updates
        this.deviceId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }

    /**
     * Get the view for a single event notification.
     *
     * @param position The position of the event in the list.
     * @param convertView The view to convert.
     * @param parent The parent view.
     * @return The view for the event.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.content_notifications_user, parent, false);
        }

        UserEventNotifications event = getItem(position);

        // Populate views with event data
        TextView eventName = convertView.findViewById(R.id.user_notification_event_name);
        TextView eventDate = convertView.findViewById(R.id.user_notification_event_date);
        TextView eventTime = convertView.findViewById(R.id.user_notification_event_time);
        TextView eventLocation = convertView.findViewById(R.id.user_notification_event_location);
        Button acceptButton = convertView.findViewById(R.id.Accept_Invitation);
        Button declineButton = convertView.findViewById(R.id.Decline_Invitation);

        if (event != null) {
            eventName.setText(event.getName());
            eventDate.setText(event.getDate());
            eventTime.setText(event.getTime());
            eventLocation.setText(event.getLocation());

            // Set up Accept button
            acceptButton.setOnClickListener(v -> handleAcceptEvent(event));

            // Set up Decline button
            declineButton.setOnClickListener(v -> handleDeclineEvent(event));
        }

        return convertView;
    }

    /**
     * Handle the "Accept" action for an event.
     * @param event The event to accept.
     */
    private void handleAcceptEvent(UserEventNotifications event) {
        String eventId = event.getEventId();

        // Update Firestore: Move event from "invitedEvents" to "finalistEvents"
        db.collection("entrants").document(deviceId).update(
                        "invitedEvents", FieldValue.arrayRemove(eventId),
                        "finalistEvents", FieldValue.arrayUnion(eventId)
                ).addOnSuccessListener(aVoid -> Log.d("Firestore", "Event accepted: " + eventId))
                .addOnFailureListener(e -> Log.e("Firestore", "Error accepting event: " + eventId, e));
    }

    /**
     * Handle the "Decline" action for an event.
     * @param event The event to decline.
     */
    private void handleDeclineEvent(UserEventNotifications event) {
        String eventId = event.getEventId();

        // Update Firestore: Remove event from "invitedEvents" and remove device ID from the event's "invited" list
        db.collection("entrants").document(deviceId).update(
                "invitedEvents", FieldValue.arrayRemove(eventId)
        ).addOnSuccessListener(aVoid -> {
            // Also remove this device from the event's "invited" list
            db.collection("events").document(eventId).update(
                            "invited", FieldValue.arrayRemove(deviceId)
                    ).addOnSuccessListener(aVoid1 -> Log.d("Firestore", "Event declined: " + eventId))
                    .addOnFailureListener(e -> Log.e("Firestore", "Error updating event invite list: " + eventId, e));
        }).addOnFailureListener(e -> Log.e("Firestore", "Error declining event: " + eventId, e));
    }
}
