package com.example.rps.models;

public class Player {

    private String token;
    private String userName;

    public Player(String token, String userName) {
        this.token = token;
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public String getUserName() {
        return userName;
    }
}
