package com.example.slacks_lottoevent;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.slacks_lottoevent.model.User;
import com.example.slacks_lottoevent.view.CreateEventActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class CreateEventTest {

    @Before
    public void setup() {
        // Obtain the application context
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Initialize the User singleton
        User.initialize(context);
    }

    @Test
    public void testNameValidation() {
        // Launch the activity manually
        try (ActivityScenario<CreateEventActivity> scenario = ActivityScenario.launch(CreateEventActivity.class)) {
            onView(withId(R.id.event_name)).perform(replaceText(""));
            onView(withId(R.id.event_name)).check(matches(isDisplayed()));
            onView(withId(R.id.event_name)).perform(replaceText("2025 RISC-V Conference"));
            onView(withText("2025 RISC-V Conference")).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testTimeValidation() {
        // Launch the activity manually
        try (ActivityScenario<CreateEventActivity> scenario = ActivityScenario.launch(CreateEventActivity.class)) {
            onView(withId(R.id.eventTime)).perform(replaceText("25:00"));
            onView(withId(R.id.eventTime)).check(matches(ViewMatchers.hasErrorText("Time must be in hh:mm format")));

            onView(withId(R.id.eventTime)).perform(replaceText("15:30"));
            onView(withId(R.id.eventTime)).check(matches(Matchers.not(ViewMatchers.hasErrorText("Time must be in hh:mm format"))));
        }
    }

    @Test
    public void testDescriptionValidation() {
        // Add test case for description validation
        try (ActivityScenario<CreateEventActivity> scenario = ActivityScenario.launch(CreateEventActivity.class)) {
            onView(withId(R.id.event_details)).perform(replaceText("Meet us at 2pm!"));
            onView(withText("Meet us at 2pm!")).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testEventSlotsValidation() {
        // Add test case for event slots validation
        try (ActivityScenario<CreateEventActivity> scenario = ActivityScenario.launch(CreateEventActivity.class)) {
            onView(withId(R.id.eventSlots)).perform(replaceText("0"));
            onView(withText("0")).check(matches(isDisplayed()));
            onView(withId(R.id.eventSlots)).perform(replaceText("5"));
            onView(withText("5")).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testWaitlistCapacityValidation() {
        // Add test case for date validation
        try (ActivityScenario<CreateEventActivity> scenario = ActivityScenario.launch(CreateEventActivity.class)) {
            onView(withId(R.id.eventSlots)).perform(replaceText("5"));
            onView(withId(R.id.waitListCapacity)).perform(replaceText("3"));
            onView(withId(R.id.waitListCapacity)).check(matches(ViewMatchers.hasErrorText("Waitlist capacity must be greater than the event slots")));

            onView(withId(R.id.waitListCapacity)).perform(replaceText("10"));
            onView(withId(R.id.waitListCapacity)).check(matches(Matchers.not(ViewMatchers.hasErrorText("Waitlist capacity must be greater than the event slots"))));
        }
    }
}
