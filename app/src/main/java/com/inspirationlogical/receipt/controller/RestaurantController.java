package com.inspirationlogical.receipt.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.inspirationlogical.receipt.utility.Wrapper;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Duration;

public class RestaurantController implements Initializable {

    private static final int HOLD_DURATION_MILLIS = 500;

    @FXML
    AnchorPane layout;

    Popup popup;

    VBox contextMenu;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        addPressAndHoldHandler(layout, Duration.millis(HOLD_DURATION_MILLIS));

        setUpContextMenu();
    }

    private void setUpContextMenu() {
        try {
            contextMenu = FXMLLoader.load(getClass().getResource("/view/fxml/ContextMenu.fxml"));
            popup = new Popup();
            popup.getContent().add(contextMenu);
            contextMenu.setUserData(popup);
        }
        catch (Exception e) {
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
            popup.show(node, eventWrapper.content.getScreenX(), eventWrapper.content.getScreenY());
        });

        node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            eventWrapper.content = event ;
            holdTimer.playFromStart();
        });
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> holdTimer.stop());
        node.addEventHandler(MouseEvent.DRAG_DETECTED, event -> holdTimer.stop());
    }
}
