package com.example.slacks_lottoevent;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class OrganizerCancelledFragment extends Fragment {

    private ListView listViewEntrantsCancelled;
    private ArrayList<String> entrantNames;

    private static final String ARG_CANCELLED = "cancelled";

    public OrganizerCancelledFragment() {
        // Required empty public constructor
    }

    // Factory method to create new instance with cancelled entrants list
    public static OrganizerCancelledFragment newInstance(EntrantList cancelled) {
        OrganizerCancelledFragment fragment = new OrganizerCancelledFragment();
        Bundle args = new Bundle();
        ArrayList<String> names = new ArrayList<>();

        // Convert EntrantList to ArrayList of String names
        for (Entrant entrant : cancelled.getEntrants()) {
            names.add(entrant.getUser().getName());
        }

        args.putStringArrayList(ARG_CANCELLED, names);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            entrantNames = getArguments().getStringArrayList(ARG_CANCELLED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_cancelled, container, false);

        // Setup ListView
        listViewEntrantsCancelled = view.findViewById(R.id.listViewEntrantsCancelled);

        // Set adapter with passed entrant names
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, entrantNames);
        listViewEntrantsCancelled.setAdapter(adapter);

        // Example button for sending notifications (same as original)
        Button buttonCancelNotification = view.findViewById(R.id.buttonCancelNotification);
        buttonCancelNotification.setOnClickListener(v -> showCancellationNotification("Alpha"));

        return view;
    }

    private void showCancellationNotification(String entrantName) {
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "cancellation_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Entrant Cancellations", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(getContext(), channelId)
                .setContentTitle("Entrant Cancellation")
                .setContentText(entrantName + " has cancelled their entry.")
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .build();

        notificationManager.notify(1, notification);
    }
}
