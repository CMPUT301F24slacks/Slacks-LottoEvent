package com.example.slacks_lottoevent;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;

import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class EventsTabTest {

    @Rule
    public ActivityScenarioRule<EventsHomeActivity> activityRule =
            new ActivityScenarioRule<>(EventsHomeActivity.class);

    /**
     * Test the My Events tab.
     */
    @Test
    public void testMyEventsTabSelected() {
        // Simulate clicking on "My Events" tab
        onView(withText("My Events")).perform(click());

        // Check that HomeFragment is displayed and the QR Code scanner button is visible
        onView(withId(R.id.qr_code_scanner_FAB)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)));
        onView(withId(R.id.create_event_FAB)).check(matches(withEffectiveVisibility(Visibility.GONE)));
    }

    /**
     * Test the Manage My Events tab.
     */
    @Test
    public void testManageMyEventsTabSelected() {
        // Simulate clicking on "Manage My Events" tab
        onView(withText("Manage My Events")).perform(click());

        // Check that ManageMyEventsFragment is displayed and the Create Event button is visible
        onView(withId(R.id.qr_code_scanner_FAB)).check(matches(withEffectiveVisibility(Visibility.GONE)));
        onView(withId(R.id.create_event_FAB)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)));
    }

}
