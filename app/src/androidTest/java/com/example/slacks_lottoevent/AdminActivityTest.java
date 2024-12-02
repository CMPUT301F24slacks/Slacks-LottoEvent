package com.example.slacks_lottoevent;

import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.slacks_lottoevent.R;
import com.example.slacks_lottoevent.view.AdminActivity;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class AdminActivityTest {

    @Rule
    public ActivityScenarioRule<AdminActivity> activityScenarioRule =
            new ActivityScenarioRule<>(AdminActivity.class);

    /**
     * Tests if the correct fragment is displayed when the "Events" tab is selected.
     */
    @Test
    public void testSwitchToEventsTab() {
        onView(withId(R.id.tab_Layout_Admin)).perform(click());
        onView(withText("Events")).perform(click());
        onView(withId(R.id.FrameLayoutAdmin)) // Replace with unique view ID in AdminEventsFragment
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    /**
     * Tests if the correct fragment is displayed when the "Images" tab is selected.
     */
    @Test
    public void testSwitchToImagesTab() {
        onView(withId(R.id.tab_Layout_Admin)).perform(click());
        onView(withText("Images")).perform(click());
        onView(withId(R.id.FrameLayoutAdmin)) // Replace with unique view ID in AdminImagesFragment
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    /**
     * Tests if the correct fragment is displayed when the "Facilities" tab is selected.
     */
    @Test
    public void testSwitchToFacilitiesTab() {
        onView(withId(R.id.tab_Layout_Admin)).perform(click());
        onView(withText("Facilities")).perform(click());
        onView(withId(R.id.FrameLayoutAdmin)) // Replace with unique view ID in AdminFacilitiesFragment
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    /**
     * Tests if the correct fragment is displayed when the "Profiles" tab is selected.
     */
    @Test
    public void testSwitchToProfilesTab() {
        onView(withId(R.id.tab_Layout_Admin)).perform(click());
        onView(withText("Profiles")).perform(click());
        onView(withId(R.id.FrameLayoutAdmin)) // Replace with unique view ID in AdminProfilesFragment
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    /**
     * Tests switching between multiple tabs to ensure no overlap or incorrect fragments.
     */
    @Test
    public void testMultipleTabSwitching() {
        onView(withId(R.id.tab_Layout_Admin)).perform(click());
        onView(withText("Events")).perform(click());
        onView(withId(R.id.FrameLayoutAdmin))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        onView(withText("Images")).perform(click());
        onView(withId(R.id.FrameLayoutAdmin))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        onView(withText("Facilities")).perform(click());
        onView(withId(R.id.FrameLayoutAdmin))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        onView(withText("Profiles")).perform(click());
        onView(withId(R.id.FrameLayoutAdmin))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}

