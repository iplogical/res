package com.inspirationlogical.receipt.corelib.frontend.view;

import com.inspirationlogical.receipt.corelib.utility.Wrapper;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class DragAndDropHandler {

    private static final int POSITION_X_MIN = 10;
    private static final int POSITION_X_MAX = 810;
    private static final int POSITION_Y_MIN = 10;
    private static final int POSITION_Y_MAX = 600;
    private static final int GRID_SIZE_IN_PIXELS = 5;


    public static void addFormDragAndDrop(Node view) {
        final Wrapper<Point2D> deltaWrapper = new Wrapper<>();
        view.setOnMousePressed(mouseEvent -> savePosition(mouseEvent, deltaWrapper));
        view.setOnMouseDragged(mouseEvent -> updatePosition(mouseEvent, deltaWrapper));
    }

    public static void updatePosition(MouseEvent mouseEvent, Wrapper<Point2D> deltaWrapper) {
        Node node = (Node)mouseEvent.getSource();
        double posX = mouseEvent.getSceneX() + deltaWrapper.getContent().getX();
        double posY = mouseEvent.getSceneY() + deltaWrapper.getContent().getY();
        posX = snapToGrid(posX);
        posY = snapToGrid(posY);
        applyLimitsAndSetLayout(posX, posY, node);
    }

    private static long snapToGrid(double value) {
        return Math.round(value / GRID_SIZE_IN_PIXELS) * GRID_SIZE_IN_PIXELS;
    }

    private static void applyLimitsAndSetLayout(double posX, double posY, Node node) {
        posX = posX < POSITION_X_MIN ? POSITION_X_MIN : posX;
        posY = posY < POSITION_Y_MIN ? POSITION_Y_MIN : posY;
        posX = posX > POSITION_X_MAX ? POSITION_X_MAX : posX;
        posY = posY > POSITION_Y_MAX ? POSITION_Y_MAX : posY;
        node.setLayoutX(posX);
        node.setLayoutY(posY);
    }

    public static void savePosition(MouseEvent mouseEvent, Wrapper<Point2D> deltaWrapper) {
        Node node = (Node)mouseEvent.getSource();
        double posX = node.getLayoutX() - mouseEvent.getSceneX();
        double posY = node.getLayoutY() - mouseEvent.getSceneY();
        posX = snapToGrid(posX);
        posY = snapToGrid(posY);
        Point2D delta = new Point2D(posX, posY);
        deltaWrapper.setContent(delta);
    }
}
