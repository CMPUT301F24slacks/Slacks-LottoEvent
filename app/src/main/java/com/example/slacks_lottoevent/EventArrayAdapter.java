package com.example.slacks_lottoevent;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.content_event, parent, false);
        }

        TextView eventName = convertView.findViewById(R.id.event_name);
        TextView eventDate = convertView.findViewById(R.id.event_date);
        TextView eventTime = convertView.findViewById(R.id.event_time);
        TextView eventAddress = convertView.findViewById(R.id.event_address);
        TextView eventDescription = convertView.findViewById(R.id.event_description);
        eventName.setText(event.getName());
        eventDate.setText(event.getEventDate());
        eventTime.setText(event.getTime());
        // eventAddress.setText(event.getFacilityId().getStreetAddress1());
        eventDescription.setText(event.getDescription());
        Button eventButton = convertView.findViewById(R.id.event_button);

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