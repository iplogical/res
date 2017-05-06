package com.inspirationlogical.receipt.corelib.frontend.view;

import com.inspirationlogical.receipt.corelib.utility.Wrapper;

import com.inspirationlogical.receipt.corelib.frontend.viewstate.MotionViewState;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class DragAndDropHandler {

    private static final int POSITION_X_MIN = 10;
    private static final int POSITION_X_MAX = 810;
    private static final int POSITION_Y_MIN = 10;
    private static final int POSITION_Y_MAX = 600;

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
        applyLimitsAndSetLayout(posX, posY, node);
    }

    private static void applyLimitsAndSetLayout(double posX, double posY, Node node) {
        posX = posX < POSITION_X_MIN ? POSITION_X_MIN : posX;
        posY = posY < POSITION_Y_MIN ? POSITION_Y_MIN : posY;
        posX = posX > POSITION_X_MAX ? POSITION_X_MAX : posX;
        posY = posY > POSITION_Y_MAX ? POSITION_Y_MAX : posY;
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
