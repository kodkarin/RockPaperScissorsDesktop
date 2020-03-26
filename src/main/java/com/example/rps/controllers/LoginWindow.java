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
        PreparedStatement getUserId = null;
        PreparedStatement setToken = null;

        try {
            Connection conn = getConnection();
            loginStatement = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?;");
            loginStatement.setString(1, username);
            loginStatement.setString(2, password);

            ResultSet results = loginStatement.executeQuery();


            if (!results.next()) {
                message.setText("Felaktigt anv" + (char)228 + "ndarnamn eller l" + (char)246 + "senord");
                message.setTextFill(Color.RED);
                message.setVisible(true);
                usernameTextField.setText("");
                passwordField.clear();
            } else {

                String token = createToken(25);

                getUserId = conn.prepareStatement("SELECT id FROM users WHERE username = ? AND password = ?");
                getUserId.setString(1, username);
                getUserId.setString(2, password);
                ResultSet userIdResults = getUserId.executeQuery();
                userIdResults.next();
                int userId = userIdResults.getInt(1);

                setToken = conn.prepareStatement("INSERT INTO tokens (user_id, value, issued) VALUES (?, ?, CURRENT_TIMESTAMP)");
                setToken.setInt(1, userId);
                setToken.setString(2, token);

                setToken.executeUpdate();

                getScreenController().setWindow(ScreenController.NEW_GAME, token);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (loginStatement != null) {
                    loginStatement.close();
                }
                if (setToken != null) {
                    setToken.close();
                }
                if(getUserId != null) {
                    getUserId.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    public void handleNewUserButton() {
        getScreenController().setWindow(ScreenController.CREATE_ACCOUNT, "");
    }
}