package com.example.slacks_lottoevent;

import static android.app.PendingIntent.getActivity;
import static androidx.test.InstrumentationRegistry.getContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.fragment.app.FragmentTransaction;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.slacks_lottoevent.R;
import com.example.slacks_lottoevent.model.Facility;
import com.example.slacks_lottoevent.view.SignUpActivity;
import com.example.slacks_lottoevent.view.fragment.AdminFacilitiesFragment;
import com.example.slacks_lottoevent.viewmodel.adapter.FacilityListArrayAdapter;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class AdminBrowseTest {
    private ArrayList<Facility> mockFacilities;
    private ArrayList<Facility> mockEvents;
    private ArrayList<Facility> mockProfiles;
    private ArrayList<Facility> mockImages;
    private ListView listViewAdminFacilities;
    private FacilityListArrayAdapter adapter;

    @Before
    public void setUp() {
        // Create mock facilities data
        mockFacilities = new ArrayList<>();
        mockFacilities.add(new Facility("Gym", "123 45 St SW T65 3G4", "1234"));
        mockFacilities.add(new Facility("Swimming Pool", "Butterdome Ave 4040 90th Ave SW T5R 4F5", "1234"));
        mockFacilities.add(new Facility("Library", "Macewan University 106 Ave TH6 4R5", "1234"));
    }

    @Test
    public void testFacilitiesDisplayed() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_admin_facilities, null);

        onView(withId(R.id.ListViewAdminFacilities)).check(matches(isDisplayed()));

        // Create the events adapter (even though it's not used here for testing)
//        ArrayList<Event> eventList = new ArrayList<>();
//        OrganizerEventArrayAdapter eventsAdapter = new OrganizerEventArrayAdapter(getContext(), eventList, true);

        adapter = new FacilityListArrayAdapter(getContext(), mockFacilities, true, null, null);

        onView(withId(R.id.ListViewAdminFacilities)).perform(ViewActions.scrollTo());
        listViewAdminFacilities.setAdapter(adapter);


        // Verify each mock facility is displayed in the ListView
        for (Facility facility : mockFacilities) {
            onView(withText(facility.getFacilityName())).check(matches(isDisplayed())); // Check each facility's name is displayed
        }
    }






}
