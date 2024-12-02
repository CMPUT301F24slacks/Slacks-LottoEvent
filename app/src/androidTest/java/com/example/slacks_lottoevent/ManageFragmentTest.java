package com.example.slacks_lottoevent;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiSelector;

import com.example.slacks_lottoevent.view.MainActivity;
import com.google.android.material.tabs.TabLayout;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ManageFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    private void handleNotificationPermissionDialog() {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        try {
            // Look for the "Allow" button and click it
            device.findObject(new UiSelector().text("Allow")).click();
        } catch (Exception e) {
            // If the dialog is not displayed, ignore the exception
        }
    }


    @Test
    public void testDefaultTabDisplayed() {
        handleNotificationPermissionDialog();
        onView(withId(R.id.nav_manage)).perform(click());

        // Verify "Events" tab is displayed by default
        onView(withText("Events"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testSwitchToFacilityTab() {
        onView(withId(R.id.nav_manage)).perform(click());

        // Switch to "Facility" tab
        onView(withId(R.id.manage_tab_layout))
                .perform(selectTabWithText("Facility"));

        // Verify "Facility" content is displayed
        onView(withText("Facility"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testSwitchBackToEventsTab() {
        onView(withId(R.id.nav_manage)).perform(click());

        // Switch to "Facility" tab
        onView(withId(R.id.manage_tab_layout))
                .perform(selectTabWithText("Facility"));

        // Switch back to "Events" tab
        onView(withId(R.id.manage_tab_layout))
                .perform(selectTabWithText("Events"));

        // Verify "Events" content is displayed again
        onView(withText("Events"))
                .check(matches(isDisplayed()));
    }


    // Helper method for selecting tabs by text
    private ViewAction selectTabWithText(String tabName) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TabLayout.class);
            }

            @Override
            public String getDescription() {
                return "Select tab with text: " + tabName;
            }

            @Override
            public void perform(UiController uiController, View view) {
                TabLayout tabLayout = (TabLayout) view;
                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    TabLayout.Tab tab = tabLayout.getTabAt(i);
                    if (tab != null && tab.getText() != null && tab.getText().equals(tabName)) {
                        tab.select();
                        break;
                    }
                }
            }
        };
    }
}
