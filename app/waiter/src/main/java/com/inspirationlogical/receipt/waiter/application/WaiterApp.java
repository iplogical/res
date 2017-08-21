package com.inspirationlogical.receipt.waiter.application;

import static com.inspirationlogical.receipt.corelib.frontend.application.MainStage.APP_HEIGHT;
import static com.inspirationlogical.receipt.corelib.frontend.application.MainStage.APP_TITLE;
import static com.inspirationlogical.receipt.corelib.frontend.application.MainStage.APP_WIDTH;
import static com.inspirationlogical.receipt.waiter.registry.WaiterRegistry.getInstance;

import com.inspirationlogical.receipt.corelib.frontend.application.MainStage;
import com.inspirationlogical.receipt.corelib.frontend.application.ResourcesProvider;
import com.inspirationlogical.receipt.corelib.frontend.application.StageProvider;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.model.transaction.EntityManagerProvider;
import com.inspirationlogical.receipt.corelib.model.utils.BackgroundThread;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.corelib.utility.resources.ResourceBundleWrapper;
import com.inspirationlogical.receipt.corelib.utility.resources.Resources;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantController;

import com.inspirationlogical.receipt.waiter.utility.WaiterResources;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class WaiterApp extends Application implements StageProvider, ResourcesProvider {

    @Setter
    private static boolean testApplication = false;

    final private static Logger logger = LoggerFactory.getLogger(WaiterApp.class);

    private static Stage stage;

    private ResourceBundleWrapper resources;

    @Override
    public void init() {
        if(testApplication) {
            EntityManagerProvider.getTestEntityManager();
            EntityManagerProvider.getTestEntityManagerArchive();
        }
    }


    @Override
    public void start(Stage stage) {
        logger.warn("Entering WaiterApp");
        this.stage = stage;
        resources = WaiterResources.WAITER;

        Thread.setDefaultUncaughtExceptionHandler(WaiterApp::defaultExceptionHandler);

        try {
            MainStage.setStageProvider(this);
            MainStage.setResourcesProvider(this);
            ViewLoader viewLoader = getInstance(ViewLoader.class);
            Parent root = (Parent) viewLoader.loadView(getInstance(RestaurantController.class));
            stage.setTitle(APP_TITLE);
            stage.setScene(new Scene(root, APP_WIDTH, APP_HEIGHT));
            stage.setFullScreen(true);
            stage.show();
        } catch (Throwable t) {
            logger.error("Error in waiter app.", t);
            t.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        EntityManagerProvider.closeEntityManager();
        EntityManagerProvider.closeEntityManagerArchive();
        BackgroundThread.shutdown();
        logger.warn("Stopping WaiterApp");
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
        logger.error("Unhandled exception in WaiterApp.", e);
        ErrorMessage.showErrorMessageLong(stage.getScene().getRoot(), WaiterResources.WAITER.getString("UnhandledException"));
    }
}

