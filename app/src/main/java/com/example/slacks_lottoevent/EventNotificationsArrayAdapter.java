package com.example.slacks_lottoevent;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
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
    SharedPreferences sharedPreferences;


    /**
     * Constructor for the ArrayAdapter.
     *
     * @param context The current context.
     * @param events The list of events to display.
     */
    public EventNotificationsArrayAdapter(@NonNull Context context, ArrayList<UserEventNotifications> events, SharedPreferences sharedPreferences) {
        super(context, 0, events);
        this.context = context;
        this.db = FirebaseFirestore.getInstance();

        // Retrieve device ID for Firestore updates
        this.deviceId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        this.sharedPreferences = sharedPreferences;
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
        Button okayButton = convertView.findViewById(R.id.Okay_Button);

        if (event != null) {
            eventName.setText(event.getName());
            eventDate.setText(event.getDate());
            eventTime.setText(event.getTime());
            eventLocation.setText(event.getLocation());

            if (!event.getSelected()){
                acceptButton.setVisibility(View.GONE);
                declineButton.setVisibility(View.GONE);
                okayButton.setVisibility(View.VISIBLE);
            }


            //make changes to both events and entrants, not only events
            acceptButton.setOnClickListener(v -> {
                AdminActivity.showAdminAlertDialog(context, () -> handleAcceptEvent(event), "Enrollment Confirmed",
                        "You have opted to join the event.", "Note: You can still forfeit your position and leave the event",
                        "Cancel", "Confirm", null);
                removeEvent(position);
            });
            //make changes to both events and entrants, not only events
            declineButton.setOnClickListener(v -> {
                AdminActivity.showAdminAlertDialog(context, () -> handleDeclineEvent(event), "Declination Confirmed",
                        "You have opted to decline the event.", "WARNING: You cannot forfeit your invitation",
                        "Cancel", "Confirm", null);
                removeEvent(position);
            });

            okayButton.setOnClickListener(v->{
                AdminActivity.showAdminAlertDialog(context, () -> saveEventAsDisplayed(event.getEventId()), "Don't Give Up!",
                        "You have opted to rid of this message.",
                        "WARNING: This notice is meant to remind you of the possible opportunity, reminder cannot be undeleted",
                        null, "OK", null);
                removeEvent(position);
            });
        }

        return convertView;
    }

    /**
     * Handle the "Accept" action for an event.
     * @param event The event to accept.
     */
    private void handleAcceptEvent(UserEventNotifications event) {
        //        Removes from selected array and puts into the finalists array
        String eventId = event.getEventId();
        db.collection("entrants").document(deviceId).update(
                        "invitedEvents", FieldValue.arrayRemove(eventId),
                        "finalistEvents", FieldValue.arrayUnion(eventId)
                ).addOnSuccessListener(aVoid -> Log.d("Firestore", "Event accepted: " + eventId))
                .addOnFailureListener(e -> Log.e("Firestore", "Error accepting event: " + eventId, e));

        db.collection("events").document(eventId).update(
                "selected", FieldValue.arrayRemove(deviceId),
                "selectedNotificationsList", FieldValue.arrayRemove(deviceId),
                "finalists", FieldValue.arrayUnion(deviceId),
                "joinedNotificationsList", FieldValue.arrayUnion(deviceId));
    }

    /**
     * Handle the "Decline" action for an event.
     * @param event The event to decline.
     */
    private void handleDeclineEvent(UserEventNotifications event) {
//        Removes from selected array and puts into the cancelled array
        String eventId = event.getEventId();

        db.collection("entrants").document(deviceId).update(
                "invitedEvents", FieldValue.arrayRemove(eventId),
                "uninvitedEvents", FieldValue.arrayRemove(eventId)).addOnSuccessListener(aVoid -> {

            db.collection("events").document(eventId).update(
                            "selected", FieldValue.arrayRemove(deviceId),
                    "selectedNotificationsList", FieldValue.arrayRemove(deviceId),
                    "cancelled", FieldValue.arrayUnion(deviceId),
                    "cancelledNotificationsList", FieldValue.arrayUnion(deviceId))
                    .addOnSuccessListener(aVoid1 -> Log.d("Firestore", "Event declined: " + eventId))
                    .addOnFailureListener(e -> Log.e("Firestore", "Error updating event invite list: " + eventId, e));
        }).addOnFailureListener(e -> Log.e("Firestore", "Error declining event: " + eventId, e));
    }

    /**
     * Method to remove the event and refresh the list.
     * @param position of that event in the list.
     */
    private void removeEvent(int position) {
        // Remove item from the data source
        remove(getItem(position));
        // Notify the adapter of data change
        notifyDataSetChanged();
    }

    /**
     * Save the event ID in SharedPreferences to avoid displaying it again.
     * @param eventId The ID of the event to save.
     */
    private void saveEventAsDisplayed(String eventId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(eventId, true); // Save event ID with a true value indicating it's displayed
        editor.apply();
    }

}
