package com.example.rps.controllers;

import java.security.SecureRandom;
import java.sql.Connection;

public class Window {

    private Connection conn;
    private ScreenController screenController;
    private String token;

    public Window () {
        conn = null;
    }

    public void init(Connection conn, ScreenController screenController, String token) {
        this.conn = conn;
        this.screenController = screenController;
        this.token = token;
    }

    public void setUpWindow() {

    }

    public Connection getConnection() {
        return conn;
    }

    public ScreenController getScreenController() {
        return screenController;
    }

    public String getToken() {
        return token;
    }

    public String createToken(int numberOfCharacters) {
        char[] characters = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f','g','h','i','j','k','l','m',
                'n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I',
                'J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
        StringBuilder sb = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 1; i <= numberOfCharacters; i++) {
            char character = characters[secureRandom.nextInt(characters.length)];
            sb.append(character);
        }
        return sb.toString();
    }
}
