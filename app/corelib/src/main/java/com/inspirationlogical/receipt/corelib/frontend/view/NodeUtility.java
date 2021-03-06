package com.inspirationlogical.receipt.corelib.frontend.view;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;

public class NodeUtility {

    private static final int LAYOUT_OFFSET_Y = 100;

    public static void showNode(Node node, Point2D position) {
        node.setLayoutX(position.getX());
        node.setLayoutY(position.getY());
        node.toFront();
        node.setVisible(true);
    }

    public static void moveNode(Node node, Pane target) {
        if (node != null) {
            if (node.getParent() != null) ((Pane) node.getParent()).getChildren().remove(node);
            if (!target.getChildren().contains(node)) target.getChildren().add(node);
        }
    }

    public static void removeNode(Pane parent, Node node) {
        if (node != null) {
            parent.getChildren().remove(node);
        }
    }

    public static Point2D calculatePopupPosition(Node source, Pane owner) {
        double posX = source.getLayoutX() + owner.getScene().getWindow().getX();
        double posY = source.getLayoutY() + owner.getScene().getWindow().getY();

        return new Point2D(posX, posY);
    }

    public static Point2D calculateTablePosition(Node source, Pane owner) {
        double posX = source.getLayoutX() - owner.getScene().getWindow().getX();
        double posY = source.getLayoutY() - owner.getScene().getWindow().getY() - LAYOUT_OFFSET_Y;

        return new Point2D(posX, posY);
    }

    public static void showPopup(Popup popup, Controller controller, Node parent, Point2D position) {
        controller.getRootNode().setLayoutX(position.getX());
        controller.getRootNode().setLayoutY(position.getY());
        popup.show(parent, 0, 0);
        popup.getContent().forEach(node -> node.setVisible(true));
    }
}
