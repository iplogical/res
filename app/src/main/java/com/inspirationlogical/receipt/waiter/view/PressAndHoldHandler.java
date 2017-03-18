package com.inspirationlogical.receipt.waiter.view;

import static com.inspirationlogical.receipt.waiter.view.NodeUtility.getNodePosition;
import static com.inspirationlogical.receipt.waiter.view.NodeUtility.hideNode;
import static com.inspirationlogical.receipt.waiter.view.NodeUtility.showNode;

import com.inspirationlogical.receipt.corelib.utility.Wrapper;
import com.inspirationlogical.receipt.waiter.controller.ContextMenuController;

import javafx.animation.PauseTransition;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class PressAndHoldHandler {

    private static ContextMenuController controller;

    public static void setController(ContextMenuController controller) {
        PressAndHoldHandler.controller = controller;
    }

    public static void addPressAndHold(Node node, Node popup, Duration holdTime) {
        Wrapper<MouseEvent> mouseEventWrapper = new Wrapper<>();
        PauseTransition holdTimer = new PauseTransition(holdTime);

        holdTimer.setOnFinished(event -> {
            MouseEvent mouseEvent = mouseEventWrapper.getContent();
            Point2D position = new Point2D(mouseEvent.getX(), mouseEvent.getY()).add(getNodePosition(node));
            controller.setSourceNode((Node) mouseEvent.getSource());
            showNode(popup, position);
        });

        node.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            mouseEvent.consume();
            mouseEventWrapper.setContent(mouseEvent);
            holdTimer.playFromStart();
            hideNode(popup);
        });

        node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> holdTimer.stop());

        node.addEventHandler(MouseEvent.DRAG_DETECTED, event -> holdTimer.stop());
    }
}
