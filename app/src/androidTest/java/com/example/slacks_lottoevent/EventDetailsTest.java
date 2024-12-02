package com.example.slacks_lottoevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiSelector;

import com.example.slacks_lottoevent.view.JoinEventDetailsActivity;
import com.example.slacks_lottoevent.view.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EventDetailsTest {
    private static final String MOCK_QR_CODE = "45af9327-c42d-43c1-a89e-c8a16e625795";
    private void handleNotificationPermissionDialog() {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        try {
            // Look for the "Allow" button and click it
            device.findObject(new UiSelector().text("Allow")).click();
        } catch (Exception e) {
            // If the dialog is not displayed, ignore the exception
        }
    }

    @Before
    public void setUp() {
        // Create an intent with the fake QR code
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), JoinEventDetailsActivity.class);
        intent.putExtra("qrCodeValue", MOCK_QR_CODE);
        Intents.init();
        ActivityScenario.launch(intent);
    }

    @Test
    public void testActivityLoadsWithQRCode() {

        onView(withId(R.id.eventTitle)).check(matches(isDisplayed()));

    }

    @Test
    public void testBackButtonNavigatesToHome() {

        onView(withId(R.id.event_details_back_button)).perform(click());
        handleNotificationPermissionDialog();
        intended(hasComponent(MainActivity.class.getName()));
    }
    @After
    public void tearDown() {
        Intents.release();
    }

}
