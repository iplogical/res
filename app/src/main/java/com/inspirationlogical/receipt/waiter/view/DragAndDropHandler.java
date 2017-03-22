package com.inspirationlogical.receipt.waiter.view;

import com.inspirationlogical.receipt.corelib.utility.Wrapper;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class DragAndDropHandler {

    public static void addDragAndDrop(Node view, boolean enableControl) {
        final Wrapper<Point2D> deltaWrapper = new Wrapper<>();

        view.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (enableControl) {
                    savePosition(mouseEvent, view, deltaWrapper);
                }
            }
        });

        view.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (enableControl) {
                    updatePosition(mouseEvent, view, deltaWrapper);
                }
            }
        });
    }

    public static void addDragAndDrop(Node view) {
        final Wrapper<Point2D> deltaWrapper = new Wrapper<>();

        view.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                savePosition(mouseEvent, view, deltaWrapper);
            }
        });

        view.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                updatePosition(mouseEvent, view, deltaWrapper);
            }
        });
    }

    private static void updatePosition(MouseEvent mouseEvent, Node view, Wrapper<Point2D> deltaWrapper) {
        view.setLayoutX(mouseEvent.getSceneX() + deltaWrapper.getContent().getX());
        view.setLayoutY(mouseEvent.getSceneY() + deltaWrapper.getContent().getY());
    }

    private static void savePosition(MouseEvent mouseEvent, Node view, Wrapper<Point2D> deltaWrapper) {
        Point2D delta = new Point2D(view.getLayoutX() - mouseEvent.getSceneX(),
                view.getLayoutY() - mouseEvent.getSceneY());
        deltaWrapper.setContent(delta);
    }

}
