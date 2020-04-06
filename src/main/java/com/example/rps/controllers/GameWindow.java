package com.example.rps.controllers;

import com.example.rps.models.Game;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Random;

//Karin har skrivit den här klassen
public class GameWindow extends Window {

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
    @FXML
    private ScrollPane scrollPane;

    public static final int USER_ID_FOR_CPU_PLAYER = 3;

    private Game game = null;
    private boolean isPlayer1 = false;

    public void initGame(Game game) {
        this.game = game;
        if(getUserId(getToken()) == game.getPlayer1().getUserId()) {
            isPlayer1 = true;
        } else if (getUserId(getToken()) == game.getPlayer2().getUserId()) {
            isPlayer1 = false;
        } else {
            getScreenController().setWindow(ScreenController.ACTIVE_GAMES, getToken());
        }
        setUpGameWindow();
    }

    @FXML
    private void setUpGameWindow() {
        player1Vbox.getChildren().clear();
        player2Vbox.getChildren().clear();
        player1Label.setText(game.getPlayer1().getUserName());
        player2Label.setText(game.getPlayer2().getUserName());
        int scorePlayer1 = 0;
        int scorePlayer2 = 0;
        int completedRounds = 0;
        int maxRound = 0;
        scrollPane.setVvalue(1.0);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        rock.setDisable(false);
        scissors.setDisable(false);
        paper.setDisable(false);
        PreparedStatement getMoves = null;
        try {
            getMoves = getConnection().prepareStatement("SELECT * FROM moves WHERE match_id = ? ORDER BY round_no");
            getMoves.setInt(1, game.getGameID());
            ResultSet results = getMoves.executeQuery();
            int[] movesPlayer1 = new int[5];
            int[] movesPlayer2 = new int[5];

            while(results.next()) {

                int move = results.getInt("value");
                String text = "";
                switch (move) {
                    case Game.ROCK:
                        text = "Rock";
                        break;
                    case Game.SCISSORS:
                        text = "Scissors";
                        break;
                    case  Game.PAPER:
                        text = "Paper";
                }

                Label tempLabel = new Label(text);
                int playerId = results.getInt("user_id");
                int roundNumber = results.getInt("round_no");
                if(roundNumber >= movesPlayer1.length) {
                    int[] temp = new int[movesPlayer1.length * 2];
                    System.arraycopy(movesPlayer1, 0, temp, 0, movesPlayer1.length);
                    movesPlayer1 = temp;
                }
                if(roundNumber >= movesPlayer2.length) {
                    int[] temp = new int[movesPlayer2.length * 2];
                    System.arraycopy(movesPlayer2, 0, temp, 0, movesPlayer2.length);
                    movesPlayer2 = temp;
                }

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
                    player1Vbox.getChildren().get(i-1).setStyle("-fx-text-fill: #ED9011;");
                } else if (roundWinner == Game.PLAYER2_WINS) {
                    scorePlayer2++;
                    player2Vbox.getChildren().get(i-1).setStyle("-fx-text-fill: #ED9011;");
                }
            }
            game.setScorePlayer1(scorePlayer1);
            game.setScorePlayer2(scorePlayer2);

            if((scorePlayer1 > 4) || (scorePlayer2 > 4)) {
                int winner = scorePlayer1 > scorePlayer2 ? game.getPlayer1().getUserId() : game.getPlayer2().getUserId();
                int loser = winner == game.getPlayer1().getUserId() ? game.getPlayer2().getUserId() : game.getPlayer1().getUserId();
                endGame(winner, loser);
            } else {
                resultLabel.setText(scorePlayer1 + " - " + scorePlayer2);
                if(player1Vbox.getChildren().size() != player2Vbox.getChildren().size()) {
                    if (isPlayer1) {
                        if(player2Vbox.getChildren().size() > player1Vbox.getChildren().size()) {
                            player2Vbox.getChildren().get(player2Vbox.getChildren().size()-1).setVisible(false);
                        } else {
                            rock.setDisable(true);
                            scissors.setDisable(true);
                            paper.setDisable(true);
                        }

                    } else {
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
    private void handleChoiceButtons(ActionEvent actionEvent) {

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
        PreparedStatement checkIfOpponentHasMadeMove = null;
        ResultSet results = null;
        PreparedStatement insertRoundWinner = null;
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(false);

            insertMove = conn.prepareStatement("INSERT INTO moves(match_id, user_id, round_no, value) VALUES (?, ?, ?, ?)");
            insertMove.setInt(1, game.getGameID());
            insertMove.setInt(2, playerId);
            insertMove.setInt(3, round);
            insertMove.setInt(4, move);

            insertMove.executeUpdate();

            int userIdForOpponent = playerId == game.getPlayer2().getUserId() ? game.getPlayer1().getUserId() : game.getPlayer2().getUserId();


            checkIfOpponentHasMadeMove = conn.prepareStatement("SELECT * FROM moves WHERE match_id = ? AND user_id = ? AND round_no = ?; ");
            checkIfOpponentHasMadeMove.setInt(1, game.getGameID());
            checkIfOpponentHasMadeMove.setInt(2, userIdForOpponent);
            checkIfOpponentHasMadeMove.setInt(3, round);

            results = checkIfOpponentHasMadeMove.executeQuery();
            if(results.next()) {
                int roundWinner;
                int winnerId = 0;
                if(playerId == game.getPlayer1().getUserId()) {
                    roundWinner = game.compareChoices(move, results.getInt("value"));
                } else {
                    roundWinner = game.compareChoices(results.getInt("value"), move);
                }
                if (roundWinner == Game.PLAYER1_WINS) {
                    game.increaseScorePlayer1();
                    winnerId = game.getPlayer1().getUserId();
                } else if (roundWinner == Game.PLAYER2_WINS) {
                    game.increaseScorePlayer2();
                    winnerId = game.getPlayer2().getUserId();
                }

                if (roundWinner > 0) {
                    insertRoundWinner = conn.prepareStatement("INSERT INTO rounds VALUES (?, ?, ?);");

                    insertRoundWinner.setInt(1, game.getGameID());
                    insertRoundWinner.setInt(2, round);
                    insertRoundWinner.setInt(3, winnerId);

                    insertRoundWinner.executeUpdate();
                } else {
                    insertRoundWinner = conn.prepareStatement("INSERT INTO rounds (match_id, round_no) VALUES (?, ?);");

                    insertRoundWinner.setInt(1, game.getGameID());
                    insertRoundWinner.setInt(2, round);

                    insertRoundWinner.executeUpdate();
                }
            }

            conn.commit();
            conn.setAutoCommit(true);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (insertMove != null) {
                    insertMove.close();
                }
                if (checkIfOpponentHasMadeMove != null) {
                    checkIfOpponentHasMadeMove.close();
                }
                if (insertRoundWinner != null) {
                    insertRoundWinner.close();
                }
                if (results != null) {
                    results.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private int getLastRoundNumber(int playerID) {
        PreparedStatement getMaxRoundNumber = null;
        ResultSet results = null;

        int maxRoundNumber = 0;
        try {
            getMaxRoundNumber = getConnection().prepareStatement("SELECT MAX(round_no) FROM moves WHERE match_id = ? AND user_id = ?");
            getMaxRoundNumber.setInt(1, game.getGameID());
            getMaxRoundNumber.setInt(2, playerID);

            results = getMaxRoundNumber.executeQuery();
            if (results.next()) {
                maxRoundNumber = results.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try  {
                if (getMaxRoundNumber != null){
                    getMaxRoundNumber.close();
                }
                if (results  != null) {
                    results.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
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
        PreparedStatement getWinner = null;
        ResultSet winnerResults = null;
        PreparedStatement insertWinner = null;
        PreparedStatement getNumberOfVictories = null;
        PreparedStatement incrementVictories = null;
        ResultSet results = null;
        int victories;
        try {
            Connection conn = getConnection();
            conn.setAutoCommit(false);
            getWinner = conn.prepareStatement("SELECT winner FROM matches WHERE id = ?");
            getWinner.setInt(1, game.getGameID());

            winnerResults = getWinner.executeQuery();
            if (winnerResults.next()) {
                if (winnerResults.getInt(1) == 0) {
                    insertWinner = conn.prepareStatement("UPDATE matches SET winner = ? WHERE id = ?");
                    insertWinner.setInt(1, winner);
                    insertWinner.setInt(2, game.getGameID());
                    insertWinner.executeUpdate();

                    getNumberOfVictories = conn.prepareStatement("SELECT victories FROM friends WHERE player1 = ? " +
                            "AND player2 = ?");
                    getNumberOfVictories.setInt(1, winner);
                    getNumberOfVictories.setInt(2, loser);

                    results = getNumberOfVictories.executeQuery();
                    if (results.next()) {
                        victories = results.getInt(1);
                        victories += 1;

                        incrementVictories = conn.prepareStatement("UPDATE friends SET victories = ? " +
                                "WHERE player1 = ? AND player2 = ?");
                        incrementVictories.setInt(1, victories);
                        incrementVictories.setInt(2, winner);
                        incrementVictories.setInt(3, loser);

                        incrementVictories.executeUpdate();
                    }
                }
            }

            conn.commit();
            conn.setAutoCommit(true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (getWinner != null) {
                    getWinner.close();
                }
                if (winnerResults != null) {
                    winnerResults.close();
                }
                if (insertWinner != null) {
                    insertWinner.close();
                }
                if (getNumberOfVictories != null) {
                    getNumberOfVictories.close();
                }
                if (incrementVictories != null) {
                    incrementVictories.close();
                }
                if (results != null) {
                    results.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        getScreenController().setWindow(ScreenController.WINNER, getToken(), game);
    }

    @FXML
    private void backButtonClicked() {
        getScreenController().setWindow(ScreenController.ACTIVE_GAMES, getToken());
    }

    @FXML
    public void helpButtonClicked() {
        getScreenController().setWindow(ScreenController.RULES, getToken(), game);
    }
}
