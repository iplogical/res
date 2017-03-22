package com.inspirationlogical.receipt.waiter.application;

import static com.inspirationlogical.receipt.waiter.controller.RestaurantControllerImpl.RESTAURANT_VIEW_PATH;
import static com.inspirationlogical.receipt.waiter.registry.FXMLLoaderProvider.getInjector;
import static com.inspirationlogical.receipt.waiter.view.ViewLoader.loadView;

import com.inspirationlogical.receipt.waiter.controller.RestaurantController;
import com.inspirationlogical.receipt.corelib.model.adapter.EntityManagerProvider;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;


public class Main extends Application {

    private static final String APP_TITLE = "Receipt";
    public static final int APP_WIDTH = 1024;
    public static final int APP_HEIGHT = 768;

    private @Getter static Stage window;

    @Override
    public void start(Stage stage) {
        window = stage;
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

