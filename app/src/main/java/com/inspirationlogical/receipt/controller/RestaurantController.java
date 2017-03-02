package com.inspirationlogical.receipt.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Popup;
import javafx.util.Duration;

public class RestaurantController implements Initializable {

    private static final int HOLD_DURATION_MILLIS = 500;

    @FXML
    AnchorPane layout;

    @FXML
    Label test;

    private ContextMenu contextMenu;

    Popup popup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        test.setText("Test!");

        contextMenu = new ContextMenu(new MenuItem("Test!!!"));

        addPressAndHoldHandler(layout, Duration.millis(HOLD_DURATION_MILLIS), contextMenu);

        try {
            AnchorPane contextMenu = FXMLLoader.load(getClass().getResource("/view/fxml/ContextMenu.fxml"));

            popup = new Popup();

            popup.getContent().add(contextMenu);

        }
        catch (Exception e) {

        }

    }

    private void addPressAndHoldHandler(Node node, Duration holdTime, ContextMenu contextMenu) {

        class Wrapper<T> { T content ; }
        Wrapper<MouseEvent> eventWrapper = new Wrapper<>();

        PauseTransition holdTimer = new PauseTransition(holdTime);
        holdTimer.setOnFinished(event -> {
            popup.show(
                    node,
                    eventWrapper.content.getScreenX(),
                    eventWrapper.content.getScreenY());
            /*contextMenu.show(
                    node,
                    eventWrapper.content.getScreenX(),
                    eventWrapper.content.getScreenY()
            );*/
        });


        node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            eventWrapper.content = event ;
            holdTimer.playFromStart();
        });
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> holdTimer.stop());
        node.addEventHandler(MouseEvent.DRAG_DETECTED, event -> holdTimer.stop());
    }
}
