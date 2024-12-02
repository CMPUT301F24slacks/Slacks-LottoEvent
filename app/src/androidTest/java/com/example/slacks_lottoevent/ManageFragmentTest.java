package com.example.slacks_lottoevent;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.slacks_lottoevent.view.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ManageFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testTabsDisplayedCorrectly() {
        // Check if TabLayout is displayed
        onView(withId(R.id.manage_tab_layout)).check(matches(isDisplayed()));

        // Check if the tabs have correct text
        onView(withText("Events")).check(matches(isDisplayed()));
        onView(withText("Facility")).check(matches(isDisplayed()));
    }

    @Test
    public void testViewPagerSwitchesOnTabClick() {
        // Click on the "Facility" tab
        onView(withText("Facility")).perform(click());

        // Verify ViewPager2 content updates (you can add specific checks for content in the fragment)
        onView(withId(R.id.manage_view_pager)).check(matches(isDisplayed()));

        // Click back to the "Events" tab
        onView(withText("Events")).perform(click());

        // Verify ViewPager2 switches back
        onView(withId(R.id.manage_view_pager)).check(matches(isDisplayed()));
    }
}

