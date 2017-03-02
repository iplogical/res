package com.inspirationlogical.receipt.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static final int APP_WIDTH = 1024;
    private static final int APP_HEIGHT = 768;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/fxml/Restaurant.fxml"));
        stage.setTitle("Receipt");
        stage.setScene(new Scene(root, APP_WIDTH, APP_HEIGHT));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

