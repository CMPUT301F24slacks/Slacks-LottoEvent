package com.example.slacks_lottoevent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class EntrantListsArrayAdapter extends ArrayAdapter<String> {

    public EntrantListsArrayAdapter(Context context, List<String> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);

            // Create a ViewHolder to cache the TextView
            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.text_view_item);
            convertView.setTag(holder); // Store the holder in the view
        } else {
            holder = (ViewHolder) convertView.getTag(); // Retrieve cached holder
        }

        // Populate the data into the TextView
        String item = getItem(position);
        if (item != null) {
            holder.textView.setText(item);
        }

        // Return the completed view to render on screen
        return convertView;
    }

    // ViewHolder class to cache the views
    private static class ViewHolder {
        TextView textView;
    }

}
