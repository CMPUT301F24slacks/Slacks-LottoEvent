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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganizerCancelledFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganizerCancelledFragment extends Fragment {

    private ListView listViewEntrantsCancelled;
    private ArrayList<String> dummyEntrants;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrganizerCancelledFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrganizerCancelledFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrganizerCancelledFragment newInstance(String param1, String param2) {
        OrganizerCancelledFragment fragment = new OrganizerCancelledFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_cancelled, container, false);

        // Setup ListView
        listViewEntrantsCancelled = view.findViewById(R.id.listViewEntrantsCancelled);

        // Dummy data
        dummyEntrants = new ArrayList<>();
        dummyEntrants.add("Alpha");
        dummyEntrants.add("Beta");
        dummyEntrants.add("Charlie");

        // Adapter to populate ListView
        EntrantListsArrayAdapter adapter = new EntrantListsArrayAdapter(getContext(), dummyEntrants);
        listViewEntrantsCancelled.setAdapter(adapter);

        // The button will be replaced by the list modification functionality
        Button buttonCancelNotification = view.findViewById(R.id.buttonCancelNotification);
        buttonCancelNotification.setOnClickListener(v -> showCancellationNotification("Alpha"));


        return view;
    }

    // TODO: If an entrantName cancelled their selection/enrollment:
    //Note: Will have to enable app notifications for slacks-lotto from the settings in the emulator
    private void showCancellationNotification(String entrantName) {
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "cancellation_channel";

        // Only for Android 8.0 and higher
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

    // TODO: another button for re-selection from organizer part

}


