package com.example.rps.controllers;

import com.example.rps.models.Game;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//Christian har skrivit den här klassen
public class WinnerWindow extends Window {

    @FXML
    private Label labelGameResultWinnerWindow;
    @FXML
    private Label labelShowTotalNumberOfWinningsForActivePlayer;
    @FXML
    private Label labelShowTotalNumberOfLossesForActivePlayer;
    @FXML
    private Label labelHeaderWinnerWindow;
    @FXML
    private Label labelHeaderLossesWindow;
    @FXML
    private ImageView imageView;

    private int numberOfWinningsActivePlayer;
    private int numberOfLossesActivePlayer;
    private int userIdPlayer1 = 0;
    private int userIdPlayer2 = 0;
    private int userIdCpuPlayer = 0;
    private String numberOfWinningsString = "";
    private String numberOfLossesString = "";

    PreparedStatement getVictories = null;
    ResultSet results = null;

    Game game;

    public void initGame(Game game) throws SQLException {
        this.game = game;
        setUpWinnerWindow();
    }

    public void setUpWinnerWindow() throws SQLException {
        Image loserImage = new Image("http://bestanimations.com/Signs&Shapes/Hearts/brokenhearts/broken-heart-red-skull-animated-gif.gif#.XoXzLilMr3Q.link");
        this.labelHeaderWinnerWindow.setVisible(false);
        this.labelHeaderLossesWindow.setVisible(false);
        this.labelShowTotalNumberOfWinningsForActivePlayer.setVisible(false);
        this.labelShowTotalNumberOfLossesForActivePlayer.setVisible(false);
        this.userIdPlayer1 = game.getPlayer1().getUserId();
        this.userIdPlayer2 = game.getPlayer2().getUserId();
        this.userIdCpuPlayer = GameWindow.USER_ID_FOR_CPU_PLAYER;
        this.numberOfWinningsString = String.valueOf(numberOfWinningsString);
        this.numberOfLossesString = String.valueOf(numberOfLossesString);
        this.showResultFromPreviousGame();
        this.showTotalNumberOfWinningsForActivePlayer();
        this.showTotalNumberOfLossesForActivePlayer();
        this.labelGameResultWinnerWindow.setText("Game results: " + game.getScorePlayer1() + " - " + game.getScorePlayer2());
        this.labelShowTotalNumberOfWinningsForActivePlayer.setText(numberOfWinningsString);
        this.labelShowTotalNumberOfLossesForActivePlayer.setText(numberOfLossesString);

        labelHeaderWinnerWindow.setText("Congratulations, you won!");
        if(getUserId(getToken()) == userIdPlayer1) {
            if (game.getScorePlayer2() > game.getScorePlayer1()) {
                this.labelHeaderWinnerWindow.setText("Sorry, you lost");
                imageView.setImage(loserImage);
            }
        } else if (game.getScorePlayer1() > game.getScorePlayer2()) {
            this.labelHeaderWinnerWindow.setText("Sorry, you lost");
            imageView.setImage(loserImage);
        }
        labelHeaderWinnerWindow.setVisible(true);

    }

