package com.example.slacks_lottoevent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProfileUnitTest {
    private Profile profile;

    @BeforeEach
    public void setup() {
        // Initialize Profile without Context
        profile = new Profile("Tate McRae", "7804448883", "tateMcRae@gmail.com", null);
    }

    @Test
    public void testSetName() {
        profile.setName("Taylor Swift", null);
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
}
