package com.example.rps.controllers;

import com.example.rps.models.Game;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.sql.SQLException;


public class WinnerWindow extends Window {

    @FXML
    private Label gameResultWinnerWindow;
    @FXML
    private Label numberOfWinningsActivePlayer;
    @FXML
    private Label numberOfLossesActivePlayer;
    @FXML
    private ImageView imageView;

    Game game;

    public void playAgainButtonClicked() {
        if (getUserId(getToken()) == game.getPlayer1().getUserId()) {
            getScreenController().setWindow(ScreenController.GAME, getToken());
        } else if (getUserId(getToken()) == game.getPlayer2().getUserId()) {
            getScreenController().setWindow(ScreenController.GAME, getToken());
        }
    }

    public void activeGamesButtonClicked() {
        getScreenController().setWindow(ScreenController.ACTIVE_GAMES, "");
    }

    public void initGame(Game game) {}

    public void showResultFromPreviousGame() {
        gameResultWinnerWindow.setText(game.getScorePlayer1() + " - " + game.getScorePlayer2());
    }

    public void showNumberOfWinningsForActivePlayer() throws SQLException {
        if (getUserId(getToken()) == game.getPlayer1().getUserId()) {
            numberOfWinningsActivePlayer = (Label) getConnection().prepareStatement("SELECT * FROM moves WHERE match_id = ? ORDER BY round_no");
        } else if (getUserId(getToken()) == game.getPlayer2().getUserId()) {
            getScreenController().setWindow(ScreenController.GAME, getToken());
        }
    }
}
