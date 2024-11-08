package com.example.slacks_lottoevent;

import static com.google.firebase.firestore.util.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import android.provider.Settings;

import androidx.test.core.app.ActivityScenario;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.robolectric.RobolectricTestRunner;

public class ProfileUnitTest {
    private Profile profile;
    private SignUpActivity signUpActivity;
    private FirebaseFirestore db;

    @BeforeEach
    public void mockProfile(){
        profile = new Profile("Tate McRae", "7804448883","tateMcRae@gmail.com");
//        ActivityScenario<SignUpActivity> scenario = ActivityScenario.launch(SignUpActivity.class);
//        scenario.onActivity(a -> signUpActivity = a);
//
//        // Initialize Firestore with emulator settings for testing
//        db = FirebaseFirestore.getInstance();
//        db.useEmulator("10.0.2.2", 8080); // Adjust IP if needed
//        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                .setPersistenceEnabled(false)
//                .build();
//        db.setFirestoreSettings(settings);
    }

    public void testConstructor() {
        assertEquals("Tate McRae", profile.getName(), "Name matches constructor input");
        assertEquals("7804448883", profile.getPhone(), "Phone matches constructor input");
        assertEquals("tateMcRae@gmail.com", profile.getEmail(), "Email matches constructor input");
    }

    @Test
    public void testSetName() {
        profile.setName("Taylor Swift");
        assertEquals("Taylor Swift", profile.getName(), "Name updates to new value");
    }

    @Test
    public void testSetPhone() {
        profile.setPhone("5878895544");
        assertEquals("5878895544", profile.getPhone(), "Phone updates to new value");
    }

    @Test
    public void testSetEmail() {
        profile.setEmail("taylorSwift@ualberta.ca");
        assertEquals("taylorSwift@ualberta.ca", profile.getEmail(), "Email updates to new value");
    }

//    @Test
//    @RunWith(RobolectricTestRunner.class)
//    public void testAddUserFirebase() {
//        // Set up sample data
//        signUpActivity.nameInput.setText("Tate McRae");
//        signUpActivity.emailInput.setText("tateMcRae@gmail.com");
//        signUpActivity.phoneInput.setText("7804448883");
//        String userId = Settings.Secure.getString(signUpActivity.getContentResolver(), Settings.Secure.ANDROID_ID);
//
//        // Save profile to Firestore
//        signUpActivity.saveUserInfoToFirebase();
//
//        // Verify successful addition using Firestore listener
//        signUpActivity.usersRef.document(userId).get().addOnCompleteListener(task -> {
//            assertTrue(task.isSuccessful(), "Firestore addition should succeed");
//            DocumentSnapshot document = task.getResult();
//            if (document.exists()) {
//                assertEquals("Tate McRae", document.getString("name"));
//                assertEquals("7804448883", document.getString("phone"));
//                assertEquals("tateMcRae@gmail.com", document.getString("email"));
//            } else {
//                fail("Document should exist after addition in Firestore");
//            }
//        });
//    }

}
