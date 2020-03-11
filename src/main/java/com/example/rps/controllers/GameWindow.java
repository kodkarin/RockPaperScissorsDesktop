package com.example.rps.controllers;

import com.example.rps.models.Game;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import java.awt.event.ActionEvent;

public class GameWindow {

    @FXML
    private Label player1;
    @FXML
    private Label player2;
    private Game game;
    private int rounds = 0;

    public GameWindow() {

    }

    @FXML
    public void handleRockButtonAction() {
        Label label = new Label("Rock");




    }
}
