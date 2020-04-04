package com.example.rps;

import com.example.rps.controllers.ScreenController;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    //Karin har skrivit den här metoden
    public void start(Stage myStage) {

        String user = "";
        String login = "";

        try {
            File file = new File("src/main/resources/text/login.txt");
            Scanner scanner = new Scanner(file);
            user = scanner.nextLine();
            login = scanner.nextLine();

        } catch (FileNotFoundException e) {
            System.out.println("Kan ej hitta fil");
        }

        Connection conn = null;

        try {
            conn = DriverManager.getConnection("jdbc:postgresql://database-1.c2yvgj4bks0w.us-east-2.rds.amazonaws.com:5432/postgres", user, login);
        } catch (Exception e) {
            e.printStackTrace();
        }

        myStage.setTitle("Rock Paper Scissors");

        ScreenController screenController = new ScreenController(conn, myStage);
        screenController.setWindow(ScreenController.LOGIN, "");
    }
}
