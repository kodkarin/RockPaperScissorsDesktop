package com.example.rps.controllers;

import com.example.rps.models.Game;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import java.awt.event.ActionEvent;
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

            int lastUsedRowNumber = 1;

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
                    gameGridPane.add(tempLabel, 1, lastUsedRowNumber + roundNumber);

                } else if (playerId == game.getPlayer2().getUserId()) {
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
    public void handleRockButtonAction() {

        Label label = new Label("Sten");
        int playerId = getUserId(getToken());

        if (playerId == game.getPlayer1().getUserId()) {
            gameGridPane.add(label, 1, lastUsedRowNumber + 1);

        } else if (playerId == game.getPlayer2().getUserId()) {
            gameGridPane.add(label, 3, lastUsedRowNumber + 1);
        }
        makeMove(playerId, Game.ROCK, getNextRoundNumber(playerId));

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
        makeMove(playerId, Game.SCISSORS, getNextRoundNumber(playerId));
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
        makeMove(playerId, Game.PAPER, getNextRoundNumber(playerId));
    }



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

    private void makeMove(int playerId, int move, int round) {
        PreparedStatement insertMove = null;
        try {
            insertMove = getConnection().prepareStatement("INSERT INTO moves(match_id, user_id, round_no, value) VALUES (?, ?, ?, ?");
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

    private int getNextRoundNumber(int playerID) {
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
        return maxRoundNumber + 1;
    }
}
