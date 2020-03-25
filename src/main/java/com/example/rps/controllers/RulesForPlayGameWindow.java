package com.example.rps.controllers;

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

public class RulesForPlayGameWindow extends Window {

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
            System.out.println("Kan ej hitta fil");
        }
    }

    public void backButtonClicked() {
        if (super.previousPage == ScreenController.ACTIVE_GAMES) {
            getScreenController().setWindow(ScreenController.ACTIVE_GAMES, getToken());
        } else {
            getScreenController().setWindow(ScreenController.GAME, getToken());
            GameWindow gameWindow = new GameWindow();
        }
    }
}
