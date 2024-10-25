package com.example.slacks_lottoevent;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class EventArrayAdapter extends ArrayAdapter<Event> implements Serializable {
    public EventArrayAdapter(@NonNull Context context, EventList eventList) {
        super(context, 0, eventList.getEventList());
    }
}