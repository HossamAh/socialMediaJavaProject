package com.example.demo.model;

public class FriendRequest {

    private int id;
    private String username;

    public FriendRequest(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return username;
    }
}