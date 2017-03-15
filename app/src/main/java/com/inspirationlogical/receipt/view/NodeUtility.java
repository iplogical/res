package com.inspirationlogical.receipt.view;

import javafx.geometry.Point2D;
import javafx.scene.Node;

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
}
