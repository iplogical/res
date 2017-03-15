package com.inspirationlogical.receipt.application;

import static com.inspirationlogical.receipt.controller.RestaurantControllerImpl.RESTAURANT_VIEW_PATH;
import static com.inspirationlogical.receipt.registry.FXMLLoaderProvider.getInjector;
import static com.inspirationlogical.receipt.view.ViewLoader.loadView;

import com.inspirationlogical.receipt.controller.RestaurantController;
import com.inspirationlogical.receipt.model.adapter.EntityManagerProvider;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static final String APP_TITLE = "Receipt";
    private static final int APP_WIDTH = 1024;
    private static final int APP_HEIGHT = 768;

    @Override
    public void start(Stage stage) {
        Parent root = (Parent) loadView(RESTAURANT_VIEW_PATH, getInjector().getInstance(RestaurantController.class));
        stage.setTitle(APP_TITLE);
        stage.setScene(new Scene(root, APP_WIDTH, APP_HEIGHT));
        stage.setFullScreen(true);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        EntityManagerProvider.closeEntityManager();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

