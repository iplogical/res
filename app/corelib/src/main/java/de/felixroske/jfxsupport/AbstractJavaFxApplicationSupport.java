package de.felixroske.jfxsupport;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public abstract class AbstractJavaFxApplicationSupport extends Application {
    private static Logger LOGGER = LoggerFactory.getLogger(AbstractJavaFxApplicationSupport.class);

    private static String[] savedArgs = new String[0];

    @Setter
    static Class<? extends AbstractFxmlView> savedInitialView;

    private static ConfigurableApplicationContext applicationContext;


    private static List<Image> icons = new ArrayList<>();

    protected AbstractJavaFxApplicationSupport() {
    }

    public static Stage getStage() {
        return GUIState.getStage();
    }

    public static Scene getScene() {
        return GUIState.getScene();
    }

    @Override
    public void init() throws Exception {
        AbstractJavaFxApplicationSupport.applicationContext = SpringApplication.run(this.getClass(), savedArgs);
    }

    @Override
    public void start(final Stage stage) throws Exception {
        GUIState.setStage(stage);
        GUIState.setHostServices(this.getHostServices());
        addFullScreenListener();
        showInitialView();
        GUIState.getStage().setFullScreen(true);
    }

    private void showInitialView() {
        showView(savedInitialView);
    }

    private void addFullScreenListener() {
        GUIState.getStage().addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if(keyEvent.getCode() == KeyCode.F11) {
                GUIState.getStage().setFullScreen(!GUIState.getStage().isFullScreen());
            }
        });
    }

    public static void showView(final Class<? extends AbstractFxmlView> newView) {
        try {
            final AbstractFxmlView view = applicationContext.getBean(newView);

            if (GUIState.getScene() == null) {
                GUIState.setScene(new Scene(view.getView()));
            } else {
                GUIState.getScene().setRoot(view.getView());
            }
            GUIState.getStage().setScene(GUIState.getScene());

            applyEnvPropsToView();

            GUIState.getStage().getIcons().addAll(icons);
            GUIState.getStage().show();

        } catch (Throwable t) {
            LOGGER.error("Failed to load application: ", t);
            showErrorAlert(t);
        }
    }

    public static Node getRootNode(final Class<? extends AbstractFxmlView> window) {
        final AbstractFxmlView view = applicationContext.getBean(window);
        return view.getView();
    }

    public static Object getController(final Class<? extends AbstractFxmlView> window) {
        final AbstractFxmlView view = applicationContext.getBean(window);
        view.getView();
        return view.getController();
    }

    private static void showErrorAlert(Throwable throwable) {
        Alert alert = new Alert(AlertType.ERROR, "Oops! An unrecoverable error occurred.\n" +
                "Please contact your software vendor.\n\n" +
                "The application will stop now.\n\n" +
                "Error: " + throwable.getMessage());
        alert.showAndWait().ifPresent(response -> Platform.exit());
    }

    private static void applyEnvPropsToView() {
        PropertyReaderHelper.setIfPresent(applicationContext.getEnvironment(), Constant.KEY_TITLE, String.class,
                GUIState.getStage()::setTitle);

        PropertyReaderHelper.setIfPresent(applicationContext.getEnvironment(), Constant.KEY_STAGE_WIDTH, Double.class,
                GUIState.getStage()::setWidth);

        PropertyReaderHelper.setIfPresent(applicationContext.getEnvironment(), Constant.KEY_STAGE_HEIGHT, Double.class,
                GUIState.getStage()::setHeight);

        PropertyReaderHelper.setIfPresent(applicationContext.getEnvironment(), Constant.KEY_STAGE_RESIZABLE, Boolean.class,
                GUIState.getStage()::setResizable);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (applicationContext != null) {
            applicationContext.close();
        }
    }


    public static void launch(final Class<? extends Application> appClass,
                              final Class<? extends AbstractFxmlView> view, final String[] args) {
        savedInitialView = view;
        savedArgs = args;

        if (SystemTray.isSupported()) {
            GUIState.setSystemTray(SystemTray.getSystemTray());
        }
        Application.launch(appClass, args);
    }
}
