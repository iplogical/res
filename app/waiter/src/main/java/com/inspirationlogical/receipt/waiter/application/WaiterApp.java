package com.inspirationlogical.receipt.waiter.application;

import com.inspirationlogical.receipt.corelib.frontend.application.MainStage;
import com.inspirationlogical.receipt.corelib.frontend.application.ResourcesProvider;
import com.inspirationlogical.receipt.corelib.frontend.application.StageProvider;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.model.transaction.EntityManagerProvider;
import com.inspirationlogical.receipt.corelib.model.utils.BackgroundThread;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.corelib.utility.resources.ResourceBundleWrapper;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantController;
import com.inspirationlogical.receipt.waiter.utility.WaiterResources;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;
import java.util.ResourceBundle;

import static com.inspirationlogical.receipt.corelib.frontend.application.MainStage.*;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.inspirationlogical.receipt.corelib",
        "com.inspirationlogical.receipt.waiter"
})
public class WaiterApp extends Application implements StageProvider, ResourcesProvider {

    @Setter
    private static boolean testApplication = false;

    final private static Logger logger = LoggerFactory.getLogger(WaiterApp.class);

    private static Stage stage;

    @Autowired
    private ViewLoader viewLoader;

    @Autowired
    private RestaurantController restaurantController;

    private ConfigurableApplicationContext springContext;

    private Parent root;

    private ResourceBundleWrapper resources;

    @Override
    public void init() {
        if(testApplication) {
            EntityManagerProvider.getTestEntityManager();
        }
        springContext = SpringApplication.run(WaiterApp.class);
//        ViewLoader viewLoader = getInstance(ViewLoader.class);
    }

    @Override
    public void start(Stage stage) {
        logger.warn("Entering WaiterApp");
        this.stage = stage;
        MainStage.setStageProvider(this);
        MainStage.setResourcesProvider(this);
        resources = WaiterResources.WAITER;

        viewLoader = springContext.getBean(ViewLoader.class);
        restaurantController = springContext.getBean(RestaurantController.class);

        ResourceBundle resourceBundle = MainStage.getResourcesProvider().getResources().getBundle();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/fxml/Restaurant.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        fxmlLoader.setController(restaurantController);
        fxmlLoader.setResources(resourceBundle);
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        root = (Parent) viewLoader.loadView(restaurantController);

        Thread.setDefaultUncaughtExceptionHandler(WaiterApp::defaultExceptionHandler);

        try {
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
        BackgroundThread.shutdown();
        springContext.stop();
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
        logger.error("Unhandled exception in WaiterApp. On thread: " + t.getName(), e);
        ErrorMessage.showErrorMessageLong(stage.getScene().getRoot(), WaiterResources.WAITER.getString("UnhandledException"));
    }
}

