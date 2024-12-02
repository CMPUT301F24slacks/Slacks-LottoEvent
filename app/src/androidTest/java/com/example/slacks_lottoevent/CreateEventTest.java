package com.example.slacks_lottoevent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import com.example.slacks_lottoevent.model.User;
import com.example.slacks_lottoevent.view.CreateEventActivity;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class CreateEventTest {

    @Rule
    public ActivityScenarioRule<CreateEventActivity> activityRule = new ActivityScenarioRule<>(CreateEventActivity.class);

    /**
     * Test the create event button.
     */
    @Test
    public void testDateValidation() {
        onView(withId(R.id.event_date)).perform(replaceText("13/01/2025"));
        onView(withId(R.id.event_date)).check(matches(ViewMatchers.hasErrorText("Date must be in MM/DD/YY format")));

        onView(withId(R.id.event_date)).perform(replaceText("01/13/2025"));
        onView(withId(R.id.event_date)).check(matches(Matchers.not(ViewMatchers.hasErrorText("Date must be in MM/DD/YY format"))));
    }

    /**
     * Test the create event button.
     */
    @Test
    public void testTimeValidation() {
        onView(withId(R.id.event_time)).perform(replaceText("25:00"));
        onView(withId(R.id.event_time)).check(matches(ViewMatchers.hasErrorText("Time must be in hh:mm format")));

        onView(withId(R.id.event_time)).perform(replaceText("15:30"));
        onView(withId(R.id.event_time)).check(matches(Matchers.not(ViewMatchers.hasErrorText("Time must be in hh:mm format"))));
    }

    /**
     * Test the create event button.
     */
    @Test
    public void testWaitListCapacityValidation() {
        onView(withId(R.id.eventSlots)).perform(replaceText("5"));
        onView(withId(R.id.waitListCapacity)).perform(replaceText("3"));
        onView(withId(R.id.waitListCapacity)).check(matches(ViewMatchers.hasErrorText("Waitlist capacity must be greater than the event slots")));

        onView(withId(R.id.waitListCapacity)).perform(replaceText("10"));
        onView(withId(R.id.waitListCapacity)).check(matches(Matchers.not(ViewMatchers.hasErrorText("Waitlist capacity must be greater than the event slots"))));
    }
}