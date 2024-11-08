package com.example.slacks_lottoevent;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.action.ViewActions;

import com.example.slacks_lottoevent.R;
import android.view.View;


import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SignUpActivityTest {

    @Rule
    public ActivityScenarioRule<SignUpActivity> activityRule = new ActivityScenarioRule<>(SignUpActivity.class);

    /**
     * Clear SharedPreferences before each test.
     */
    @Before
    public void clearSharedPreferences() {
        // Clear SharedPreferences before each test
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("SlacksLottoEventUserInfo", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    /**
     * Test the sign-up button with valid input.
     */
    @Test
    public void testSuccessfulSignUp() {
        // Input valid name, email, and phone number
        onView(withId(R.id.name_input)).perform(typeText("Tate McRae"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.email_input)).perform(typeText("tateMcRae@gmail.com"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.phone_input)).perform(typeText("5874461234"), ViewActions.closeSoftKeyboard());

        // Click on the sign-up button
        onView(withId(R.id.sign_up_button)).perform(click());

        // Verify if data is saved in SharedPreferences
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("SlacksLottoEventUserInfo", Context.MODE_PRIVATE);
        assertEquals("Tate McRae", sharedPreferences.getString("userName", null));
        assertEquals("tateMcRae@gmail.com", sharedPreferences.getString("userEmail", null));
        assertEquals("5874461234", sharedPreferences.getString("userPhone", null));
        assertEquals(true, sharedPreferences.getBoolean("isSignedUp", false));
    }

    /**
     * Test the sign-up button with an empty name field.
     */
    @Test
    public void testEmptyNameField() {
        // Leave the name field empty, enter valid email and phone number
        onView(withId(R.id.email_input)).perform(typeText("tateMcRae@gmail.com"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.phone_input)).perform(typeText("5874461234"), ViewActions.closeSoftKeyboard());

        // Click on the sign-up button
        onView(withId(R.id.sign_up_button)).perform(click());

        // Check if the error message is displayed on the name input field
        onView(withId(R.id.name_input)).check(matches(withError("Name is required")));
    }

    /**
     * Test the sign-up button with an empty email field.
     */
    @Test
    public void testInvalidEmail() {
        // Enter valid name and phone number, but an invalid email
        onView(withId(R.id.name_input)).perform(typeText("Tate McRae"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.email_input)).perform(typeText("invalid-email"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.phone_input)).perform(typeText("5874461234"), ViewActions.closeSoftKeyboard());

        // Click on the sign-up button
        onView(withId(R.id.sign_up_button)).perform(click());

        // Check if the error message is displayed on the email input field
        onView(withId(R.id.email_input)).check(matches(withError("Enter a valid email")));
    }

    /**
     * Test the sign-up button with an empty phone number field.
     */
    public static Matcher<View> withError(final String expectedError) {
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof EditText)) {
                    return false;
                }
                EditText editText = (EditText) view;
                CharSequence error = editText.getError();
                return error != null && error.toString().equals(expectedError);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with error: " + expectedError);
            }
        };
    }




}

