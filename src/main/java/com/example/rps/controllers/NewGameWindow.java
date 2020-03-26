package com.example.rps.controllers;
import com.example.rps.models.Player;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class NewGameWindow extends Window {

    @FXML
    private ListView<Player> friendsListView;
    @FXML
    private ListView<Player> requestsListView;
    @FXML
    private Label requestLabel;
    @FXML
    private Button acceptFriendRequest;
    @FXML
    private Button rejectFriendRequest;
    @FXML
    private Button inviteButton;

    @Override
    public void setUpWindow() {

        PreparedStatement getFriends = null;
        PreparedStatement getFriendRequests = null;
        ResultSet friendsResults = null;
        ResultSet requestsResults = null;

        try {
            getFriends = getConnection().prepareStatement("SELECT * FROM users INNER JOIN friends ON friends.player2 = users.id" +
                    " WHERE friends.player1 IN (SELECT users.id FROM users INNER JOIN tokens ON tokens.user_id = users.id WHERE tokens.value = ?);");
            getFriends.setString(1, getToken());
            friendsResults = getFriends.executeQuery();


            while(friendsResults.next()){
                if (friendsResults.getInt("id") != GameWindow.USER_ID_FOR_CPU_PLAYER) {
                    Player player = new Player (friendsResults.getString("username"), friendsResults.getInt("id"));
                    friendsListView.getItems().add(player);
                }
            }

            getFriendRequests = getConnection().prepareStatement("SELECT * FROM users INNER JOIN friend_requests ON " +
                    "friend_requests.player1 = users.id WHERE friend_requests.requested_friend IN (SELECT users.id FROM users INNER JOIN " +
                    "tokens ON tokens.user_id = users.id WHERE tokens.value = ?);");
            getFriendRequests.setString(1, getToken());
            requestsResults = getFriendRequests.executeQuery();

            while(requestsResults.next()) {
                Player player = new Player(requestsResults.getString("username"), requestsResults.getInt("id"));
                requestsListView.getItems().add(player);
                requestsListView.setVisible(true);
                requestLabel.setVisible(true);
                acceptFriendRequest.setVisible(true);
                rejectFriendRequest.setVisible(true);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (getFriends != null) {
                    getFriends.close();
                }
                if (getFriendRequests != null) {
                    getFriendRequests.close();
                }
                if (friendsResults != null) {
                    friendsResults.close();
                }
                if (requestsResults != null) {
                    requestsResults.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    public void activateInviteFriendButton() {
        inviteButton.setDisable(false);
        acceptFriendRequest.setDisable(true);
        rejectFriendRequest.setDisable(true);
    }

    @FXML
    public void activateRequestButtons() {
        acceptFriendRequest.setDisable(false);
        rejectFriendRequest.setDisable(false);
        inviteButton.setDisable(true);
    }

    public void inviteFriendAndStartGame() {
        //skriv metod som startar nytt spel med en vän
    }

    public void playWithCpuAndStartGame() {
        //skriv metod som startar nytt spel mot datorn
    }

    public void addFriendButtonClicked() {
        getScreenController().setWindow(ScreenController.ADD_FRIEND, getToken());
    }

    public void backButtonClicked() {
        getScreenController().setWindow(ScreenController.ACTIVE_GAMES, getToken());
    }
}
