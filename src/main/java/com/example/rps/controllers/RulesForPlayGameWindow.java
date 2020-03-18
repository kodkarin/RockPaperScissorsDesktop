package com.example.rps.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class RulesForPlayGameWindow extends Window {

    /*@FXML
    Label text;

    @FXML
    public void initialize() {
        try {
            text.setText(
                new String(
                    ScreenController.getResAsStream("/text/helpText").readAllBytes(),
                    StandardCharsets.UTF_8).strip());
            System.out.println(text.isResizable());
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }*/

    @FXML
    private TextField textField;

    @FXML
    public void initialize() {

        try {
            File file = new File("src/main/resources/text/helpText");
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter("Z");
            String text = scanner.next();
            textField.setText(text);
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Kan ej hitta fil");
        }
    }
}
