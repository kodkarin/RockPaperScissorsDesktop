package com.example.rps.controllers;

import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class ActiveGameWindow extends Window {

    public void newGameButtonClicked() {
        getScreenController().setWindow(ScreenController.GAME, "");
    }

    public void helpButtonClicked() {
       getScreenController().setWindow(ScreenController.RULES, "");
    }

    public void logOutButtonClicked() {
        getScreenController().setWindow(ScreenController.LOGIN, "");
    }
}
