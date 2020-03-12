package com.example.rps.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginWindow extends Window {



    @FXML
    private Label message;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordField;


    @FXML
    public void initialize() {

    }





    @FXML
    public void handleLoginButton() {


        String username = usernameTextField.getText();
        String password = passwordField.getText();

        PreparedStatement loginStatement= null;

        try {
            Connection conn = super.getConnection();
            loginStatement = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?;");
            loginStatement.setString(1, username);
            loginStatement.setString(2, password);

            ResultSet results = loginStatement.executeQuery();


            if (!results.next()) {
                message.setText("Felaktigt användarnamn eller lösenord");
                message.setTextFill(Color.RED);
                message.setVisible(true);
                usernameTextField.setText("");
                passwordField.clear();
            } else {
                ScreenController.setWindow(ScreenController.ACTIVE_GAMES);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (loginStatement != null) {
                    loginStatement.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


    }
}