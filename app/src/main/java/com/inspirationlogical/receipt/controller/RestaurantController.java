package com.inspirationlogical.receipt.controller;

import static com.inspirationlogical.receipt.controller.ContextMenuController.CONTEXT_MENU_VIEW_PATH;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.registry.FXMLLoaderProvider;
import com.inspirationlogical.receipt.utility.Wrapper;
import com.inspirationlogical.receipt.view.TableView;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

@Singleton
public class RestaurantController implements Initializable {

    public static final String RESTAURANT_VIEW_PATH = "/view/fxml/Restaurant.fxml";
    private static final int HOLD_DURATION_MILLIS = 500;

    @FXML
    AnchorPane layout;

    private VBox contextMenu;

    @Inject
    private ContextMenuController contextMenuController;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setUpContextMenu();
    }

    private void setUpContextMenu() {
        try {
            FXMLLoader loader = FXMLLoaderProvider.getLoader(CONTEXT_MENU_VIEW_PATH);
            loader.setController(contextMenuController);
            contextMenu = loader.load();
            contextMenu.setVisible(false);
            layout.getChildren().add(contextMenu);

            addPressAndHoldHandler(layout, Duration.millis(HOLD_DURATION_MILLIS));
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void addPressAndHoldHandler(Node node, Duration holdTime) {

        Wrapper<MouseEvent> eventWrapper = new Wrapper<>();
        PauseTransition holdTimer = new PauseTransition(holdTime);

        holdTimer.setOnFinished(event -> {
            if (eventWrapper.content.getSource() instanceof AnchorPane) {
                contextMenu.getChildrenUnmodifiable().forEach(elem ->  elem.setManaged((Boolean) elem.getUserData()));
            }
            contextMenu.setLayoutX(eventWrapper.content.getX());
            contextMenu.setLayoutY(eventWrapper.content.getY());
            contextMenu.setVisible(true);
        });

        node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            eventWrapper.content = event ;
            holdTimer.playFromStart();
        });
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> holdTimer.stop());
        node.addEventHandler(MouseEvent.DRAG_DETECTED, event -> holdTimer.stop());
    }

    public void addTable() {

        // todo: Call service method to add a new table
        Point2D position = new Point2D(contextMenu.getLayoutX(), contextMenu.getLayoutY());


        addPressAndHoldHandler(new TableView(layout, "Table", position).getView(), Duration.millis(HOLD_DURATION_MILLIS));
    }
}
