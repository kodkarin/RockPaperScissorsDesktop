package com.example.rps.controllers;

import com.example.rps.models.Game;
import com.example.rps.models.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ActiveGameWindow extends Window {

    @FXML
    private Label makeMoveLabel;
    @FXML
    private Label opponentsTurnLabel;
    @FXML
    private Label finishedGamesLabel;
    @FXML
    private ListView<Game> makeMoveListView;
    @FXML
    private ListView<Game> opponentsTurnListView;
    @FXML
    private ListView<Game> finishedGamesListView;
    @FXML
    private Button newFriendRequestButton;
    @FXML
    private Label gameInvitationLabel;
    @FXML
    private HBox gameInvitationHBox;
    @FXML
    private ListView<Player> gameInvitationListView;
    @FXML
    private Button acceptInvitationButton;
    @FXML
    private Button rejectInvitationButton;

    //Karin har skrivit den här metoden
    @Override
    public void setUpWindow() {

        makeMoveLabel.setText("You can't make any moves right now");
        makeMoveListView.getItems().clear();
        makeMoveListView.setVisible(false);
        opponentsTurnLabel.setText("No opponent to wait for to make a move");
        opponentsTurnListView.setVisible(false);
        finishedGamesLabel.setVisible(false);
        finishedGamesListView.getItems().clear();
        finishedGamesListView.setVisible(false);
        newFriendRequestButton.setVisible(false);
        newFriendRequestButton.setDisable(true);
        gameInvitationLabel.setVisible(false);
        gameInvitationHBox.setVisible(false);
        acceptInvitationButton.setDisable(true);
        rejectInvitationButton.setDisable(true);
        gameInvitationListView.getItems().clear();

        PreparedStatement getAllGames = null;
        ResultSet allGameResults = null;
        PreparedStatement getFriendRequests = null;
        ResultSet friendRequestsResults = null;
        PreparedStatement getGameInvitations = null;
        ResultSet gameInvitationsResults = null;

        try {
            getAllGames = getConnection().prepareStatement("SELECT * FROM matches WHERE player1 = ? OR player2 = ?");
            getAllGames.setInt(1,getUserId(getToken()));
            getAllGames.setInt(2, getUserId(getToken()));

            allGameResults = getAllGames.executeQuery();
            String getPlayerSql = "SELECT username FROM users WHERE id = ?;";

            while(allGameResults.next()) {
                int gameId = allGameResults.getInt("id");
                int player1Id = allGameResults.getInt("player1");
                String player1Username = "";
                int player2Id = allGameResults.getInt("player2");
                String player2Username = "";

                PreparedStatement getPlayer1Statement = getConnection().prepareStatement(getPlayerSql);
                getPlayer1Statement.setInt(1, player1Id);
                PreparedStatement getPlayer2Statement = getConnection().prepareStatement(getPlayerSql);
                getPlayer2Statement.setInt(1, player2Id);
                ResultSet player1Results = getPlayer1Statement.executeQuery();
                if (player1Results.next()){
                    player1Username = player1Results.getString(1);
                }
                ResultSet player2Results = getPlayer2Statement.executeQuery();
                if (player2Results.next()){
                    player2Username = player2Results.getString(1);
                }
                if (player1Username != null && player2Username != null) {
                    Player player1 = new Player(player1Username, player1Id);
                    Player player2 = new Player(player2Username, player2Id);
                    Game game = new Game(gameId, player1, player2);
                    setGameScore(game);

                    if (allGameResults.getInt("winner") > 0) {
                        finishedGamesListView.getItems().add(game);
                    } else {
                        PreparedStatement checkIfPlayerCanMakeMove = getConnection().prepareStatement("SELECT user_id, max(round_no) FROM moves WHERE match_id = ? GROUP BY user_id;");
                        checkIfPlayerCanMakeMove.setInt(1, gameId);
                        ResultSet maxRoundNumberResults = checkIfPlayerCanMakeMove.executeQuery();

                        if(maxRoundNumberResults.next()) {
                            int userId1 = maxRoundNumberResults.getInt("user_id");
                            boolean hasUserId1 = getUserId(getToken()) == userId1;
                            int maxRound1 = maxRoundNumberResults.getInt(2);
                            if (maxRoundNumberResults.next()) {
                                int maxRound2 = maxRoundNumberResults.getInt(2);

                                if(hasUserId1) {
                                    if (maxRound1 > maxRound2) {
                                        opponentsTurnListView.getItems().add(game);
                                    } else {
                                        makeMoveListView.getItems().add(game);
                                    }
                                } else {
                                    if (maxRound2 > maxRound1) {
                                        opponentsTurnListView.getItems().add(game);
                                    } else {
                                        makeMoveListView.getItems().add(game);
                                    }
                                }
                            } else if (hasUserId1) {
                                // Bara den spelare som har userId1 har gjort drag hittills i matchen.
                                opponentsTurnListView.getItems().add(game);
                            } else {
                                makeMoveListView.getItems().add(game);
                            }
                        } else {
                            // Inga drag har gjorts i matchen hittills. Båda spelarna kan göra drag
                            makeMoveListView.getItems().add(game);
                        }
                        checkIfPlayerCanMakeMove.close();
                        maxRoundNumberResults.close();
                    }
                }
                getPlayer1Statement.close();
                getPlayer2Statement.close();
                player1Results.close();
                player2Results.close();
            }

            getGameInvitations = getConnection().prepareStatement("SELECT * FROM users INNER JOIN invitations " +
                    "ON users.id = invitations.player1 WHERE invitations.invited_friend = ?;");
            getGameInvitations.setInt(1, getUserId(getToken()));
            gameInvitationsResults = getGameInvitations.executeQuery();
            while(gameInvitationsResults.next()) {
                Player player = new Player(gameInvitationsResults.getString("username"), gameInvitationsResults.getInt("id"));
                gameInvitationListView.getItems().add(player);
            }

            if (makeMoveListView.getItems().size() > 0) {
                makeMoveLabel.setText("Your turn to make a move");
                makeMoveListView.setVisible(true);
            }
            if(opponentsTurnListView.getItems().size() > 0) {
                opponentsTurnLabel.setText("Opponent's turn to make a move");
                opponentsTurnListView.setVisible(true);
            }
            if(finishedGamesListView.getItems().size() > 0) {
                finishedGamesLabel.setVisible(true);
                finishedGamesListView.setVisible(true);
            }
            if(gameInvitationListView.getItems().size() > 0) {
                gameInvitationLabel.setVisible(true);
                gameInvitationHBox.setVisible(true);
            }

            getFriendRequests = getConnection().prepareStatement("SELECT * FROM friend_requests WHERE requested_friend = ?;");
            getFriendRequests.setInt(1, getUserId(getToken()));
            friendRequestsResults = getFriendRequests.executeQuery();

            if (friendRequestsResults.next()){
                newFriendRequestButton.setVisible(true);
                newFriendRequestButton.setDisable(false);
            }



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try  {
                if (getAllGames != null){
                    getAllGames.close();
                }
                if (allGameResults != null) {
                    allGameResults.close();
                }
                if (getFriendRequests != null) {
                    getFriendRequests.close();
                }
                if (friendRequestsResults != null) {
                    friendRequestsResults.close();
                }
                if (getGameInvitations != null) {
                    getGameInvitations.close();
                }
                if (gameInvitationsResults != null) {
                    gameInvitationsResults.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


    }

    //Karin har skrivit den här metoden
    private void setGameScore(Game game) {
        PreparedStatement getAllRounds = null;
        ResultSet results = null;
        int userIdPlayer1 = game.getPlayer1().getUserId();
        try {
            getAllRounds = getConnection().prepareStatement("SELECT * FROM rounds WHERE match_id = ?;");
            getAllRounds.setInt(1, game.getGameID());

            results = getAllRounds.executeQuery();
            while (results.next()) {
                if (results.getInt("round_winner") > 0) {
                    int winner = results.getInt("round_winner") == userIdPlayer1 ? 1 : 2;
                    if (winner == 1) {
                        game.increaseScorePlayer1();
                    } else {
                        game.increaseScorePlayer2();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(getAllRounds != null) {
                    getAllRounds.close();
                }
                if (results != null) {
                    results.close();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    //Karin har skrivit den här metoden
    @FXML
    public void openGame(MouseEvent event) {

        ListView listView = (ListView) event.getSource();
        if (listView.getSelectionModel().getSelectedItem() != null) {
            Game game =  (Game) listView.getSelectionModel().getSelectedItem();
            getScreenController().setWindow(ScreenController.GAME, getToken(), game);
        }
    }

    //Karin har skrivit den här metoden
    @FXML
    public void openFinishedGame() {
        if (finishedGamesListView.getSelectionModel().getSelectedItem() != null) {
            Game game = finishedGamesListView.getSelectionModel().getSelectedItem();
            getScreenController().setWindow(ScreenController.WINNER, getToken(), game);
        }
    }

    //Karin har skrivit den här metoden
    @FXML
    private void activateGameInvitationButtons() {
        acceptInvitationButton.setDisable(false);
        rejectInvitationButton.setDisable(false);
    }

    //Karin har skrivit den här metoden
    @FXML
    private void handleGameInvitationButtons(ActionEvent event) {

        Player invitingPlayer = gameInvitationListView.getSelectionModel().getSelectedItem();
        PreparedStatement acceptGameInvitationStatement = null;
        PreparedStatement removeGameInvitationStatement = null;

        Button button = (Button) event.getSource();
        String buttonId = button.getId();

        try {
            Connection conn = getConnection();
            conn.setAutoCommit(false);

            if (buttonId.equals("acceptInvitationButton")) {

                acceptGameInvitationStatement = conn.prepareStatement("INSERT INTO matches (player1, player2) " +
                        "VALUES (?, ?);");
                acceptGameInvitationStatement.setInt(1, invitingPlayer.getUserId());
                acceptGameInvitationStatement.setInt(2, getUserId(getToken()));
                acceptGameInvitationStatement.executeUpdate();
            }

            removeGameInvitationStatement = conn.prepareStatement("DELETE FROM invitations WHERE player1 = ? AND " +
                    "invited_friend = ?;");
            removeGameInvitationStatement.setInt(1, invitingPlayer.getUserId());
            removeGameInvitationStatement.setInt(2, getUserId(getToken()));
            removeGameInvitationStatement.executeUpdate();

            conn.commit();
            conn.setAutoCommit(true);

            setUpWindow();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(acceptGameInvitationStatement != null) {
                    acceptGameInvitationStatement.close();
                }
                if (removeGameInvitationStatement != null) {
                    removeGameInvitationStatement.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    //Christian har skrivit den här metoden
    public void newGameButtonClicked() {
        getScreenController().setWindow(ScreenController.NEW_GAME, getToken());
    }

    //Christian har skrivit den här metoden
    public void helpButtonClicked() {
        getScreenController().setPreviousPage(ScreenController.ACTIVE_GAMES);
        getScreenController().setWindow(ScreenController.RULES, getToken());
    }

    //Christian har skrivit den här metoden
    public void logOutButtonClicked() {
        getScreenController().setWindow(ScreenController.LOGIN, getToken());
    }

    //Karin har skrivit den här metoden
    @FXML
    private void showFriendRequests() {
        getScreenController().setWindow(ScreenController.NEW_GAME, getToken());
    }

    @FXML
    private void refreshActiveGameWindow() {
        setUpWindow();
    }
}
