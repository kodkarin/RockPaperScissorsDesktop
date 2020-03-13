package com.example.rps.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CreateAccountWindow extends Window {

    @FXML
    private TextField newUsername;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField passwordField2;

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private TextField email;

    @FXML
    private Label message;

    @FXML
    private Button newUserButton;

    private boolean newUsernameEntered = false;
    private boolean passwordEntered = false;
    private boolean password2Entered = false;
    private boolean emailEntered = false;

    @FXML
    private void handleKeyTypedNewUsername() {
        newUsernameEntered = !newUsername.getText().equals("");
        checkButtonActive();
    }

    @FXML
    private void handleKeyTypedPasswordField() {
        passwordEntered = !passwordField.getText().equals("");
        checkButtonActive();
    }

    @FXML
    private void handleKeyTypedPasswordField2() {
        password2Entered = !passwordField2.getText().equals("");
        checkButtonActive();
    }

    @FXML
    private void handleKeyTypedEmail() {
        emailEntered = !email.getText().equals("");
        checkButtonActive();
    }

    private void checkButtonActive() {
        if (((newUsernameEntered) && (passwordEntered)) &&
                ((password2Entered) && (emailEntered))) {
            newUserButton.setDisable(false);
        }
        else {
            newUserButton.setDisable(true);
        }
    }

    @FXML
    private void handleCreateAccountButton() {

        String password = passwordField.getText();
        String repeatedPassword = passwordField2.getText();

        if (password.equals(repeatedPassword)) {

            String username = newUsername.getText();
            String firstName = firstNameTextField.getText();
            String lastName = lastNameTextField.getText();
            String emailAddress = email.getText();

            PreparedStatement checkIfUserExistsStatement = null;
            PreparedStatement createAccountStatement = null;
            PreparedStatement setToken = null;
            PreparedStatement getUserId = null;

            try {
                Connection conn = getConnection();
                checkIfUserExistsStatement = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
                checkIfUserExistsStatement.setString(1, username);

                ResultSet results = checkIfUserExistsStatement.executeQuery();


                if (results.next()) {
                    message.setText("Anv" + (char)228 + "ndarnamnet " + (char)228 + "r redan taget");
                    message.setVisible(true);
                    newUsername.setText("");
                    passwordField.clear();
                    passwordField2.clear();
                } else {

                    if(firstName.equals("")) {
                        if(lastName.equals("")) {
                            createAccountStatement = conn.prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?)");
                            createAccountStatement.setString(1, username);
                            createAccountStatement.setString(2, password);
                            createAccountStatement.setString(3, emailAddress);
                        } else {
                            createAccountStatement = conn.prepareStatement("INSERT INTO users (username, password, last_name, email) VALUES (?, ?, ?, ?)");
                            createAccountStatement.setString(1, username);
                            createAccountStatement.setString(2, password);
                            createAccountStatement.setString(3, lastName);
                            createAccountStatement.setString(4, emailAddress);
                        }
                    } else if (lastName.equals("")) {
                        createAccountStatement = conn.prepareStatement("INSERT INTO users (username, password, first_name, email) VALUES (?, ?, ?, ?)");
                        createAccountStatement.setString(1, username);
                        createAccountStatement.setString(2, password);
                        createAccountStatement.setString(3, firstName);
                        createAccountStatement.setString(4, emailAddress);
                    } else {
                        createAccountStatement = conn.prepareStatement("INSERT INTO users (username, password, first_name, last_name, email) VALUES (?, ?, ?, ?, ?)");
                        createAccountStatement.setString(1, username);
                        createAccountStatement.setString(2, password);
                        createAccountStatement.setString(3, firstName);
                        createAccountStatement.setString(4, lastName);
                        createAccountStatement.setString(5, emailAddress);
                    }

                    createAccountStatement.executeUpdate();

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

                    getScreenController().setWindow(ScreenController.ACTIVE_GAMES, token);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (createAccountStatement != null) {
                        createAccountStatement.close();
                    }
                    if(checkIfUserExistsStatement != null) {
                        checkIfUserExistsStatement.close();
                    }
                    if(setToken != null) {
                        setToken.close();
                    }
                    if(getUserId != null) {
                        getUserId.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            message.setText("L" + (char)246 + "senorden " + (char)228 + "r inte identiska");
            message.setVisible(true);
            passwordField.clear();
            passwordField2.clear();
            newUserButton.setDisable(true);
        }

    }


}
