package com.example.slacks_lottoevent;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
                } else {
                    // Go back to the last thing in the stack
                    onBackPressed();
                }
            } else {
                // Go back to the last thing in the stack
                onBackPressed();
            }
        });

        frameLayout = (FrameLayout) findViewById(R.id.FrameLayout);
        tabLayout = (TabLayout) findViewById(R.id.tab_Layout);

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
                        selected_fragment = OrganizerWaitlistFragment.newInstance(eventID);
                        break;
                    case 1:
                        selected_fragment = OrganizerInvitedFragment.newInstance(eventID);;
                        break;
                    case 2:
                        selected_fragment = OrganizerCancelledFragment.newInstance(eventID);;
                        break;
                    case 3:
                        selected_fragment = OrganizerEnrolledFragment.newInstance(eventID);;
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
        });

    }
}
