package com.inspirationlogical.receipt.view;

import static com.inspirationlogical.receipt.view.NodeUtility.getNodePosition;
import static com.inspirationlogical.receipt.view.NodeUtility.hideNode;
import static com.inspirationlogical.receipt.view.NodeUtility.showNode;

import com.inspirationlogical.receipt.utility.Wrapper;

import javafx.animation.PauseTransition;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class PressAndHoldHandler {

    public static void addPressAndHold(Node node, Node popup, Duration holdTime) {
        Wrapper<Point2D> positionWrapper = new Wrapper<>();
        PauseTransition holdTimer = new PauseTransition(holdTime);

        holdTimer.setOnFinished(event -> {
            Point2D position = positionWrapper.getContent().add(getNodePosition(node));
            showNode(popup, position);
        });

        node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            positionWrapper.setContent(new Point2D(event.getX(), event.getY()));
            holdTimer.playFromStart();
            hideNode(popup);
        });

        node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> holdTimer.stop());

        node.addEventHandler(MouseEvent.DRAG_DETECTED, event -> holdTimer.stop());
    }
}
