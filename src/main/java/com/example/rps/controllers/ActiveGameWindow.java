package com.example.rps.controllers;

import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class ActiveGameWindow extends Window {

    public void newGameButtonClicked() {
        getScreenController().setWindow(ScreenController.GAME, getToken());
    }

    public void helpButtonClicked() {
        super.previousPage = ScreenController.ACTIVE_GAMES;
        getScreenController().setWindow(ScreenController.RULES, getToken());
    }

    public void logOutButtonClicked() {
        getScreenController().setWindow(ScreenController.LOGIN, "");
    }
}
