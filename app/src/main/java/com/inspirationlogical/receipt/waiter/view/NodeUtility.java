package com.inspirationlogical.receipt.waiter.view;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class NodeUtility {

    public static Point2D getNodePosition(Node node) {
        return new Point2D(node.getLayoutX(), node.getLayoutY());
    }

    public static void showNode(Node node, Point2D position) {
        node.setLayoutX(position.getX());
        node.setLayoutY(position.getY());
        node.toFront();
        node.setVisible(true);
    }

    public static void hideNode(Node node) {
        node.setVisible(false);
    }

    public static void moveNode(Pane source, Pane target, Node node) {
        if (node != null) {
            if (source.getChildren().contains(node)) source.getChildren().remove(node);
            if (!target.getChildren().contains(node)) target.getChildren().add(node);
        }
    }
}
