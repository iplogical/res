package com.inspirationlogical.receipt.application;

import static com.inspirationlogical.receipt.controller.RestaurantController.RESTAURANT_VIEW_PATH;

import com.inspirationlogical.receipt.registry.FXMLLoaderProvider;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static final int APP_WIDTH = 1024;
    private static final int APP_HEIGHT = 768;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoaderProvider.getLoader(RESTAURANT_VIEW_PATH).load();
        stage.setTitle("Receipt");
        stage.setScene(new Scene(root, APP_WIDTH, APP_HEIGHT));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