    public void playAgainButtonClicked() {

        if (userIdPlayer1 == getUserId(getToken()) && userIdPlayer2 != GameWindow.USER_ID_FOR_CPU_PLAYER) {
            PreparedStatement inviteFriendStatement = null;

            try {
                inviteFriendStatement = getConnection().prepareStatement("INSERT INTO invitations VALUES (?, ?);");
                inviteFriendStatement.setInt(1,userIdPlayer1);
                inviteFriendStatement.setInt(2,userIdPlayer2);
                inviteFriendStatement.executeUpdate();

                getScreenController().setWindow(ScreenController.ACTIVE_GAMES, getToken());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if(inviteFriendStatement != null) {
                        inviteFriendStatement.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else if (userIdPlayer1 != getUserId(getToken())) {
            PreparedStatement inviteFriendStatement = null;

            try {
                inviteFriendStatement = getConnection().prepareStatement("INSERT INTO invitations VALUES (?, ?);");
                inviteFriendStatement.setInt(1,userIdPlayer2);
                inviteFriendStatement.setInt(2,userIdPlayer1);
                inviteFriendStatement.executeUpdate();

                getScreenController().setWindow(ScreenController.ACTIVE_GAMES, getToken());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if(inviteFriendStatement != null) {
                        inviteFriendStatement.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        if (userIdPlayer2 == GameWindow.USER_ID_FOR_CPU_PLAYER) {
            PreparedStatement startGameAgainstCpuStatement = null;

            try {
                startGameAgainstCpuStatement = getConnection().prepareStatement("INSERT INTO matches (player1, player2)" +
                        "VALUES (?, ?);");
                startGameAgainstCpuStatement.setInt(1, userIdPlayer1);
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
    }

    public void activeGamesButtonClicked() {
        getScreenController().setWindow(ScreenController.ACTIVE_GAMES, getToken());
    }

    public void showResultFromPreviousGame() {
        labelGameResultWinnerWindow.setText("Game results: " + game.getScorePlayer1() + " - " + game.getScorePlayer2());
    }

    private int showTotalNumberOfWinningsForActivePlayer() throws SQLException {

        if (getUserId(getToken()) == game.getPlayer1().getUserId()) {
            getVictories = getConnection().prepareStatement("SELECT victories from friends WHERE player1 = ? AND player2 = ?");
            getVictories.setInt(1, userIdPlayer1);
            getVictories.setInt(2, userIdPlayer2);
            results = getVictories.executeQuery();

            if (results.next()) {
                numberOfWinningsActivePlayer = results.getInt(1);
                numberOfWinningsString = String.valueOf(numberOfWinningsActivePlayer);
                labelShowTotalNumberOfWinningsForActivePlayer.setText(numberOfWinningsString);
                labelShowTotalNumberOfWinningsForActivePlayer.setVisible(true);
            }
        } else if (getUserId(getToken()) == game.getPlayer2().getUserId()) {
            getVictories = getConnection().prepareStatement("SELECT victories from friends WHERE player1 = ? AND player2 = ?");
            getVictories.setInt(1,userIdPlayer2);
            getVictories.setInt(2,userIdPlayer1);
            results = getVictories.executeQuery();

            if (results.next()) {
                numberOfWinningsActivePlayer = results.getInt(1);
                numberOfWinningsString = String.valueOf(numberOfWinningsActivePlayer);
                labelShowTotalNumberOfWinningsForActivePlayer.setText(numberOfWinningsString);
                labelShowTotalNumberOfWinningsForActivePlayer.setVisible(true);
            }
        }

        return numberOfWinningsActivePlayer;
    }

    private int showTotalNumberOfLossesForActivePlayer() throws SQLException {

        if (getUserId(getToken()) == game.getPlayer1().getUserId()) {
            getVictories = getConnection().prepareStatement("SELECT victories from friends WHERE player1 = ? AND player2 = ?");
            getVictories.setInt(1,userIdPlayer2);
            getVictories.setInt(2,userIdPlayer1);
            results = getVictories.executeQuery();

            if (results.next()) {
                numberOfLossesActivePlayer = results.getInt(1);
                numberOfLossesString = String.valueOf(numberOfLossesActivePlayer);
                labelShowTotalNumberOfLossesForActivePlayer.setText(numberOfLossesString);
                labelShowTotalNumberOfLossesForActivePlayer.setVisible(true);
            }
        }

        if (getUserId(getToken()) == game.getPlayer2().getUserId()) {
            getVictories = getConnection().prepareStatement("SELECT victories from friends WHERE player1 = ? AND player2 = ?");
            getVictories.setInt(1,userIdPlayer1);
            getVictories.setInt(2,userIdPlayer2);
            results = getVictories.executeQuery();

            if (results.next()) {
                numberOfLossesActivePlayer = results.getInt(1);
                numberOfLossesString = String.valueOf(numberOfLossesActivePlayer);
                labelShowTotalNumberOfLossesForActivePlayer.setText(numberOfLossesString);
                labelShowTotalNumberOfLossesForActivePlayer.setVisible(true);
            }
        }

        return numberOfLossesActivePlayer;
    }
}
