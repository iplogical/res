package com.inspirationlogical.receipt.waiter.view;

import com.inspirationlogical.receipt.corelib.utility.Wrapper;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Toggle;
import javafx.scene.input.MouseEvent;

public class DragAndDropHandler {

    public static void addDragAndDrop(Node view, Toggle enableControl) {
        final Wrapper<Point2D> deltaWrapper = new Wrapper<>();

        view.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (enableControl.isSelected()) {
                    Point2D delta = new Point2D(view.getLayoutX() - mouseEvent.getSceneX(),
                            view.getLayoutY() - mouseEvent.getSceneY());
                    deltaWrapper.setContent(delta);
                }
            }
        });

        view.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (enableControl.isSelected()) {
                    view.setLayoutX(mouseEvent.getSceneX() + deltaWrapper.getContent().getX());
                    view.setLayoutY(mouseEvent.getSceneY() + deltaWrapper.getContent().getY());
                }
            }
        });
    }
}
