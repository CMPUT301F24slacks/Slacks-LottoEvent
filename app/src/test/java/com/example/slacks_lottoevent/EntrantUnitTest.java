package com.example.slacks_lottoevent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class EntrantUnitTest {
    private Entrant testEntrant;

    @BeforeEach
    void setUp(){
        testEntrant = new Entrant();
    }
    @Test
    void testAddAndGetWaitlistedEvents() {
        testEntrant.addWaitlistedEvents("0d7577ae-16f9-5g46-9be4-fe65e9a4e290");
        ArrayList<String> waitlistedEvents = testEntrant.getWaitlistedEvents();

        assertEquals(1, waitlistedEvents.size());
        assertTrue(waitlistedEvents.contains("0d7577ae-16f9-5g46-9be4-fe65e9a4e290"));
    }

    @Test
    void testAddAndGetFinalistEvents() {
        testEntrant.addFinalistEvents("1e2024ie-19f9-5g46-9be4-fe65e9a4e860");
        ArrayList<String> finalistEvents = testEntrant.getFinalistEvents();

        assertEquals(1, finalistEvents.size());
        assertTrue(finalistEvents.contains("1e2024ie-19f9-5g46-9be4-fe65e9a4e860"));
    }

    @Test
    void testAddAndGetInvitedEvents() {
        testEntrant.addInvitedEvents("2r2025ie-90f9-5g46-9be4-fe65e9a4e740");
        ArrayList<String> invitedEvents = testEntrant.getInvitedEvents();

        assertEquals(1, invitedEvents.size());
        assertTrue(invitedEvents.contains("2r2025ie-90f9-5g46-9be4-fe65e9a4e740"));
    }

    @Test
    void testAddAndGetUninvitedEvents() {
        testEntrant.addUninvitedEvents("7adf4a5b-b256-408a-a213-ae6f88504c08");
        ArrayList<String> uninvitedEvents = testEntrant.getUninvitedEvents();

        assertEquals(1, uninvitedEvents.size());
        assertTrue(uninvitedEvents.contains("7adf4a5b-b256-408a-a213-ae6f88504c08"));
    }

    @Test
    void testAddAndGetWaitlistedEventsNotis() {
        testEntrant.addWaitlistedEventsNotis("9c781495-f91e-4648-9bb0-c390f558db10");
        ArrayList<String> waitlistedEventsNotis = testEntrant.getWaitlistedEventsNotis();

        assertEquals(1, waitlistedEventsNotis.size());
        assertTrue(waitlistedEventsNotis.contains("9c781495-f91e-4648-9bb0-c390f558db10"));
    }

    @Test
    void testAddAndGetFinalistEventsNotis() {
        testEntrant.addFinalistEventsNotis("9g781495-f91e-4648-9bb0-c390f558db10");
        ArrayList<String> finalistEventsNotis = testEntrant.getFinalistEventsNotis();

        assertEquals(1, finalistEventsNotis.size());
        assertTrue(finalistEventsNotis.contains("9g781495-f91e-4648-9bb0-c390f558db10"));
    }

    @Test
    void testAddAndGetInvitedEventsNotis() {
        testEntrant.addInvitedEventsNotis("8y341495-f51e-4648-9bb0-c390f558db20");
        ArrayList<String> invitedEventsNotis = testEntrant.getInvitedEventsNotis();

        assertEquals(1, invitedEventsNotis.size());
        assertTrue(invitedEventsNotis.contains("8y341495-f51e-4648-9bb0-c390f558db20"));
    }

    @Test
    void testAddAndGetUninvitedEventsNotis() {
        testEntrant.addUninvitedEventsNotis("6y341495-j71e-4648-9bb0-r457f558db20");
        ArrayList<String> uninvitedEventsNotis = testEntrant.getUninvitedEventsNotis();

        assertEquals(1, uninvitedEventsNotis.size());
        assertTrue(uninvitedEventsNotis.contains("6y341495-j71e-4648-9bb0-r457f558db20"));
    }

    @Test
    void testMultipleAdditions() {
        testEntrant.addWaitlistedEvents("8i781495-f91e-4648-9bb0-c390f558jc40");
        testEntrant.addWaitlistedEvents("9i781495-f91e-4648-9bb0-i890f558jc40");

        testEntrant.addFinalistEvents("4u641403-f93e-4648-9bb0-i890f558jc40");
        testEntrant.addFinalistEvents("0q721403-k93e-0648-9bb0-b890f558jc90");

        ArrayList<String> waitlistedEvents = testEntrant.getWaitlistedEvents();
        ArrayList<String> finalistEvents = testEntrant.getFinalistEvents();

        assertEquals(2, waitlistedEvents.size());
        assertTrue(waitlistedEvents.contains("8i781495-f91e-4648-9bb0-c390f558jc40"));
        assertTrue(waitlistedEvents.contains("9i781495-f91e-4648-9bb0-i890f558jc40"));

        assertEquals(2, finalistEvents.size());
        assertTrue(finalistEvents.contains("4u641403-f93e-4648-9bb0-i890f558jc40"));
        assertTrue(finalistEvents.contains("0q721403-k93e-0648-9bb0-b890f558jc90"));
    }
}
