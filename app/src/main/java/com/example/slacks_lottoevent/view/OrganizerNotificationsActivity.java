package com.example.slacks_lottoevent.view;

import static com.example.slacks_lottoevent.view.AdminActivity.showAdminAlertDialog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.slacks_lottoevent.R;
import com.example.slacks_lottoevent.model.Event;
import com.example.slacks_lottoevent.view.fragment.OrganizerCancelledFragment;
import com.example.slacks_lottoevent.view.fragment.OrganizerEnrolledFragment;
import com.example.slacks_lottoevent.view.fragment.OrganizerInvitedFragment;
import com.example.slacks_lottoevent.view.fragment.OrganizerWaitlistFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * OrganizerNotificationsActivity.java
 * This class is used to display the notifications for the organizer.
 * The organizer can view the waitlist, invited, cancelled, and enrolled users for their event.
 * The organizer can also view the details of the users in each category.
 */
public class OrganizerNotificationsActivity extends AppCompatActivity {

    Button reSelect;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventRef;
    private Event event;
    private FrameLayout frameLayout;
    private TabLayout tabLayout;

    /**
     * This method is called when the activity is first created.
     * It sets up the tab layout for the organizer to view the waitlist, invited, cancelled, and enrolled users.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications_organizer);
        reSelect = findViewById(R.id.reselectButton);

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
                    if (!event.getgeoLocation()) {
                        mapBtn.setVisibility(View.GONE);
                    }
                    event.setFinalists((ArrayList<String>) eventDoc.get("finalists"));
                    event.setWaitlisted((ArrayList<String>) eventDoc.get("waitlisted"));
                    event.setSelected((ArrayList<String>) eventDoc.get("selected"));
                    event.setCancelled((ArrayList<String>) eventDoc.get("cancelled"));
                    event.setReselected((ArrayList<String>) eventDoc.get("reselected"));
                    event.setSelectedNotificationsList(
                            (ArrayList<String>) eventDoc.get("selectedNotificationsList"));

                    updateReselectButtonVisibility();
                } else {
                    // Go back to the last thing in the stack
                    onBackPressed();
                }
            } else {
                // Go back to the last thing in the stack
                onBackPressed();
            }
        });

        frameLayout = findViewById(R.id.FrameLayout);
        tabLayout = findViewById(R.id.tab_Layout);

        reSelect.setOnClickListener(v -> {
            // Call a method to perform the re-selection logic

            if (event.getEventSlots() == event.getFinalists().size()) {

//              Event has closed - updated final waitlisted entrants (remove and put too cancel)
                updateUninvitedFinalEntrants(event);
                updateUninvitedonEvents(event);
                showAdminAlertDialog(this, null, "Cannot Re-Select", "Event slots are full",
                                     null, null, "OK", null);
                return;

            }

            if (event.getWaitlisted().size() == 0) {

                showAdminAlertDialog(this, null, "Cannot Re-Select",
                                     "There is no one who wants to be reselected.", null, null,
                                     "OK", null);
                return;
            }

            if (!event.getEntrantsChosen()) {
                showAdminAlertDialog(this, null, "Cannot Re-Select",
                                     "Need to sample entrants first.", null, null, "OK", null);
                return;
            }

            if (event.getSelected().size() + event.getFinalists().size() == event.getEventSlots()) {
                showAdminAlertDialog(this, null, "Cannot Re-Select",
                                     "No Space right now. Still waiting on responses.", null, null,
                                     "OK", null);
                return;
            }

//            everything worked
            if (event.getEntrantsChosen() && event.getWaitlisted().size() > 0 &&
                event.getEventSlots() != event.getFinalists().size() &&
                event.getSelected().size() + event.getFinalists().size() != event.getEventSlots()) {
                handleReSelect();
                showAdminAlertDialog(this, null, "Entrants Selected",
                                     "Entrants were selected for the event.", null, null, "OK",
                                     null);
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent geolocationMapsActivityIntent = new Intent(getApplicationContext(),
                                                                  GeolocationMapsActivity.class);
                geolocationMapsActivityIntent.putExtra("eventID", eventID);
                startActivity(geolocationMapsActivityIntent);
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayout,
                                                               OrganizerWaitlistFragment.newInstance(
                                                                       eventID))
                                   .addToBackStack(null)
                                   .commit();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selected_fragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        selected_fragment = OrganizerWaitlistFragment.newInstance(
                                event.getEventID());
                        updateReselectButtonVisibility();
                        break;
                    case 1:
                        selected_fragment = OrganizerInvitedFragment.newInstance(
                                event.getEventID());
                        break;
                    case 2:
                        selected_fragment = OrganizerCancelledFragment.newInstance(
                                event.getEventID());
                        break;
                    case 3:
                        selected_fragment = OrganizerEnrolledFragment.newInstance(
                                event.getEventID());
                        break;
                }
                if (selected_fragment != null) {
                    getSupportFragmentManager().beginTransaction()
                                               .replace(R.id.FrameLayout, selected_fragment)
                                               .setTransition(
                                                       FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
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

    /**
     * Handles the re-selecting logic.
     */
    private void handleReSelect() {

        event.reSelecting();
        updateSelectedEntrants(event); // in the event - update the waitlist this way
        updateInvitedEntrants(event); // in entrant

        updateReselectButtonVisibility();
    }

