package com.example.rps.controllers;

import com.example.rps.models.Game;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;


import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;

public class GameWindow extends Window {

    @FXML
    private GridPane gameGridPane;
    @FXML
    private VBox player1Vbox;
    @FXML
    private VBox player2Vbox;
    @FXML
    private Label player1Label;
    @FXML
    private Label player2Label;
    @FXML
    private Button rock;
    @FXML
    private Button scissors;
    @FXML
    private Button paper;
    @FXML
    private Label resultLabel;

    public static final int USER_ID_FOR_CPU_PLAYER = 3;

    private Game game = null;
    private int completedRounds = 0;
    private int scorePlayer1 = 0;
    private int scorePlayer2 = 0;

    public void initGame(Game game) {
        this.game = game;
        setUpGameWindow();
    }


    private void setUpGameWindow() {
        player1Label.setText(game.getPlayer1().getUserName());
        player2Label.setText(game.getPlayer2().getUserName());
        scorePlayer1 = 0;
        scorePlayer2 = 0;
        completedRounds = 0;
        int maxRound = 0;

        rock.setDisable(false);
        scissors.setDisable(false);
        paper.setDisable(false);
        PreparedStatement getMoves = null;
        try {
            getMoves = getConnection().prepareStatement("SELECT * FROM moves WHERE match_id = ? ORDER BY round_no");
            getMoves.setInt(1, game.getGameID());
            ResultSet results = getMoves.executeQuery();
            int[] movesPlayer1 = new int[30];
            int[] movesPlayer2 = new int[30];

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
                if (roundNumber > maxRound) {
                    maxRound = roundNumber;
                } else if (roundNumber == maxRound) {
                    completedRounds = roundNumber;
                }

                if (playerId == game.getPlayer1().getUserId()) {
                    movesPlayer1[roundNumber] = move;
                    player1Vbox.getChildren().add(tempLabel);
                } else if (playerId == game.getPlayer2().getUserId()) {
                    movesPlayer2[roundNumber] = move;
                    player2Vbox.getChildren().add(tempLabel);
                }
            }
            for (int i = 1; i <= completedRounds; i++) {
                int roundWinner = game.compareChoices(movesPlayer1[i], movesPlayer2[i]);
                if (roundWinner == Game.PLAYER1_WINS) {
                    scorePlayer1++;
                    player1Vbox.getChildren().get(i-1).setStyle("-fx-border-color: black;");
                    player1Vbox.getChildren().get(i-1).setStyle("-fx-border-width: 2;");
                    player1Vbox.getChildren().get(i-1).setStyle("-fx-border-style: solid;");
                } else if (roundWinner == Game.PLAYER2_WINS) {
                    scorePlayer2++;
                    player2Vbox.getChildren().get(i-1).setStyle("-fx-border-color: black;");
                    player2Vbox.getChildren().get(i-1).setStyle("-fx-border-width: 2;");
                    player2Vbox.getChildren().get(i-1).setStyle("-fx-border-style: solid;");
                }
            }
            if((scorePlayer1 > 4) || (scorePlayer2 > 4)) {
                int winner = scorePlayer1 > scorePlayer2 ? game.getPlayer1().getUserId() : game.getPlayer2().getUserId();
                int loser = winner == game.getPlayer1().getUserId() ? game.getPlayer2().getUserId() : game.getPlayer1().getUserId();
                endGame(winner, loser);
            } else {
                resultLabel.setText(scorePlayer1 + " - " + scorePlayer2);
                System.out.println("Player1Vbox.size: " + player1Vbox.getChildren().size());
                System.out.println("Player2Vbox.size: " + player2Vbox.getChildren().size());
                if(player1Vbox.getChildren().size() != player2Vbox.getChildren().size()) {
                    if (getUserId(getToken()) == game.getPlayer1().getUserId()) {
                        if(player2Vbox.getChildren().size() > player1Vbox.getChildren().size()) {
                            player2Vbox.getChildren().get(player2Vbox.getChildren().size()-1).setVisible(false);
                        } else {
                            rock.setDisable(true);
                            scissors.setDisable(true);
                            paper.setDisable(true);
                        }

                    } else if (getUserId(getToken()) == game.getPlayer2().getUserId()) {
                        if (player1Vbox.getChildren().size() > player2Vbox.getChildren().size()) {
                            player1Vbox.getChildren().get(player1Vbox.getChildren().size() - 1).setVisible(false);
                        } else {
                            rock.setDisable(true);
                            scissors.setDisable(true);
                            paper.setDisable(true);
                        }
                    }
                }
            }

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
        int choice = 0;

        switch(buttonId) {
            case "rock":
                choice = Game.ROCK;
                break;
            case "scissors":
                choice = Game.SCISSORS;
                break;
            case "paper":
                choice = Game.PAPER;
        }
        int playerId = getUserId(getToken());
        int round = getLastRoundNumber(playerId) + 1;
        addMoveToDatabase(playerId, choice, round);
        if (game.getPlayer2().getUserId() == USER_ID_FOR_CPU_PLAYER) {
            makeMoveForCpuPlayer();
        }
        player1Vbox.getChildren().clear();
        player2Vbox.getChildren().clear();
        setUpGameWindow();
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

    private void makeMoveForCpuPlayer() {
        Random random = new Random();
        int cpuChoice = random.nextInt(3) + 1;
        int roundNumber = getLastRoundNumber(USER_ID_FOR_CPU_PLAYER) + 1;
        addMoveToDatabase(USER_ID_FOR_CPU_PLAYER, cpuChoice, roundNumber);
    }

    private void endGame(int winner, int loser) {
        PreparedStatement insertWinner = null;
        PreparedStatement getNumberOfVictories = null;
        PreparedStatement incrementVictories = null;
        int victories = 0;
        try {
            insertWinner = getConnection().prepareStatement("INSERT INTO matches (winner) VALUES (?) WHERE id = ?");
            insertWinner.setInt(1, winner);
            insertWinner.setInt(2, game.getGameID());
            insertWinner.executeUpdate();

            getNumberOfVictories = getConnection().prepareStatement("SELECT victories FROM friends WHERE player1 = ? " +
                    "AND player2 = ?");
            getNumberOfVictories.setInt(1, winner);
            getNumberOfVictories.setInt(2, loser);

            ResultSet results = getNumberOfVictories.executeQuery();
            if (results.next()) {
                victories = results.getInt(1);
                victories += 1;

                incrementVictories = getConnection().prepareStatement("UPDATE friends SET victories = ? " +
                        "WHERE player1 = ? AND player2 = ?");
                incrementVictories.setInt(1, victories);
                incrementVictories.setInt(2, winner);
                incrementVictories.setInt(3, loser);

                incrementVictories.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (insertWinner != null) {
                try {
                    insertWinner.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (getNumberOfVictories != null) {
                try {
                    getNumberOfVictories.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (incrementVictories != null) {
                try {
                    incrementVictories.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        getScreenController().setWindow(ScreenController.WINNER, getToken(), game);
    }

    @FXML
    public void helpButtonClicked() {
        getScreenController().setWindow(ScreenController.RULES, getToken());
        super.saveActiveGame = game;
    }

}
