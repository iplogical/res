package com.inspirationlogical.receipt.waiter.view;

import static com.inspirationlogical.receipt.waiter.view.NodeUtility.getNodePosition;

import com.inspirationlogical.receipt.corelib.utility.Wrapper;
import com.inspirationlogical.receipt.waiter.builder.ContextMenuBuilder;
import com.inspirationlogical.receipt.waiter.viewstate.RestaurantViewState;

import javafx.animation.PauseTransition;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class PressAndHoldHandler {

    private static RestaurantViewState restaurantViewState;

    public static void addPressAndHold(Control control, ContextMenuBuilder contextMenuBuilder, Duration holdTime) {
        Wrapper<MouseEvent> mouseEventWrapper = new Wrapper<>();
        Wrapper<ContextMenu> contextMenuWrapper = new Wrapper<>();
        PauseTransition holdTimer = new PauseTransition(holdTime);

        holdTimer.setOnFinished(event -> {
            MouseEvent mouseEvent = mouseEventWrapper.getContent();
            Point2D position = new Point2D(mouseEvent.getX(), mouseEvent.getY()).add(getNodePosition(control));
            contextMenuWrapper.getContent().show(control, 0, 0);
        });

        control.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            mouseEvent.consume();
            mouseEventWrapper.setContent(mouseEvent);
            contextMenuWrapper.setContent(contextMenuBuilder.build(restaurantViewState));
            control.setContextMenu(contextMenuWrapper.getContent());
            holdTimer.playFromStart();
        });

        control.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> holdTimer.stop());

        control.addEventHandler(MouseEvent.DRAG_DETECTED, event -> holdTimer.stop());
    }
}
