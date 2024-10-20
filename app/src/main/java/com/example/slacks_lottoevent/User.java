package com.example.slacks_lottoevent;



public class User {
    private String name;
    private String email;
    private String phone;
    private Entrant entrant;
    private Organizer organizer;
    private EventList eventList;



    public User(String name, String phone, String email){
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.entrant = new Entrant();
    }

}
