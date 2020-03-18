package com.example.rps.controllers;

import com.example.rps.models.Game;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;


import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GameWindow extends Window {

    @FXML
    private GridPane gameGridPane;
    @FXML
    private Label player1Label;
    @FXML
    private Label player2Label;
    @FXML
    private Label resultLabel;
    private Game game = null;
    private int rounds = 0;
    private int lastUsedRowNumber = 1;

    public void initGame(Game game) {
        this.game = game;
        setUpGameWindow();
    }


    private void setUpGameWindow() {
        player1Label.setText(game.getPlayer1().getUserName());
        player2Label.setText(game.getPlayer2().getUserName());
        PreparedStatement getMoves = null;
        try {
            getMoves = getConnection().prepareStatement("SELECT * FROM moves WHERE match_id = ?");
            getMoves.setInt(1, game.getGameID());
            ResultSet results = getMoves.executeQuery();

            lastUsedRowNumber = 1;

            while(results.next()) {
                int move = results.getInt("value");
                String text = "";
                switch (move) {
                    case Game.ROCK:
                        text = "Sten";
                        break;
                    case Game.SCISSORS:
                        text = "Sax";
                        break;
                    case  Game.PAPER:
                        text = "P" + (char)229 + "se";
                }

                Label tempLabel = new Label(text);
                int playerId = results.getInt("user_id");
                int roundNumber = results.getInt("round_no");
                if (roundNumber > rounds) {
                    rounds = roundNumber;
                }

                if (playerId == game.getPlayer1().getUserId()) {
                    String labelId = "round" + roundNumber + "player1";
                    tempLabel.setId(labelId);
                    gameGridPane.add(tempLabel, 1, lastUsedRowNumber + roundNumber);
                } else if (playerId == game.getPlayer2().getUserId()) {
                    String labelId = "round" + roundNumber + "player2";
                    tempLabel.setId(labelId);
                    gameGridPane.add(tempLabel, 3, lastUsedRowNumber + roundNumber);
                }

            }
            lastUsedRowNumber += rounds;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(getMoves != null) {
                try {
                    getMoves.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @FXML
    public void handleChoiceButtons(ActionEvent actionEvent) {


        Button button = (Button) actionEvent.getSource();
        String buttonId = button.getId();
        Label label = new Label();
        label.setText(buttonId);

        int playerId = getUserId(getToken());

        int round = getLastRoundNumber(playerId) + 1;

        if (playerId == game.getPlayer1().getUserId()) {
            String labelId = "round" + round + "player1";
            label.setId(labelId);
            gameGridPane.add(label, 1, lastUsedRowNumber + 1);

        } else if (playerId == game.getPlayer2().getUserId()) {
            String labelId = "round" + round + "player2";
            label.setId(labelId);
            gameGridPane.add(label, 3, lastUsedRowNumber + 1);
        }
        int choice = 0;
        switch(buttonId) {
            case "sten":
                choice = Game.ROCK;
                break;
            case "sax":
                choice = Game.SCISSORS;
                break;
            case "p" + (char)229 + "se":
                choice = Game.PAPER;
        }
        addMoveToDatabase(playerId, choice, round);
    }

  /*  @FXML
    public void handleRockButtonAction() {

        Label label = new Label("Sten");
        int playerId = getUserId(getToken());
        int round = getLastRoundNumber(playerId) + 1;

        if (playerId == game.getPlayer1().getUserId()) {
            String labelId = "round" + round + "player1";
            label.setId(labelId);
            gameGridPane.add(label, 1, lastUsedRowNumber + 1);

        } else if (playerId == game.getPlayer2().getUserId()) {
            String labelId = "round" + round + "player2";
            label.setId(labelId);
            gameGridPane.add(label, 3, lastUsedRowNumber + 1);
        }
        addMoveToDatabase(playerId, Game.ROCK, round);

    }

    @FXML
    public void handleScissorsButtonAction() {

        Label label = new Label("Sax");
        int playerId = getUserId(getToken());

        if (playerId == game.getPlayer1().getUserId()) {
            gameGridPane.add(label, 1, lastUsedRowNumber + 1);

        } else if (playerId == game.getPlayer2().getUserId()) {
            gameGridPane.add(label, 3, lastUsedRowNumber + 1);
        }
        addMoveToDatabase(playerId, Game.SCISSORS, getLastRoundNumber(playerId) + 1);
    }

    @FXML
    public void handlePaperButtonAction() {

        Label label = new Label("P" + (char)229 + "se");
        int playerId = getUserId(getToken());

        if (playerId == game.getPlayer1().getUserId()) {
            gameGridPane.add(label, 1, lastUsedRowNumber + 1);

        } else if (playerId == game.getPlayer2().getUserId()) {
            gameGridPane.add(label, 3, lastUsedRowNumber + 1);
        }
        addMoveToDatabase(playerId, Game.PAPER, getLastRoundNumber(playerId) + 1);
    }*/



    private int getUserId(String token) {
        PreparedStatement getUserId = null;
        Connection conn = getConnection();
        int userId = -1;
        try {
            getUserId = conn.prepareStatement("SELECT user_id FROM tokens WHERE value = ?");
            getUserId.setString(1, token);
            ResultSet results = getUserId.executeQuery();
            if (results.next()) {
                userId = results.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (getUserId != null) {
                try {
                    getUserId.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return userId;
    }

    private void addMoveToDatabase (int playerId, int move, int round) {
        PreparedStatement insertMove = null;
        try {
            insertMove = getConnection().prepareStatement("INSERT INTO moves(match_id, user_id, round_no, value) VALUES (?, ?, ?, ?)");
            insertMove.setInt(1, game.getGameID());
            insertMove.setInt(2, playerId);
            insertMove.setInt(3, round);
            insertMove.setInt(4, move);

            insertMove.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (insertMove != null) {
                try {
                    insertMove.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private int getLastRoundNumber(int playerID) {
        PreparedStatement getMaxRoundNumber = null;
        int maxRoundNumber = 0;
        try {
            getMaxRoundNumber = getConnection().prepareStatement("SELECT MAX(round_no) FROM moves WHERE user_id = ?");
            getMaxRoundNumber.setInt(1, playerID);

            ResultSet results = getMaxRoundNumber.executeQuery();
            if (results.next()) {
                maxRoundNumber = results.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (getMaxRoundNumber != null) {
                try {
                    getMaxRoundNumber.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return maxRoundNumber;
    }

    private void finishRound(){
        int roundNumber = getLastRoundNumber(getUserId(getToken()));
        int choicePlayer1 = 0;
        int choicePlayer2 = 0;
        PreparedStatement getChoice = null;
        try {
            getChoice = getConnection().prepareStatement("SELECT value FROM moves WHERE user_id = ? AND round_no = ?");
            getChoice.setInt(1, game.getPlayer1().getUserId());
            getChoice.setInt(2, roundNumber);

            ResultSet results = getChoice.executeQuery();
            if (results.next()) {
                choicePlayer1 = results.getInt(1);
            }

            getChoice.setInt(1, game.getPlayer2().getUserId());
            results = getChoice.executeQuery();
            if (results.next()) {
                choicePlayer2 = results.getInt(1);
            }
            int winner = game.compareChoices(choicePlayer1, choicePlayer2);
            game.setRoundWinners(roundNumber, winner);

            if (winner != Game.DRAW){
                String labelId = "";
                if (winner == Game.PLAYER1_WINS) {
                    labelId = "round" + roundNumber + "player1";
                } else if (winner == Game.PLAYER2_WINS) {
                    labelId = "round" + roundNumber + "player2";
                }

               // int labelIndex = gameGridPane.getChildren().indexOf();
                //Label winningLabel = (Label)gameGridPane.getChildren().get(labelIndex);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (getChoice != null) {
                try {
                    getChoice.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}
