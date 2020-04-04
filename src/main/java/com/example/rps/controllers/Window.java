package com.example.rps.controllers;

import java.security.SecureRandom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

//Karin har skrivit den här klassen
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
        //Används av vissa av subklasserna för att lägga in data som ska visas när fönstret öppnas. Anropas för alla fönster när de öppnas men alla använder den inte
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

    public boolean validateToken(String token) {
        PreparedStatement getTimestampIssued = null;
        boolean validToken = false;

        try{
            getTimestampIssued = conn.prepareStatement("SELECT issued FROM tokens WHERE value = ? AND issued >= (CURRENT_TIMESTAMP - interval '1 day')");
            getTimestampIssued.setString(1, token);
            ResultSet results = getTimestampIssued.executeQuery();
            if(results.next()) {
                validToken = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (getTimestampIssued != null) {
                    getTimestampIssued.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return validToken;
    }

    public int getUserId(String token) {
        PreparedStatement getUserId = null;
        Connection conn = getConnection();
        int userId = -1;
        try {
            getUserId = conn.prepareStatement("SELECT user_id FROM tokens WHERE value = ?");
            getUserId.setString(1, token);
            ResultSet results = getUserId.executeQuery();
            if (results.next()) {
                userId = results.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (getUserId != null) {
                try {
                    getUserId.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return userId;
    }
}