    /**
     * Handles updating the entrants who are invited for the event.
     */
    private void updateSelectedEntrants(Event event) {
        db.collection("events").whereEqualTo("eventID", event.getEventID()).get()
          .addOnCompleteListener(task -> {
              if (task.isSuccessful() && !task.getResult().isEmpty()) {
                  DocumentSnapshot document = task.getResult().getDocuments().get(0);
                  DocumentReference eventRef = db.collection("events").document(document.getId());

                  for (String entrant : event.getSelected()) {
                      eventRef.update("selected", FieldValue.arrayUnion(entrant),
                                      "selectedNotificationsList", FieldValue.arrayUnion(entrant),
                                      "waitlistedNotificationsList",
                                      FieldValue.arrayRemove(entrant));
                  }

                  eventRef.update("waitlisted", event.getWaitlisted());
                  eventRef.update("entrantsChosen", event.getEntrantsChosen());
              }
          });
    }

    /**
     * Handles updating the entrants who are invited.
     */
    private void updateInvitedEntrants(Event event) {
        for (String entrant : event.getSelected()) {
            DocumentReference entrantsRef = db.collection("entrants").document(entrant);
            entrantsRef.get().addOnSuccessListener(entrantDoc -> {
                if (entrantDoc.exists()) {
                    entrantsRef.update("invitedEvents", FieldValue.arrayUnion(event.getEventID()),
                                       "waitlistedEvents",
                                       FieldValue.arrayRemove(event.getEventID()))
                               .addOnSuccessListener(
                                       aVoid -> Log.d("Firestore", "Event added for entrant"))
                               .addOnFailureListener(e -> Log.e("Firestore",
                                                                "Error updating invitedEvents for entrant"));
                }
            });
        }
    }

    /**
     * Handles updating the entrants who are not invited.
     */
    private void updateUninvitedFinalEntrants(Event event) {
        for (String entrant : event.getWaitlisted()) {
            DocumentReference entrantsRef = db.collection("entrants").document(entrant);
            entrantsRef.get().addOnSuccessListener(entrantDoc -> {
                if (entrantDoc.exists()) {
                    entrantsRef.update("uninvitedEvents", FieldValue.arrayUnion(event.getEventID()),
                                       "waitlistedEvents",
                                       FieldValue.arrayRemove(event.getEventID()))
                               .addOnSuccessListener(
                                       aVoid -> Log.d("Firestore", "Event added for entrant"))
                               .addOnFailureListener(e -> Log.e("Firestore",
                                                                "Error updating invitedEvents for entrant"));
                }
            });
        }
    }

    /**
     * Handles the re-selection logic for entrants in the event.
     */
    private void updateUninvitedonEvents(Event event) {
        db.collection("events").whereEqualTo("eventID", event.getEventID()).get()
          .addOnCompleteListener(task -> {
              if (task.isSuccessful() && !task.getResult().isEmpty()) {
                  DocumentSnapshot document = task.getResult().getDocuments().get(0);
                  DocumentReference eventRef = db.collection("events").document(document.getId());

                  for (String entrant : event.getWaitlisted()) {
                      eventRef.update("waitlisted", FieldValue.arrayRemove(entrant),
                                      "waitlistedNotificationsList",
                                      FieldValue.arrayRemove(entrant),
                                      "cancelled", FieldValue.arrayUnion(entrant),
                                      "cancelledNotificationsList", FieldValue.arrayUnion(entrant));
                  }
                  event.fullEvent(); // clearing waitlist now
                  eventRef.update("waitlisted", event.getWaitlisted());
                  eventRef.update("reselected", event.getWaitlisted());
              }
          });
    }

    /**
     * This method is called to update the reselect button so the organizer knows when they can or can't reselect.
     * It makes sure all the organizer cannot reselect given the conditions.
     */
    private void updateReselectButtonVisibility() {
        if (event == null) {
            reSelect.setVisibility(View.GONE);
            return;
        }

        boolean canReselect = event.getWaitlisted().size() > 0
                              && event.getEntrantsChosen()
                              && event.getFinalists().size() < event.getEventSlots()
                              && (event.getSelected().size() + event.getFinalists().size() <
                                  event.getEventSlots());

        boolean finalistsFull = event.getFinalists().size() == event.getEventSlots();
        boolean waitlistEmpty = event.getWaitlisted().size() > 0;

        reSelect.setVisibility((finalistsFull && waitlistEmpty) || canReselect ? View.VISIBLE : View.GONE);
    }

}
