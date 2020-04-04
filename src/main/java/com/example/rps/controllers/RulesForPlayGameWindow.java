package com.example.rps.controllers;

import com.example.rps.models.Game;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

//Christian har skrivit den här klassen
public class RulesForPlayGameWindow extends Window {

    private Game gameFromPreviousWindow = null;

    @FXML
    private TextArea textArea;

    @FXML
    public void initialize() {
        try {
            File file = new File("src/main/resources/text/helpText");
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter("Z");
            String text = scanner.next();
            textArea.setText(text);
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Can´t find file");
        }
    }

    public void initGame(Game game) {
        gameFromPreviousWindow = game;
    }

    public void backButtonClicked() {
        if (getScreenController().getPreviousPage().equals(ScreenController.ACTIVE_GAMES)) {
            getScreenController().setWindow(ScreenController.ACTIVE_GAMES, getToken());
        } else {
            getScreenController().setWindow(ScreenController.GAME, getToken(), gameFromPreviousWindow);
        }
    }
}
