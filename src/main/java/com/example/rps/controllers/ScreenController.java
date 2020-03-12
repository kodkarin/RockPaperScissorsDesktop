package com.example.rps.controllers;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.Connection;


public class ScreenController {

    private static Connection conn;
    private static Stage stage;

   // private static final Map<Integer, String> FXMLS = new HashMap<>();

    public static final String LOGIN = "src/main/resources/fxml/login.fxml";
    public static final String CREATE_ACCOUNT = "src/main/resources/fxml/createAccount.fxml";
    public static final String ACTIVE_GAMES = "src/main/resources/fxml/activeGame.fxml";
    public static final String NEW_GAME = "src/main/resources/fxml/newGame.fxml";
    public static final String GAME = "src/main/resources/fxml/game.fxml";
    public static final String WINNER = "src/main/resources/fxml/winner.fxml";
    public static final String ADD_FRIEND = "src/main/resources/fxml/addFriend.fxml";
    public static final String RULES = "src/main/resources/fxml/rulesforplaygame.fxml";



    public ScreenController(Connection conn, Stage stage) {
        this.stage = stage;
        this.conn = conn;
    }



    public static void setWindow (String selectedWindow) {


        try {
            URL url = new File(selectedWindow).toURI().toURL();
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Window window = loader.getController();
            window.initConnection(conn);
            window.setUpWindow();

            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
