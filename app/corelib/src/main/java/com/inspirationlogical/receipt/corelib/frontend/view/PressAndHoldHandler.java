package com.inspirationlogical.receipt.corelib.frontend.view;

import com.inspirationlogical.receipt.corelib.utility.Wrapper;
import com.inspirationlogical.receipt.corelib.frontend.contextmenu.ContextMenuBuilder;

import javafx.animation.PauseTransition;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class PressAndHoldHandler {
    private static final int HOLD_DURATION_MILLIS = 200;

    public static void addPressAndHold(Control control, ContextMenuBuilder contextMenuBuilder) {
        Wrapper<MouseEvent> mouseEventWrapper = new Wrapper<>();
        PauseTransition holdTimer = new PauseTransition(Duration.millis(HOLD_DURATION_MILLIS));

        holdTimer.setOnFinished(event -> {
            MouseEvent mouseEvent = mouseEventWrapper.getContent();
            ContextMenu contextMenu = contextMenuBuilder.build();
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

        control.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> holdTimer.stop());
    }
}
