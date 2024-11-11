package com.example.slacks_lottoevent.refactor;

import java.util.ArrayList;

/**
 * EntrantList is a class that holds a list of Entrant objects.
 * It is used to store all the entrants in the system.
 */
public class EntrantList {
    private ArrayList<Entrant> entrants;

    /**
     * Constructor for EntrantList
     */
    public EntrantList() {
        this.entrants = new ArrayList<Entrant>();
    }

    public ArrayList<Entrant> getEntrants() {
        return entrants;
    }

    public void setEntrants(ArrayList<Entrant> entrants) {
        this.entrants = entrants;
    }

    public void addEntrant(Entrant entrant) {
        entrants.add(entrant);
    }

    public void removeEntrant(Entrant entrant) {
        entrants.remove(entrant);
    }

    // TODO:  Find an entrant if their user hash matches the given user hash (SET UP THE isEqual() method for this!)
    // TODO: Is an this entrant object in the list?
    public boolean contains(Entrant entrant) {
        return entrants.contains(entrant);
    }
}