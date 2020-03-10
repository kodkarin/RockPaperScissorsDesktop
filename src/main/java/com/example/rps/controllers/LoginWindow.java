package com.example.rps.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginWindow {
    private Connection conn;
    private ScreenController screenController;

    @FXML
    private Label message;

    public LoginWindow(Connection conn, ScreenController screenController) {
        this.conn = conn;
        this.screenController = screenController;
    }

    @FXML
    public void login(String username, String password) {
        PreparedStatement loginStatement= null;
        String loginMessage = "OK";

        try {
            loginStatement = conn.prepareStatement("Select * from \"users\" where \"username\" = ? and \"password\" = ?;");
            loginStatement.setString(1, username);
            loginStatement.setString(2, password);
            ResultSet results = loginStatement.executeQuery();

            if (!results.next()) {
                message.setText("Felaktigt användarnamn eller lösenord");
                message.setTextFill(Color.RED);
                message.setVisible(true);
            } else {
                screenController.activate("games");
            }

            while(results.next()) {
                System.out.println("Användaruppgifter: \nNamn: " + results.getString("first_name") + " "
                        + results.getString("last_name") + " \nE-post: " + results.getString("email")
                        + "\nAnvändarnamn: " + results.getString("username"));
            }

        } catch (Exception e) {
            loginMessage = e.getMessage();
        } finally {
            try {
                if (loginStatement != null) {
                    loginStatement.close();
                }
            } catch (Exception ex) {
                loginMessage = ex.getMessage();
            }
        }


    }
}