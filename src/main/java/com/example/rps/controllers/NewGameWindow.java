package com.example.rps.controllers;
import com.example.rps.models.Player;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
        friendsListView.getItems().clear();
        requestsListView.getItems().clear();
        acceptFriendRequest.setDisable(true);
        rejectFriendRequest.setDisable(true);
        inviteButton.setDisable(true);
        requestsListView.setVisible(false);
        requestLabel.setVisible(false);
        acceptFriendRequest.setVisible(false);
        rejectFriendRequest.setVisible(false);

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
    private void activateInviteFriendButton() {
        inviteButton.setDisable(false);
        acceptFriendRequest.setDisable(true);
        rejectFriendRequest.setDisable(true);
    }

    @FXML
    private void activateRequestButtons() {
        acceptFriendRequest.setDisable(false);
        rejectFriendRequest.setDisable(false);
        inviteButton.setDisable(true);
    }

    @FXML
    private void handleFriendRequestButtons(ActionEvent event) {
        Player friendRequest = requestsListView.getSelectionModel().getSelectedItem();

        PreparedStatement acceptFriendRequestStatement = null;
        PreparedStatement acceptFriendRequestStatement2 = null;
        PreparedStatement removeRequestStatement = null;

        Button button = (Button) event.getSource();
        String buttonId = button.getId();

        try {

            getConnection().setAutoCommit(false);

            if (buttonId.equals("acceptFriendRequest")) {
                String sql = "INSERT INTO friends (player1, player2, victories) VALUES (?, ?, 0);";
                acceptFriendRequestStatement = getConnection().prepareStatement(sql);
                acceptFriendRequestStatement.setInt(1, getUserId(getToken()));
                acceptFriendRequestStatement.setInt(2, friendRequest.getUserId());
                acceptFriendRequestStatement.executeUpdate();

                acceptFriendRequestStatement2 = getConnection().prepareStatement(sql);
                acceptFriendRequestStatement2.setInt(1, friendRequest.getUserId());
                acceptFriendRequestStatement2.setInt(2, getUserId(getToken()));
                acceptFriendRequestStatement2.executeUpdate();
            }

            removeRequestStatement = getConnection().prepareStatement("DELETE FROM friend_requests WHERE player1 = ? " +
                    "AND requested_friend = ?");
            removeRequestStatement.setInt(1, friendRequest.getUserId());
            removeRequestStatement.setInt(2, getUserId(getToken()));
            removeRequestStatement.executeUpdate();

            getConnection().commit();
            setUpWindow();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(acceptFriendRequestStatement != null) {
                    acceptFriendRequestStatement.close();
                }
                if(acceptFriendRequestStatement2 != null) {
                    acceptFriendRequestStatement2.close();
                }
                if (removeRequestStatement != null) {
                    removeRequestStatement.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


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
