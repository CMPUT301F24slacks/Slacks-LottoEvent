package com.example.slacks_lottoevent;

import android.content.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProfileUnitTest {
    private Profile profile;
    private Context mockContext;

    @BeforeEach
    public void setup() {
        // Mock the Android Context
        mockContext = Mockito.mock(Context.class);

        // Mock file directory behavior
        Mockito.when(mockContext.getFilesDir()).thenReturn(new java.io.File("mock_directory"));

        // Initialize Profile with mocked context
        profile = new Profile("Tate McRae", "7804448883", "tateMcRae@gmail.com", mockContext);
    }

    @Test
    public void testSetName() {
        profile.setName("Taylor Swift", mockContext);
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
