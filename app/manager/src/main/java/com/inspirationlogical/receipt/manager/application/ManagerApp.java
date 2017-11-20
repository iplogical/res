package com.inspirationlogical.receipt.manager.application;

import static com.inspirationlogical.receipt.corelib.frontend.application.MainStage.APP_HEIGHT;
import static com.inspirationlogical.receipt.corelib.frontend.application.MainStage.APP_TITLE;
import static com.inspirationlogical.receipt.corelib.frontend.application.MainStage.APP_WIDTH;
import static com.inspirationlogical.receipt.manager.registry.ManagerRegistry.getInstance;

import com.inspirationlogical.receipt.corelib.frontend.application.MainStage;
import com.inspirationlogical.receipt.corelib.frontend.application.ResourcesProvider;
import com.inspirationlogical.receipt.corelib.frontend.application.StageProvider;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.model.transaction.EntityManagerProvider;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.corelib.utility.resources.ResourceBundleWrapper;
import com.inspirationlogical.receipt.corelib.utility.resources.Resources;
import com.inspirationlogical.receipt.manager.controller.goods.GoodsController;

import com.inspirationlogical.receipt.manager.utility.ManagerResources;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagerApp extends Application implements StageProvider, ResourcesProvider {

    @Setter
    private static boolean testApplication = false;

    final private static Logger logger = LoggerFactory.getLogger(ManagerApp.class);

    private static Stage stage;

    private ResourceBundleWrapper resources;

    @Override
    public void init() {
        if(testApplication) {
            EntityManagerProvider.getTestEntityManager();
        }
    }

    @Override
    public void start(Stage stage) {
        logger.warn("Entering ManagerApp");
        this.stage = stage;
        resources = ManagerResources.MANAGER;

        Thread.setDefaultUncaughtExceptionHandler(ManagerApp::defaultExceptionHandler);

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
    public ResourceBundleWrapper getResources() {
        return resources;
    }

    private static void defaultExceptionHandler(Thread t, Throwable e) {
        logger.error("Unhandled exception in ManagerApp. On thread: " + t.getName(), e);
        ErrorMessage.showErrorMessageLong(stage.getScene().getRoot(), ManagerResources.MANAGER.getString("UnhandledException"));
    }

}

