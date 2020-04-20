package com.droyder.flashchat;

public class InstantRoom {

    private String name, creator;
    private int id;

    public InstantRoom(String roomName, String c, int i) {
        this.name = roomName;
        this.creator= c;
        id  = i;
    }

    public  InstantRoom(){

    }

    public String getCreator() {
        return creator;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
