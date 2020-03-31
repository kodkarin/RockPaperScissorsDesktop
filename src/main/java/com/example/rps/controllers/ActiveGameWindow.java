package com.example.rps.controllers;

import com.example.rps.models.Game;
import com.example.rps.models.Player;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.beans.EventHandler;
import java.io.IOException;
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

    @Override
    public void setUpWindow() {

        makeMoveLabel.setVisible(false);
        makeMoveListView.setVisible(false);
        opponentsTurnLabel.setVisible(false);
        opponentsTurnListView.setVisible(false);
        finishedGamesLabel.setVisible(false);
        finishedGamesListView.setVisible(false);
        newFriendRequestButton.setVisible(false);
        newFriendRequestButton.setDisable(true);

        PreparedStatement getAllGames = null;
        ResultSet allGameResults = null;
        PreparedStatement getFriendRequests = null;
        ResultSet friendRequestsResults = null;

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
                            int maxRound1 = maxRoundNumberResults.getInt(2);
                            if (maxRoundNumberResults.next()) {
                                int userId2 = maxRoundNumberResults.getInt("user_id");
                                int maxRound2 = maxRoundNumberResults.getInt(2);
                                if (maxRound2 > maxRound1) {
                                    if (userId2 == getUserId(getToken())) {
                                        opponentsTurnListView.getItems().add(game);
                                    } else {
                                        makeMoveListView.getItems().add(game);
                                    }
                                } else if (maxRound1 > maxRound2){
                                    if (userId1 == getUserId(getToken())) {
                                        opponentsTurnListView.getItems().add(game);
                                    } else {
                                        makeMoveListView.getItems().add(game);
                                    }
                                } else {
                                    makeMoveListView.getItems().add(game);
                                }
                            } else if (userId1 == getUserId(getToken())) {
                                // Bara en spelare har gjort drag hittills i matchen. Måste kolla om det är den inloggade spelaren eller motståndaren
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
            if (makeMoveListView.getItems().size() > 0) {
                makeMoveLabel.setVisible(true);
                makeMoveListView.setVisible(true);
            }
            if(opponentsTurnListView.getItems().size() > 0) {
                opponentsTurnLabel.setVisible(true);
                opponentsTurnListView.setVisible(true);
            }
            if(finishedGamesListView.getItems().size() > 0) {
                finishedGamesLabel.setVisible(true);
                finishedGamesListView.setVisible(true);
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
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


    }

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

    @FXML
    public void openGame(MouseEvent event) {

        ListView listView = (ListView) event.getSource();
        if (listView.getSelectionModel().getSelectedItem() != null) {
            Game game =  (Game) listView.getSelectionModel().getSelectedItem();
            getScreenController().setWindow(ScreenController.GAME, getToken(), game);
        }
    }

    @FXML
    public void openFinishedGame() {
        if (finishedGamesListView.getSelectionModel().getSelectedItem() != null) {
            Game game = finishedGamesListView.getSelectionModel().getSelectedItem();
            getScreenController().setWindow(ScreenController.WINNER, getToken(), game);
        }
    }

    public void newGameButtonClicked() {
        getScreenController().setWindow(ScreenController.NEW_GAME, getToken());
    }

    public void helpButtonClicked() {
        getScreenController().setPreviousPage(ScreenController.ACTIVE_GAMES);
        getScreenController().setWindow(ScreenController.RULES, getToken());
    }

    public void logOutButtonClicked() {
        getScreenController().setWindow(ScreenController.LOGIN, getToken());
    }

    @FXML
    public void showFriendRequests() {
        getScreenController().setWindow(ScreenController.NEW_GAME, getToken());
    }
}
