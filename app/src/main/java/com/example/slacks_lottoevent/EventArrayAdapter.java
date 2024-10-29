package com.example.slacks_lottoevent;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.w3c.dom.Text;

import java.io.Serializable;

/**
 * EventArrayAdapter is a custom ArrayAdapter that is used to display the individual events in event lists.
 * It is used to display the name, date, time, address, and description of each event.
 */
public class EventArrayAdapter extends ArrayAdapter<Event> implements Serializable {
    public EventArrayAdapter(@NonNull Context context, EventList eventList) {
        super(context, 0, eventList.getEventList());
    }

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
        eventDate.setText(event.getDate());
        eventTime.setText(event.getTime());
        eventAddress.setText(event.getFacility().getAddress());
        eventDescription.setText(event.getDescription());
        Button eventButton = convertView.findViewById(R.id.event_button);

        eventButton.setOnClickListener(v -> {
            // Create an Intent to navigate to Organizer_MainActivity
            Intent intent = new Intent(getContext(), Organizer_MainActivity.class);

            // Pass any additional data if needed
            // intent.putExtra("EXTRA_EVENT_ID", event.getId());  // Example of passing data

            // Start Organizer_MainActivity
            getContext().startActivity(intent);
        });

        return convertView;
    }
}