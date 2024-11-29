package com.example.slacks_lottoevent;

import android.content.Context;
import android.content.Intent;
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
import com.example.slacks_lottoevent.model.User;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * EventArrayAdapter is a custom ArrayAdapter that is used to display the individual events in event lists.
 * It is used to display the name, date, time, address, and description of each event.
 */
public class EventArrayAdapter extends ArrayAdapter<Event> implements Serializable {

    public EventArrayAdapter(@NonNull Context context, ArrayList eventList) {
        super(context, 0, eventList);
    }

    /**
     * getView is a method that is called to display the individual events in the event list.
     * It sets the text of the event name, date, time, address, and description.
     * It also sets the onClickListener for the event button to navigate to the EventDetailsActivity.
     *
     * @param position    The position of the event in the event list
     * @param convertView The view that is being converted
     * @param parent      The parent view group
     * @return The view that is being displayed
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Event event = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_event, parent, false);
        }

        // Initialize the main TextViews and Button
        TextView eventName = convertView.findViewById(R.id.event_name);
        TextView eventDate = convertView.findViewById(R.id.event_date);
        TextView eventTime = convertView.findViewById(R.id.event_time);
        TextView eventAddress = convertView.findViewById(R.id.event_address);
        TextView eventDescription = convertView.findViewById(R.id.event_description);
        Button eventButton = convertView.findViewById(R.id.event_button);

        // Initialize the TextViews for the event status
        TextView statusAttending = convertView.findViewById(R.id.status_attending);
        TextView statusInvited = convertView.findViewById(R.id.status_invited);
        TextView statusUnselected = convertView.findViewById(R.id.status_unselected);
        TextView statusWaitlisted = convertView.findViewById(R.id.status_waitlisted);

        // Set the name, date, time, and description of the event
        ImageView eventPoster = convertView.findViewById(R.id.event_image);
        eventName.setText(event.getName());
        eventDate.setText(event.getEventDate());
        eventTime.setText(event.getTime());
        eventAddress.setText(event.getLocation());
        eventDescription.setText(event.getDescription());
        // Set the address with a newline after removing the first comma
        if (event.getLocation() != null && event.getLocation().contains(",")) {
            String[] addressParts = event.getLocation().split(",", 2);
            eventAddress.setText(addressParts[0] + "\n" + addressParts[1].trim());
        } else {
            // If no comma is found, display the address as it is
            eventAddress.setText(event.getLocation());
        }

        if (event.getEventPosterURL() != null && !event.getEventPosterURL().isEmpty()) {
            Glide.with(this.getContext()) // 'this' refers to the activity context
                    .load(event.getEventPosterURL())
                    .into(eventPoster);
        } else {
            Log.d("EventDetails", "Event poster URL is empty or null");
        }

        User user = User.getInstance();
        String deviceId = user.getDeviceId();
        if (event.getWaitlisted().contains(deviceId)) {
            statusWaitlisted.setVisibility(View.VISIBLE);
        } else if (event.getSelected().contains(deviceId)) {
            statusInvited.setVisibility(View.VISIBLE);
        } else if (event.getFinalists().contains(deviceId)) {
            statusAttending.setVisibility(View.VISIBLE);
        } else if (event.getCancelled().contains(deviceId)) {
            statusUnselected.setVisibility(View.VISIBLE);
        }

        // Set an OnClickListener for the event button
        eventButton.setOnClickListener(v -> {
            // Create an Intent to navigate to the EventDetailsActivity
            Intent intent = new Intent(getContext(), EntrantEventDetailsActivity.class);
            String userId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            intent.putExtra("userId", userId);
            intent.putExtra("qrCodeValue", event.getEventID());

            // Start the EventDetailsActivity
            getContext().startActivity(intent);
        });

        return convertView;
    }
}