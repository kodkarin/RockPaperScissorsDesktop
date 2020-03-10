package com.example.rps;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private static final String user = "postgres";
    private static final String login = "elvira09postgres";

    public void start(Stage myStage) throws Exception {


        Connection conn = null;

        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Stensaxpase", user, login);
        } catch (Exception e) {
            e.printStackTrace();
        }

        URL url = new File("src/main/resources/fxml/addFriend.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);


        Scene scene = new Scene(root, 400, 600);

        myStage.setTitle("Logga in");
        myStage.setScene(scene);
        myStage.show();

    }

   /* public void switchScene(String fxmlFile)
    {

        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource(fxmlFile));
        Parent root;
        try
        {
            root = (Parent)loader.load();
            if(fxmlFile.equals("calculator.fxml"))
            {
                BasicCalculatorView controller = (BasicCalculatorView)loader.getController();
                controller.setModel(new BasicCalculatorModelTest(controller));
                controller.setLogic(this);
            }
            else if(fxmlFile.equals("TestSwitch.fxml"))
            {
                TestSwitch controller = (TestSwitch)loader.getController();
                controller.setLogic(this);
            }
            this.stage.setScene(new Scene(root));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }*/
}
