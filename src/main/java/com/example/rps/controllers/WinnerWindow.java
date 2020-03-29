package com.example.rps.controllers;

import com.example.rps.models.Game;
import com.example.rps.models.Player;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class WinnerWindow extends Window {

    @FXML
    private Label labelGameResultWinnerWindow;
    @FXML
    private Label labelShowTotalNumberOfWinningsForActivePlayer;
    @FXML
    private Label labelShowTotalNumberOfLossesForActivePlayer;
    @FXML
    private ImageView imageView;

    private int numberOfWinningsActivePlayer;
    private int numberOfLossesActivePlayer;
    private int userIdPlayer1 = 0;
    private int userIdPlayer2 = 0;

    PreparedStatement getVictories = null;
    ResultSet results = null;

    Game game;

    public WinnerWindow() {
        this.userIdPlayer1 = game.getPlayer1().getUserId();
        this.userIdPlayer2 = game.getPlayer2().getUserId();
    }

    public void playAgainButtonClicked() {
        PreparedStatement inviteFriendStatement = null;

        try {
            inviteFriendStatement = getConnection().prepareStatement("INSERT INTO invitations VALUES (?, ?);");
            inviteFriendStatement.setInt(userIdPlayer1,getUserId(getToken()));
            inviteFriendStatement.setInt(userIdPlayer2,getUserId(getToken()));
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


    public void activeGamesButtonClicked() {
        getScreenController().setWindow(ScreenController.ACTIVE_GAMES, "");
    }

    public void initGame(Game game) {}

    public void showResultFromPreviousGame() {
        labelGameResultWinnerWindow.setText(game.getScorePlayer1() + " - " + game.getScorePlayer2());
    }

    @FXML
    private int showTotalNumberOfWinningsForActivePlayer(ActionEvent event) throws SQLException {

        if (userIdPlayer1 == game.getPlayer1().getUserId()) {
            getVictories = getConnection().prepareStatement("SELECT victories from friends WHERE player1 = ? AND player2 = ?");
            getVictories.setInt(1,userIdPlayer1);
            getVictories.setInt(2,userIdPlayer2);
            results = getVictories.executeQuery();

            if (results.next()) {
                numberOfWinningsActivePlayer = results.getInt(userIdPlayer1);
            }
        } else if (userIdPlayer2 == game.getPlayer2().getUserId()) {
            getVictories = getConnection().prepareStatement("SELECT victories from friends WHERE player1 = ? AND player2 = ?");
            getVictories.setInt(1,userIdPlayer2);
            getVictories.setInt(2,userIdPlayer1);
            results = getVictories.executeQuery();

            if (results.next()) {
                numberOfWinningsActivePlayer = results.getInt(userIdPlayer2);
            }
        }

        return numberOfWinningsActivePlayer;
    }

    @FXML
    private int showTotalNumberOfLossesForActivePlayer(ActionEvent event) throws SQLException {

        if (userIdPlayer1 == game.getPlayer1().getUserId()) {
            getVictories = getConnection().prepareStatement("SELECT victories from friends WHERE player1 = ? AND player2 = ?");
            getVictories.setInt(1,userIdPlayer2);
            getVictories.setInt(2,userIdPlayer1);
            results = getVictories.executeQuery();

            if (results.next()) {
                numberOfLossesActivePlayer = results.getInt(userIdPlayer1);
            }
        }

        if (userIdPlayer2 == game.getPlayer2().getUserId()) {
            getVictories = getConnection().prepareStatement("SELECT victories from friends WHERE player1 = ? AND player2 = ?");
            getVictories.setInt(1,userIdPlayer2);
            getVictories.setInt(2,userIdPlayer1);
            results = getVictories.executeQuery();

            if (results.next()) {
                numberOfLossesActivePlayer = results.getInt(userIdPlayer2);
            }
        }

        return numberOfLossesActivePlayer;
    }
}
