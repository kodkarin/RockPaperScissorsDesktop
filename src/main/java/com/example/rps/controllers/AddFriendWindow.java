package com.example.rps.controllers;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AddFriendWindow extends Window {

    @FXML
    private GridPane root;

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private Button searchButton;

    @FXML
    private void handleSearchButton() {
        boolean firstNameEntered = false;
        boolean lastNameEntered = false;
        boolean usernameEntered = false;
        boolean emailEntered = false;

        PreparedStatement searchStatement = null;

        String firstName = firstNameTextField.getText();
        if (!firstName.equals("")) {
            firstNameEntered = true;
        }

        String lastName = lastNameTextField.getText();
        if (!lastName.equals("")) {
            lastNameEntered = true;
        }

        String username = usernameTextField.getText();
        if (!username.equals("")) {
            usernameEntered = true;
        }

        String email = emailTextField.getText();
        if (!email.equals("")) {
            emailEntered = true;
        }

        try {

            Connection conn = getConnection();



            if(firstNameEntered) {
                if (lastNameEntered) {
                    if(usernameEntered){
                        if(emailEntered){
                            searchStatement = conn.prepareStatement("SELECT * FROM users WHERE first_name = ? AND " +
                                    "last_name = ? AND username = ? AND email = ?");
                            searchStatement.setString(1, firstName);
                            searchStatement.setString(2, lastName);
                            searchStatement.setString(3, username);
                            searchStatement.setString(4, email);
                        } else {
                            searchStatement = conn.prepareStatement("SELECT * FROM users WHERE first_name = ? AND " +
                                    "last_name = ? AND username = ?");
                            searchStatement.setString(1, firstName);
                            searchStatement.setString(2, lastName);
                            searchStatement.setString(3, username);
                        }
                    } else if (emailEntered) {
                        searchStatement = conn.prepareStatement("SELECT * FROM users WHERE first_name = ? AND " +
                                "last_name = ? AND email = ?");
                        searchStatement.setString(1, firstName);
                        searchStatement.setString(2, lastName);
                        searchStatement.setString(3, email);
                    } else {
                        searchStatement = conn.prepareStatement("SELECT * FROM users WHERE first_name = ? AND " +
                                "last_name = ?");
                        searchStatement.setString(1, firstName);
                        searchStatement.setString(2, lastName);
                    }
                } else if (usernameEntered) {
                    if(emailEntered) {
                        searchStatement = conn.prepareStatement("SELECT * FROM users WHERE first_name = ? AND " +
                                "username = ? AND email = ?");
                        searchStatement.setString(1, firstName);
                        searchStatement.setString(2, username);
                        searchStatement.setString(3, email);
                    } else {
                        searchStatement = conn.prepareStatement("SELECT * FROM users WHERE first_name = ? AND " +
                                "username = ?");
                        searchStatement.setString(1, firstName);
                        searchStatement.setString(2, username);
                    }
                } else if (emailEntered) {
                    searchStatement = conn.prepareStatement("SELECT * FROM users WHERE first_name = ? AND " +
                            "email = ?");
                    searchStatement.setString(1, firstName);
                    searchStatement.setString(2, email);
                } else {
                    searchStatement = conn.prepareStatement("SELECT * FROM users WHERE first_name = ?");
                    searchStatement.setString(1, firstName);
                }
            } else if (lastNameEntered) {
                if (usernameEntered) {
                    if (emailEntered) {
                        searchStatement = conn.prepareStatement("SELECT * FROM users WHERE last_name = ? AND " +
                                "username = ? AND email = ?");
                        searchStatement.setString(1, lastName);
                        searchStatement.setString(2, username);
                        searchStatement.setString(3, email);
                    } else {
                        searchStatement = conn.prepareStatement("SELECT * FROM users WHERE last_name = ? AND " +
                                "username = ?");
                        searchStatement.setString(1, lastName);
                        searchStatement.setString(2, username);
                    }
                } else if (emailEntered) {
                    searchStatement = conn.prepareStatement("SELECT * FROM users WHERE last_name = ? AND " +
                            "email = ?");
                    searchStatement.setString(1, lastName);
                    searchStatement.setString(2, email);
                } else {
                    searchStatement = conn.prepareStatement("SELECT * FROM users WHERE last_name = ?");
                    searchStatement.setString(1, lastName);
                }
            } else if (usernameEntered) {
                if (emailEntered) {
                    searchStatement = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND " +
                            "email = ?");
                    searchStatement.setString(1, username);
                    searchStatement.setString(2, email);
                } else {
                    searchStatement = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
                    searchStatement.setString(1, username);
                }
            } else {
                searchStatement = conn.prepareStatement("SELECT * FROM users WHERE email = ?");
                searchStatement.setString(1, email);
            }

            ResultSet results = searchStatement.executeQuery();

            int number = 1;
            while(results.next()) {
                String labelId = "resultTextField" + number;
                String buttonId = "resultButton" + number;
                Label tempLabel = new Label(results.getString("username"));
                int playerId = results.getInt("id");
                Button tempButton = new Button();
                tempButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        PreparedStatement addFriendStatement = null;
                        try {

                            addFriendStatement = conn.prepareStatement("INSERT INTO friend_requests (player1, requested_friend) VALUES (?, ?)");
                            addFriendStatement.setInt(1, getUserId(getToken()));
                            addFriendStatement.setInt(2, playerId);
                            addFriendStatement.executeUpdate();
                            getScreenController().setWindow(ScreenController.NEW_GAME, getToken());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        } finally {
                            if (addFriendStatement != null) {
                                try {
                                    addFriendStatement.close();
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                            }
                        }
                    }
                });
                tempLabel.setId(labelId);
                tempButton.setId(buttonId);
                root.add(tempLabel, 0, 6 + number);
                root.add(tempButton, 1, 6 + number);
                number++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (searchStatement != null) {
                try {
                    searchStatement.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

    }
}
