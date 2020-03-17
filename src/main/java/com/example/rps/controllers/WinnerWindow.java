package com.example.rps.controllers;

import java.awt.*;

public class WinnerWindow extends Window {

    TextField textFieldActiveGameResult = new TextField();
    TextField numberOfWinningsActivePlayer = new TextField();
    TextField numberOfLossesActivePlayer = new TextField();

    public void playAgainButtonClicked() {
        // Skriv metod som startar nytt spel mot samma motspelare
    }

    public void activeGamesButtonClicked() {
        getScreenController().setWindow(ScreenController.ACTIVE_GAMES, "");
    }
}
