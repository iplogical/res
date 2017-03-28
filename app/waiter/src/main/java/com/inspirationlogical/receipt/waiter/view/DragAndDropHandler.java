package com.inspirationlogical.receipt.waiter.view;

import com.inspirationlogical.receipt.corelib.utility.Wrapper;
import com.inspirationlogical.receipt.waiter.viewstate.ViewState;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class DragAndDropHandler {

    public static void addDragAndDrop(Node view, ViewState enableControl) {
        final Wrapper<Point2D> deltaWrapper = new Wrapper<>();

        view.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (enableControl.isConfigurable()) {
                    savePosition(mouseEvent, view, deltaWrapper);
                }
            }
        });

        view.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (enableControl.isConfigurable()) {
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

    private static void updatePosition(MouseEvent mouseEvent, Node node, Wrapper<Point2D> deltaWrapper) {
        node.setLayoutX(mouseEvent.getSceneX() + deltaWrapper.getContent().getX());
        node.setLayoutY(mouseEvent.getSceneY() + deltaWrapper.getContent().getY());
    }

    private static void savePosition(MouseEvent mouseEvent, Node node, Wrapper<Point2D> deltaWrapper) {
        Point2D delta = new Point2D(node.getLayoutX() - mouseEvent.getSceneX(),
                node.getLayoutY() - mouseEvent.getSceneY());
        deltaWrapper.setContent(delta);
    }

}
