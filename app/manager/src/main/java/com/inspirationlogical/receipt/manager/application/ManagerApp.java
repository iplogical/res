package com.inspirationlogical.receipt.manager.application;

import static com.inspirationlogical.receipt.corelib.frontend.application.MainStage.APP_HEIGHT;
import static com.inspirationlogical.receipt.corelib.frontend.application.MainStage.APP_TITLE;
import static com.inspirationlogical.receipt.corelib.frontend.application.MainStage.APP_WIDTH;
import static com.inspirationlogical.receipt.manager.registry.ManagerRegistry.getInstance;

import com.inspirationlogical.receipt.corelib.frontend.application.MainStage;
import com.inspirationlogical.receipt.corelib.frontend.application.ResourcesProvider;
import com.inspirationlogical.receipt.corelib.frontend.application.StageProvider;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.model.utils.EntityManagerProvider;
import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.manager.controller.GoodsController;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class ManagerApp extends Application implements StageProvider, ResourcesProvider {

    private Stage stage;

    private Resources resources;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        resources = Resources.MANAGER;

        MainStage.setStageProvider(this);
        MainStage.setResourcesProvider(this);
        ViewLoader viewLoader = getInstance(ViewLoader.class);
        Parent root = (Parent) viewLoader.loadView(getInstance(GoodsController.class));
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

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public Resources getResources() {
        return resources;
    }
}

