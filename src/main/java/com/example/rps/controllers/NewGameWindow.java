package com.example.rps.controllers;
import javafx.scene.input.MouseEvent;
import java.io.IOException;

public class NewGameWindow extends Window {

    public void inviteFriendAndStartGame() {
        //skriv metod som startar nytt spel med en vän
    }

    public void playWithCpuAndStartGame() {
        //skriv metod som startar nytt spel mot datorn
    }

    public void addFriendButtonClicked() {
        getScreenController().setWindow(ScreenController.ADD_FRIEND, getToken());
    }

    public void backButtonClicked() {
        getScreenController().setWindow(ScreenController.ACTIVE_GAMES, getToken());
    }
}
