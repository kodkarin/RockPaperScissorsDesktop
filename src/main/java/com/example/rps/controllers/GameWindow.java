package com.example.rps.controllers;

import com.example.rps.models.Game;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.sql.Connection;

public class GameWindow extends Window {

    @FXML
    private Label player1;
    @FXML
    private Label player2;
    private Game game;
    private int rounds = 0;


    @FXML
    public void handleRockButtonAction() {
        Parent root = null;
        Label label = new Label("Rock");

        try {
            URL url = new File("src/main/resources/fxml/game.fxml").toURI().toURL();
            root = FXMLLoader.load(url);

        } catch (Exception e) {
            e.printStackTrace();
        }







    }
}
