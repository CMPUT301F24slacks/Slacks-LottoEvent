package com.example.slacks_lottoevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiSelector;


import com.example.slacks_lottoevent.view.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class BottomNavBarTest {

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
    public void testBottomNavigationView_isDisplayed() {
        // Handle the notification permission dialog
        handleNotificationPermissionDialog();

        // Check if the BottomNavigationView is displayed
        onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()));
    }

    @Test
    public void testBottomNavigationView_navigateToHome() {
        // Handle the notification permission dialog
        handleNotificationPermissionDialog();

        // Click on Home menu item
        onView(withId(R.id.nav_home)).perform(click());

        // Verify that the NavController is on the correct destination
        activityScenarioRule.getScenario().onActivity(activity -> {
            NavController navController = Navigation.findNavController(activity.findViewById(R.id.nav_host_main));
            assertThat(navController.getCurrentDestination().getId(), is(R.id.homeFragment));
        });
    }


    @Test
    public void testBottomNavigationView_navigateToManage() {
        // Handle the notification permission dialog
        handleNotificationPermissionDialog();

        // Click on Manage menu item
        onView(withId(R.id.nav_manage)).perform(click());

        // Verify that the NavController is on the correct destination
        activityScenarioRule.getScenario().onActivity(activity -> {
            NavController navController = Navigation.findNavController(activity.findViewById(R.id.nav_host_main));
            assertThat(navController.getCurrentDestination().getId(), is(R.id.manageFragment));
        });
    }

    @Test
    public void testBottomNavigationView_navigateToInbox() {
        // Handle the notification permission dialog
        handleNotificationPermissionDialog();

        // Click on Inbox menu item
        onView(withId(R.id.nav_inbox)).perform(click());

        // Verify that the NavController is on the correct destination
        activityScenarioRule.getScenario().onActivity(activity -> {
            NavController navController = Navigation.findNavController(activity.findViewById(R.id.nav_host_main));
            assertThat(navController.getCurrentDestination().getId(), is(R.id.invitesFragment));
        });
    }

    @Test
    public void testBottomNavigationView_navigateToProfile() {
        // Handle the notification permission dialog
        handleNotificationPermissionDialog();

        // Click on Profile menu item
        onView(withId(R.id.nav_profile)).perform(click());

        // Verify that the NavController is on the correct destination
        activityScenarioRule.getScenario().onActivity(activity -> {
            NavController navController = Navigation.findNavController(activity.findViewById(R.id.nav_host_main));
            assertThat(navController.getCurrentDestination().getId(), is(R.id.profileFragment));
        });
    }
}
