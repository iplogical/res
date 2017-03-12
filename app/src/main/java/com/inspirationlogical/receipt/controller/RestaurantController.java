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
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

@Singleton
public class RestaurantController implements Initializable {

    public static final String RESTAURANT_VIEW_PATH = "/view/fxml/Restaurant.fxml";
    private static final int HOLD_DURATION_MILLIS = 500;
    private static double TABLE_WIDTH = 100.0;
    private static double TABLE_HEIGHT = 100.0;

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
            contextMenu.setLayoutX(eventWrapper.getContent().getX() + node.getLayoutX());
            contextMenu.setLayoutY(eventWrapper.getContent().getY() + node.getLayoutY());
            contextMenu.toFront();
            contextMenu.setVisible(true);
        });

        node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            eventWrapper.setContent(event);
            holdTimer.playFromStart();
            contextMenu.setVisible(false);
        });

        node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> holdTimer.stop());

        node.addEventHandler(MouseEvent.DRAG_DETECTED, event -> holdTimer.stop());
    }

    public void addTable() {

        Point2D position = new Point2D(contextMenu.getLayoutX(), contextMenu.getLayoutY());

        // todo: Call service method to add a new table (returns table details as table number, table name and people count)

        Button button = new Button();
        button.setMinWidth(TABLE_WIDTH);
        button.setMinHeight(TABLE_HEIGHT);
        button.setTextAlignment(TextAlignment.CENTER);
        button.setFont(Font.font(16));
        button.setText("6\n" + "Spicces Feri\n" + "3 fő");

        addPressAndHoldHandler(new TableView(button, layout, position).getView(), Duration.millis(HOLD_DURATION_MILLIS));
    }
}
