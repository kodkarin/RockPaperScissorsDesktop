package com.example.rps.controllers;


import com.example.rps.models.Player;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

//Karin har skrivit den här klassen
public class AddFriendWindow extends Window {

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
    private ListView<Player> searchResultListView;

    @FXML
    private Button addFriendButton;

    @FXML
    private Label messageLabel;

    private boolean firstNameEntered = false;
    private boolean lastNameEntered = false;
    private boolean usernameEntered = false;
    private boolean emailEntered = false;

    @FXML
    private void handleKeyTypedFirstNameTextField() {
        firstNameEntered = !firstNameTextField.getText().equals("");
        activateSearchButton();
    }

    @FXML
    private void handleKeyTypedLastNameTextField() {
        lastNameEntered = !lastNameTextField.getText().equals("");
        activateSearchButton();
    }

    @FXML
    private void handleKeyTypedUsernameTextField() {
        usernameEntered = !usernameTextField.getText().equals("");
        activateSearchButton();
    }

    @FXML
    private void handleKeyTypedEmailTextField() {
        emailEntered = !emailTextField.getText().equals("");
        activateSearchButton();
    }

    private void activateSearchButton() {
        messageLabel.setVisible(false);
        if (((firstNameEntered) || (lastNameEntered)) ||
                ((usernameEntered) || (emailEntered))) {
            searchButton.setDisable(false);
        }
        else {
            searchButton.setDisable(true);
        }
    }


    @FXML
    private void handleSearchButton() {

        searchResultListView.getItems().clear();
        searchResultListView.setVisible(false);
        addFriendButton.setDisable(true);
        addFriendButton.setVisible(false);
        messageLabel.setVisible(false);

        searchForFriends();

        if (searchResultListView.getItems().size() > 0 ) {
            searchResultListView.setVisible(true);
            addFriendButton.setVisible(true);
            addFriendButton.setDisable(true);
        } else {
            messageLabel.setText("No matches found");
            messageLabel.setVisible(true);
        }
    }

    @FXML
    private void enableAddFriendButton() {
        messageLabel.setVisible(false);
        addFriendButton.setDisable(false);
    }

    @FXML
    private void handleAddFriendButton() {

        Player friendToAdd = searchResultListView.getSelectionModel().getSelectedItem();
        PreparedStatement checkIfFriendExistsStatement = null;
        ResultSet friendExistsResults = null;
        PreparedStatement addFriendStatement = null;

        try {
            checkIfFriendExistsStatement = getConnection().prepareStatement("SELECT * FROM friends WHERE player1 = ? AND player2 = ?");
            checkIfFriendExistsStatement.setInt(1, getUserId(getToken()));
            checkIfFriendExistsStatement.setInt(2, friendToAdd.getUserId());

            friendExistsResults = checkIfFriendExistsStatement.executeQuery();

            if (friendExistsResults.next()) {
                messageLabel.setText("Already listed as friend");
                messageLabel.setVisible(true);
                addFriendButton.setDisable(true);
            } else {
                addFriendStatement = getConnection().prepareStatement("INSERT INTO friend_requests (player1, requested_friend) VALUES (?, ?)");
                addFriendStatement.setInt(1, getUserId(getToken()));
                addFriendStatement.setInt(2, friendToAdd.getUserId());
                addFriendStatement.executeUpdate();
                getScreenController().setWindow(ScreenController.NEW_GAME, getToken());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (checkIfFriendExistsStatement != null) {
                    checkIfFriendExistsStatement.close();
                }
                if (friendExistsResults != null) {
                    friendExistsResults.close();
                }
                if (addFriendStatement != null) {
                    addFriendStatement.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void searchForFriends() {

        PreparedStatement searchStatement = null;
        ResultSet searchResults = null;

        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String username = usernameTextField.getText();
        String email = emailTextField.getText();

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
            } else if (emailEntered){
                searchStatement = conn.prepareStatement("SELECT * FROM users WHERE email = ?");
                searchStatement.setString(1, email);
            }

            if(searchStatement!= null) {
                searchResults = searchStatement.executeQuery();

                while(searchResults.next()) {

                    int playerId = searchResults.getInt("id");
                    String friendUsername = searchResults.getString("username");

                    Player possibleFriend = new Player(friendUsername, playerId);
                    searchResultListView.getItems().add(possibleFriend);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try  {
                if (searchStatement != null) {
                    searchStatement.close();
                }
                if (searchResults != null) {
                    searchResults.close();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void backButtonClicked() {
        getScreenController().setWindow(ScreenController.NEW_GAME, getToken());
    }
}
