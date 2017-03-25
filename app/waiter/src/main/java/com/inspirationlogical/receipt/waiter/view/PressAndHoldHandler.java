package com.inspirationlogical.receipt.waiter.view;

import com.inspirationlogical.receipt.corelib.utility.Wrapper;
import com.inspirationlogical.receipt.waiter.builder.ContextMenuBuilder;
import com.inspirationlogical.receipt.waiter.viewstate.ViewState;

import javafx.animation.PauseTransition;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class PressAndHoldHandler {

    public static void addPressAndHold(ViewState viewState, Control control, ContextMenuBuilder contextMenuBuilder, Duration holdTime) {
        Wrapper<MouseEvent> mouseEventWrapper = new Wrapper<>();
        PauseTransition holdTimer = new PauseTransition(holdTime);

        holdTimer.setOnFinished(event -> {
            MouseEvent mouseEvent = mouseEventWrapper.getContent();
            ContextMenu contextMenu = contextMenuBuilder.build(viewState);
            control.setContextMenu(contextMenu);
            contextMenu.setUserData(control);
            contextMenu.show(control, mouseEvent.getScreenX(), mouseEvent.getScreenY());
        });

        control.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            mouseEvent.consume();
            mouseEventWrapper.setContent(mouseEvent);
            holdTimer.playFromStart();
        });

        control.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> holdTimer.stop());

        control.addEventHandler(MouseEvent.DRAG_DETECTED, event -> holdTimer.stop());
    }
}
