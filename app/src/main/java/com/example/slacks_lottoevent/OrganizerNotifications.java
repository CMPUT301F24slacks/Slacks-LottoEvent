package com.example.slacks_lottoevent;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;

/**
 * OrganizerNotifications.java
 * This class is used to display the notifications for the organizer.
 * The organizer can view the waitlist, invited, cancelled, and enrolled users for their event.
 * The organizer can also view the details of the users in each category.
 */
public class OrganizerNotifications extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventRef;
    private Event event;
    private FrameLayout frameLayout;
    private TabLayout tabLayout;

    /**
     * This method is called when the activity is first created.
     * It sets up the tab layout for the organizer to view the waitlist, invited, cancelled, and enrolled users.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications_organizer);

        //Set Up ArrayAdapter, do getUsername() for every user in said category, change it in the case-by-case tabLayout system
        Intent intent = getIntent();
        String eventID = intent.getStringExtra("eventID");
        ImageView mapBtn = findViewById(R.id.imageView_geolocation);
        // Get the current event's id from the intent
        // query the database for the event
        db = FirebaseFirestore.getInstance();
        eventRef = db.collection("events");
        eventRef.document(eventID).get().addOnCompleteListener(eventTask -> {
            if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                DocumentSnapshot eventDoc = eventTask.getResult();
                if (eventDoc.exists()) {
                    event = eventDoc.toObject(Event.class);
                    if (!event.getgeoLocation()){
                        mapBtn.setVisibility(View.GONE);
                    }
                    event.setFinalists((ArrayList<String>) eventDoc.get("finalists"));
                    event.setWaitlisted((ArrayList<String>) eventDoc.get("waitlisted"));
                    event.setSelected((ArrayList<String>) eventDoc.get("selected"));
                    event.setCancelled((ArrayList<String>) eventDoc.get("cancelled"));
                    event.setReselected((ArrayList<String>) eventDoc.get("reselected"));
                    event.setSelectedNotificationsList((ArrayList<String>) eventDoc.get("selectedNotificationsList"));
                } else {
                    // Go back to the last thing in the stack
                    onBackPressed();
                }
            } else {
                // Go back to the last thing in the stack
                onBackPressed();
            }
        });


        Button reSelect = findViewById(R.id.reselectButton);
        frameLayout = (FrameLayout) findViewById(R.id.FrameLayout);
        tabLayout = (TabLayout) findViewById(R.id.tab_Layout);

        reSelect.setOnClickListener(v -> {
            // Call a method to perform the re-selection logic

            if(event.getEventSlots() == event.getFinalists().size()){
                updateUninvitedFinalEntrants(event);
                updateUninvitedonEvents(event);
                new AlertDialog.Builder(this)
                        .setTitle("Cannot Re-Select")
                        .setMessage("Event slots are full")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();

                reSelect.setVisibility(View.GONE);
                return;

            }

            if (event.getWaitlisted().size() == 0){
                new AlertDialog.Builder(this)
                        .setTitle("Cannot Re-Select.")
                        .setMessage("There is no one who wants to be reselected.")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();
                return;
            }

            if (!event.getEntrantsChosen()) {
                new AlertDialog.Builder(this)
                        .setTitle("Cannot Re-Select")
                        .setMessage("Need to sample entrants first.")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();
                return;
            }

            if (event.getSelected().size() + event.getFinalists().size() == event.getEventSlots()){
                new AlertDialog.Builder(this)
                        .setTitle("Cannot Re-Select")
                        .setMessage("No Space right now.")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();
                return;
            }

//            everything worked
            if(event.getEntrantsChosen() && event.getWaitlisted().size() > 0 && event.getEventSlots() != event.getFinalists().size() && event.getSelected().size() + event.getFinalists().size() != event.getEventSlots() ){
                handleReSelect();
                new AlertDialog.Builder(this)
                        .setTitle("Entrants Selected")
                        .setMessage("Entrants were selected for the event.")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent geolocationMapsActivityIntent = new Intent(getApplicationContext(), GeolocationMapsActivity.class);
                geolocationMapsActivityIntent.putExtra("eventID",eventID);
                startActivity(geolocationMapsActivityIntent);
            }
        });



        getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayout, OrganizerWaitlistFragment.newInstance(eventID))
                .addToBackStack(null)
                .commit();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selected_fragment = null;
                switch (tab.getPosition()){
                    case 0:
                        selected_fragment = OrganizerWaitlistFragment.newInstance(event);
                        reSelect.setVisibility(View.VISIBLE);

                        break;
                    case 1:
                        selected_fragment = OrganizerInvitedFragment.newInstance(event);
                        reSelect.setVisibility(View.GONE);
                        break;
                    case 2:
                        selected_fragment = OrganizerCancelledFragment.newInstance(event);
                        reSelect.setVisibility(View.GONE);
                        break;
                    case 3:
                        selected_fragment = OrganizerEnrolledFragment.newInstance(event);
                        reSelect.setVisibility(View.GONE);
                        break;
                }
                if (selected_fragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.FrameLayout, selected_fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }
            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
//                // Change the background color of the tab to white
//                tab.view.setBackgroundColor(Color.WHITE);
//
//                // Change the text color of the tab to pink
//                TextView tabTextView = (TextView) ((LinearLayout) tab.view).getChildAt(1);
//                if (tabTextView != null) {
//                    tabTextView.setTextColor(Color.parseColor("#FFC0CB")); // Pink color
//                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        ImageView back = findViewById(R.id.back_button);
        back.setOnClickListener(v -> {
            onBackPressed();
            onBackPressed();
        });
    }

    private void handleReSelect() {

        event.reSelecting();
        updateSelectedEntrants(event); // in the event
        updateInvitedEntrants(event); // in entrant
        updateUninvitedEntrants(event); // in entrant
    }

    private void updateSelectedEntrants(Event event) {
        db.collection("events").whereEqualTo("eventID", event.getEventID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                DocumentReference eventRef = db.collection("events").document(document.getId());

                for (String entrant : event.getSelected()) {
                    eventRef.update("selected", FieldValue.arrayUnion(entrant),
                            "selectedNotificationsList", FieldValue.arrayUnion(entrant)); //TODO: when doing notifications get rid of this line
                }

                eventRef.update("waitlisted", event.getWaitlisted());
                eventRef.update("entrantsChosen", event.getEntrantsChosen());
            }
        });
    }

    private void updateInvitedEntrants(Event event){
        for(String entrant: event.getSelected()) {
            DocumentReference entrantsRef = db.collection("entrants").document(entrant);
            entrantsRef.get().addOnSuccessListener(entrantDoc -> {
                if (entrantDoc.exists()) {
                    entrantsRef.update("invitedEvents", FieldValue.arrayUnion(event.getEventID()),
                                    "waitlistedEvents", FieldValue.arrayRemove(event.getEventID()))
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event added for entrant"))
                            .addOnFailureListener(e -> Log.e("Firestore", "Error updating invitedEvents for entrant"));
                }
            });
        }
    }

    //    Updating everyone who did not get selected and who do not want too be reselected
    private void updateUninvitedEntrants(Event event){
        for(String entrant: event.getWaitlisted()) {
            DocumentReference entrantsRef = db.collection("entrants").document(entrant);
            entrantsRef.get().addOnSuccessListener(entrantDoc -> {
                if (entrantDoc.exists()) {
                    // Send notification but don't put them into the uninvited unless its the due date
//                    TODO: Sent notification if DID NOT get selected the nth time
                }
            });
        }
    }

//    Updating people who are still on waitlisted (wanted to get selected but the event is now full)
    private void updateUninvitedFinalEntrants(Event event){
        for(String entrant: event.getWaitlisted()) {
            DocumentReference entrantsRef = db.collection("entrants").document(entrant);
            entrantsRef.get().addOnSuccessListener(entrantDoc -> {
                if (entrantDoc.exists()) {
                    entrantsRef.update("uninvitedEvents", FieldValue.arrayUnion(event.getEventID()),
                                    "waitlistedEvents", FieldValue.arrayRemove(event.getEventID()))
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event added for entrant"))
                            .addOnFailureListener(e -> Log.e("Firestore", "Error updating invitedEvents for entrant"));
//                    ADD notifications
                }
            });
        }
    }

//    Updating event waitlist when full and the entrants cannot be select again as it is full
    private void updateUninvitedonEvents(Event event) {
        db.collection("events").whereEqualTo("eventID", event.getEventID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                DocumentReference eventRef = db.collection("events").document(document.getId());

                for (String entrant : event.getWaitlisted()) {
                    eventRef.update("waitlisted", FieldValue.arrayRemove(entrant),
                            "waitlistedNotificationsList", FieldValue.arrayRemove(entrant)); //TODO: when doing notifications get rid of this line
                }
                event.fullEvent(); // clearing waitlist now
                eventRef.update("waitlisted", event.getWaitlisted());
            }
        });
    }
}
