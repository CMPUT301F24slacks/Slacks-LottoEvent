package com.example.slacks_lottoevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.slacks_lottoevent.R;
import com.example.slacks_lottoevent.view.SignUpActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SignUpActivityTest {

    @Rule
    public ActivityTestRule<SignUpActivity> activityRule =
            new ActivityTestRule<>(SignUpActivity.class, true, false);

    @Test
    public void testSignUpWithValidInputs() {
        // Launch the activity
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        // Input valid data
        onView(withId(R.id.name_input)).perform(replaceText("John Doe"));
        onView(withId(R.id.email_input)).perform(replaceText("john.doe@example.com"));
        onView(withId(R.id.phone_input)).perform(replaceText("1234567890"));

        // Click sign up button
        onView(withId(R.id.sign_up_button)).perform(click());

    }

    @Test
    public void testSignUpWithInvalidEmail() {
        // Launch the activity
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        // Input invalid email
        onView(withId(R.id.name_input)).perform(replaceText("Jane Doe"));
        onView(withId(R.id.email_input)).perform(replaceText("invalid_email"));
        onView(withId(R.id.phone_input)).perform(replaceText("1234567890"));

        // Click sign up button
        onView(withId(R.id.sign_up_button)).perform(click());

        // Verify error message on email input
        onView(withId(R.id.email_input)).check(matches(withText("Enter a valid email")));
    }

    @Test
    public void testSignUpWithEmptyName() {
        // Launch the activity
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        // Leave name empty
        onView(withId(R.id.email_input)).perform(replaceText("jane.doe@example.com"));
        onView(withId(R.id.phone_input)).perform(replaceText("1234567890"));

        // Click sign up button
        onView(withId(R.id.sign_up_button)).perform(click());

        // Verify error message on name input
        onView(withId(R.id.name_input)).check(matches(withText("Name is required")));
    }
}
