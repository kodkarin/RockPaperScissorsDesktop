package com.example.rps.controllers;


import com.example.rps.models.Game;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.Connection;

//Karin har skrivit den här klassen
public class ScreenController {

    private Connection conn;
    private Stage stage;
    private String previousPage= "";


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

    public void setWindow (String selectedWindow, String token) {

        try {
            URL url = new File(selectedWindow).toURI().toURL();
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Window window = loader.getController();
            window.init(conn, this, token);

            if (!(window instanceof LoginWindow) && !(window instanceof CreateAccountWindow) && !(window instanceof RulesForPlayGameWindow)) {

                boolean validToken = window.validateToken(token);

                if(!validToken) {
                    setWindow(LOGIN, "");
                } else {
                    window.setUpWindow();

                    stage.setScene(scene);
                    stage.show();
                }
            } else {

                window.setUpWindow();

                stage.setScene(scene);
                stage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWindow(String selectedWindow, String token, Game game) {
        try {
            URL url = new File(selectedWindow).toURI().toURL();
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Window window = loader.getController();
            window.init(conn, this, token);

            if (window instanceof GameWindow) {
                ((GameWindow) window).initGame(game);
            } else if (window instanceof WinnerWindow) {
                ((WinnerWindow) window).initGame(game);
            } else if (window instanceof RulesForPlayGameWindow) {
                ((RulesForPlayGameWindow) window).initGame(game);
            }

            boolean validToken = window.validateToken(token);

            if(!validToken) {
                setWindow(LOGIN, "");
            } else {
                window.setUpWindow();

                stage.setScene(scene);
                stage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Christian har skrivit den här metoden
    public String getPreviousPage() {
        return previousPage;
    }

    //Christian har skrivit den här metoden
    public void setPreviousPage(String previousPage) {
        this.previousPage = previousPage;
    }
}
