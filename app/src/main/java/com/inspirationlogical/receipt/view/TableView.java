package com.inspirationlogical.receipt.view;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class TableView {

    private Button button;

    public TableView(Pane parent, String name, Point2D position) {
        button = new Button(name);
        button.setLayoutX(position.getX());
        button.setLayoutY(position.getY());
        button.setMinHeight(100.0);
        button.setMinWidth(100.0);
        final Delta dragDelta = new Delta();

        button.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // record a delta distance for the drag and drop operation.
                dragDelta.x = button.getLayoutX() - mouseEvent.getSceneX();
                dragDelta.y = button.getLayoutY() - mouseEvent.getSceneY();
                button.setCursor(Cursor.MOVE);
            }
        });

        button.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                button.setCursor(Cursor.HAND);
            }
        });

        button.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                button.setLayoutX(mouseEvent.getSceneX() + dragDelta.x);
                button.setLayoutY(mouseEvent.getSceneY() + dragDelta.y);
            }
        });

        button.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                button.setCursor(Cursor.HAND);
            }
        });

        parent.getChildren().add(button);
    }

    class Delta {
        double x, y;
    }

    public Button getView() {
        return button;
    }
}
