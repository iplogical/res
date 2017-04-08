package com.inspirationlogical.receipt.waiter.view;

import com.inspirationlogical.receipt.corelib.utility.Wrapper;

import com.inspirationlogical.receipt.corelib.utility.view.MotionViewState;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class DragAndDropHandler {

    public static void addDragAndDrop(Node view, MotionViewState motionViewState) {
        final Wrapper<Point2D> deltaWrapper = new Wrapper<>();

        view.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (motionViewState.getMovableProperty().getValue()) {
                    savePosition(mouseEvent, view, deltaWrapper, motionViewState);
                }
            }
        });

        view.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (motionViewState.getMovableProperty().getValue()) {
                    updatePosition(mouseEvent, view, deltaWrapper, motionViewState);
                }
            }
        });
    }

    public static void addDragAndDrop(Node view) {
        final Wrapper<Point2D> deltaWrapper = new Wrapper<>();

        view.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                savePosition(mouseEvent, view, deltaWrapper, null);
            }
        });

        view.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                updatePosition(mouseEvent, view, deltaWrapper, null);
            }
        });
    }

    private static void updatePosition(MouseEvent mouseEvent, Node node, Wrapper<Point2D> deltaWrapper, MotionViewState motionViewState) {
        double posX = mouseEvent.getSceneX() + deltaWrapper.getContent().getX();
        double posY = mouseEvent.getSceneY() + deltaWrapper.getContent().getY();
        if (motionViewState != null && motionViewState.getSnapToGridProperty().getValue()) {
            posX = snapToGrid(posX, motionViewState.getGridSizeProperty().intValue());
            posY = snapToGrid(posY, motionViewState.getGridSizeProperty().intValue());
        }
        node.setLayoutX(posX);
        node.setLayoutY(posY);
    }

    private static void savePosition(MouseEvent mouseEvent, Node node, Wrapper<Point2D> deltaWrapper, MotionViewState motionViewState) {
        double posX = node.getLayoutX() - mouseEvent.getSceneX();
        double posY = node.getLayoutY() - mouseEvent.getSceneY();
        if (motionViewState != null && motionViewState.getSnapToGridProperty().getValue()) {
            posX = snapToGrid(posX, motionViewState.getGridSizeProperty().intValue());
            posY = snapToGrid(posY, motionViewState.getGridSizeProperty().intValue());
        }
        Point2D delta = new Point2D(posX, posY);
        deltaWrapper.setContent(delta);
    }

    private static long snapToGrid(double value, int gridSize) {
        if (gridSize == 0) gridSize++;
        return Math.round(value / gridSize) * gridSize;
    }
}
