package com.example.slacks_lottoevent.refactor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.slacks_lottoevent.R;

import java.util.List;

public class EntrantListsArrayAdapter extends ArrayAdapter<String> {

    public EntrantListsArrayAdapter(Context context, List<String> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        // Get the data item for this position
        String item = getItem(position);

        // Lookup view for data population
        TextView textView = convertView.findViewById(R.id.text_view_item); // Use your custom ID here
        textView.setText(item);

        // Return the completed view to render on screen
        return convertView;
    }
}
