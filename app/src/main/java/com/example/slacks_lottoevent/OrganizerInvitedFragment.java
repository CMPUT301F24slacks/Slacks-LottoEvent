package com.example.slacks_lottoevent;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganizerInvitedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganizerInvitedFragment extends Fragment {

    private ListView ListViewEntrantsInvited;
    private String eventId;
    private static final String ARG_EVENT_ID = "eventID";
    private FirebaseFirestore db;
    private ArrayList<Profile> profileList;

    /**
     * Default constructor
     */
    public OrganizerInvitedFragment() {}

    /**
     * Factory method to create a new instance of this fragment using the provided parameters.
     * @param eventId The current event's ID.
     * @return A new instance of OrganizerInvitedFragment.
     */
    public static OrganizerInvitedFragment newInstance(String eventId) {
        OrganizerInvitedFragment fragment = new OrganizerInvitedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId); // Pass the event ID as a String
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        profileList = new ArrayList<>(); // Initialize the profile list
        // Retrieve the event ID from the fragment's arguments
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_organizer_invited, container, false);
        ListViewEntrantsInvited = view.findViewById(R.id.listViewEntrantsInvited);

        ProfileListArrayAdapter adapter = new ProfileListArrayAdapter(getContext(), profileList, false);
        ListViewEntrantsInvited.setAdapter(adapter);

        Button craftMessageButton = view.findViewById(R.id.craftMessage);
        Button cancelEntrantsButton = view.findViewById(R.id.cancel_entrants);
        Notifications notifications = new Notifications();

        craftMessageButton.setOnClickListener(v -> DialogHelper.showMessageDialog(getContext(), notifications,eventId, "selectedNotificationsList"));

        //1. get the event day and see if it less than 1/2 a day away -> if so let the user move the entrants that havent responded yet but if not show an alert dialog/toast
        // remove the entrants from their respective lists
        cancelEntrantsButton.setOnClickListener(v -> {
            // Get the event document from Firestore
            Log.d("inside", "here in cancel entrants button");
            db.collection("events").document(eventId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot eventDoc = task.getResult();

                    // Get the event date from the document
                    String eventDateStr = eventDoc.getString("eventDate"); // Assuming `eventDate` is stored as a string in Firestore
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());

                    try {
                        Date eventDate = sdf.parse(eventDateStr + " 00:00"); // Convert to Date object (append time if not included)
                        Date currentDate = new Date();

                        // Calculate the difference in milliseconds
                        long diffInMillis = eventDate.getTime() - currentDate.getTime();
                        long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis);

                        if (diffInHours <= 12) {
                            // Less than half a day remaining: proceed to move entrants

                            ArrayList<Map<String, Object>> invitedList = (ArrayList<Map<String, Object>>) eventDoc.get("selected");
                            ArrayList<Map<String, Object>> canceledList = (ArrayList<Map<String, Object>>) eventDoc.get("cancelled");
                            ArrayList<Map<String, Object>> invitedNotificationsList = (ArrayList<Map<String, Object>>) eventDoc.get("selectedNotificationsList");
                            ArrayList<Map<String, Object>> canceledNotificationsList = (ArrayList<Map<String, Object>>) eventDoc.get("cancelledNotificationsList");


                            if (invitedList != null && !invitedList.isEmpty()) {
                                // Move entrants from invitedList to canceledList
                                for (Map<String, Object> entrant : invitedList) {
                                    canceledList.add(entrant); // Add to canceled list
                                }
                                invitedList.clear(); // Clear the invited list
                                // Move entrants from invitedNotificationsList to canceledNotificationsList
                                for (Map<String, Object> entrant : invitedNotificationsList) {
                                    canceledNotificationsList.add(entrant); // Add to canceled list
                                }
                                invitedNotificationsList.clear(); // Clear the invited list

                                // Update Firestore
                                db.collection("events").document(eventId)
                                        .update("selectedNotificationsList", invitedNotificationsList,
                                                "cancelledNotificationsList", canceledNotificationsList)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "Entrants moved to cancelled notifications list!", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getContext(), "Error updating event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                                db.collection("events").document(eventId)
                                        .update("selectedList", invitedList,
                                                "cancelledList", canceledList)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "Entrants moved to cancelled list!", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getContext(), "Error updating event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                Toast.makeText(getContext(), "No entrants to move.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Show alert if more than 12 hours remain
                            Toast.makeText(getContext(), "Cannot cancel entrants: More than 12 hours remain until the event.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (ParseException e) {
                        Log.e("DateParsing", "Error parsing event date", e);
                    }
                } else {
                    Log.e("Firestore", "Error fetching event document", task.getException());
                }
            });
        });



        // Listen for real-time updates to the event document
        db.collection("events").document(eventId).addSnapshotListener((eventDoc, error) -> {
            if (error != null) {
                Log.e("Firestore", "Error listening to event document updates", error);
                return;
            }

            if (eventDoc != null && eventDoc.exists()) {
                ArrayList<String> deviceIds = (ArrayList<String>) eventDoc.get("selected");

                if (deviceIds != null && !deviceIds.isEmpty()) {
                    profileList.clear(); // Clear the list before adding new data

                    // Listen for real-time updates to each profile document
                    for (String deviceId : deviceIds) {
                        db.collection("profiles").document(deviceId).addSnapshotListener((profileDoc, profileError) -> {
                            if (profileError != null) {
                                Log.e("Firestore", "Error listening to profile document updates", profileError);
                                return;
                            }

                            if (profileDoc != null && profileDoc.exists()) {
                                Profile profile = profileDoc.toObject(Profile.class);
                                profileList.add(profile); // Add the name if it’s not already in the list
                                adapter.notifyDataSetChanged(); // Update the adapter
                            }
                            else {
                                Log.d("Firestore", "Profile document does not exist for device ID: " + deviceId);
                            }
                        });
                    }
                } else {
                    Log.d("Firestore", "No device IDs found in the waitlisted list.");
                    profileList.clear();
                    adapter.notifyDataSetChanged(); // Clear the ListView if no device IDs are found
                }
            } else {
                Log.d("Firestore", "Event document does not exist for ID: " + eventId);
                profileList.clear();
                adapter.notifyDataSetChanged(); // Clear the ListView if the event document doesn't exist
            }
        });

        return view;
    }

}
