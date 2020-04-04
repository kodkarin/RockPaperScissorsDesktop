package com.example.rps.controllers;

import com.example.rps.models.Player;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

//Christian har gjort det mesta av layouten för den här klassen
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
    @FXML
    private Label inviteLabel;

    //Karin har skrivit den här metoden
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
        inviteLabel.setVisible(false);
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

    //Karin har skrivit den här metoden
    @FXML
    private void activateInviteFriendButton() {
        inviteButton.setDisable(false);
        inviteLabel.setVisible(false);
        acceptFriendRequest.setDisable(true);
        rejectFriendRequest.setDisable(true);
    }

    //Karin har skrivit den här metoden
    @FXML
    private void activateRequestButtons() {
        acceptFriendRequest.setDisable(false);
        rejectFriendRequest.setDisable(false);
        inviteButton.setDisable(true);
    }

    //Karin har skrivit den här metoden
    @FXML
    private void handleFriendRequestButtons(ActionEvent event) {
        Player friendRequest = requestsListView.getSelectionModel().getSelectedItem();

        PreparedStatement acceptFriendRequestStatement = null;
        PreparedStatement acceptFriendRequestStatement2 = null;
        PreparedStatement removeRequestStatement = null;

        Button button = (Button) event.getSource();
        String buttonId = button.getId();

        try {
            Connection conn = getConnection();
            conn.setAutoCommit(false);

            if (buttonId.equals("acceptFriendRequest")) {
                String sql = "INSERT INTO friends (player1, player2, victories) VALUES (?, ?, 0);";
                acceptFriendRequestStatement = conn.prepareStatement(sql);
                acceptFriendRequestStatement.setInt(1, getUserId(getToken()));
                acceptFriendRequestStatement.setInt(2, friendRequest.getUserId());
                acceptFriendRequestStatement.executeUpdate();

                acceptFriendRequestStatement2 = conn.prepareStatement(sql);
                acceptFriendRequestStatement2.setInt(1, friendRequest.getUserId());
                acceptFriendRequestStatement2.setInt(2, getUserId(getToken()));
                acceptFriendRequestStatement2.executeUpdate();
            }

            removeRequestStatement = conn.prepareStatement("DELETE FROM friend_requests WHERE player1 = ? " +
                    "AND requested_friend = ?");
            removeRequestStatement.setInt(1, friendRequest.getUserId());
            removeRequestStatement.setInt(2, getUserId(getToken()));
            removeRequestStatement.executeUpdate();

            conn.commit();
            conn.setAutoCommit(true);
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

    //Karin har skrivit den här metoden
    @FXML
    public void inviteFriendAndStartGame() {
        Player friendToInvite = friendsListView.getSelectionModel().getSelectedItem();
        PreparedStatement checkIfInvitationExists = null;
        ResultSet results = null;
        PreparedStatement inviteFriendStatement = null;

        try {
            checkIfInvitationExists = getConnection().prepareStatement("SELECT * FROM invitations WHERE player1 = ? AND " +
                    "invited_friend = ?;");
            checkIfInvitationExists.setInt(1, getUserId(getToken()));
            checkIfInvitationExists.setInt(2, friendToInvite.getUserId());

            results = checkIfInvitationExists.executeQuery();

            if (results.next()) {
                inviteLabel.setText("Invitation already sent");
                inviteLabel.setVisible(true);
            } else {

                inviteFriendStatement = getConnection().prepareStatement("INSERT INTO invitations VALUES (?, ?);");
                inviteFriendStatement.setInt(1,getUserId(getToken()));
                inviteFriendStatement.setInt(2, friendToInvite.getUserId());
                inviteFriendStatement.executeUpdate();
                getScreenController().setWindow(ScreenController.ACTIVE_GAMES, getToken());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (checkIfInvitationExists != null) {
                    checkIfInvitationExists.close();
                }
                if (results != null) {
                    results.close();
                }
                if(inviteFriendStatement != null) {
                    inviteFriendStatement.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    public void playWithCpuAndStartGame() {

        PreparedStatement startGameAgainstCpuStatement = null;

        try {
            startGameAgainstCpuStatement = getConnection().prepareStatement("INSERT INTO matches (player1, player2) " +
                    "VALUES (?, ?);");
            startGameAgainstCpuStatement.setInt(1, getUserId(getToken()));
            startGameAgainstCpuStatement.setInt(2, GameWindow.USER_ID_FOR_CPU_PLAYER);
            startGameAgainstCpuStatement.executeUpdate();

            getScreenController().setWindow(ScreenController.ACTIVE_GAMES, getToken());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(startGameAgainstCpuStatement != null) {
                    startGameAgainstCpuStatement.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    //Christian har skrivit den här metoden
    public void addFriendButtonClicked() {
        getScreenController().setWindow(ScreenController.ADD_FRIEND, getToken());
    }

    //Christian har skrivit den här metoden
    public void backButtonClicked() {
        getScreenController().setWindow(ScreenController.ACTIVE_GAMES, getToken());
    }
}
