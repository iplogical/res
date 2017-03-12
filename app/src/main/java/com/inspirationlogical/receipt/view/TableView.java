package com.inspirationlogical.receipt.view;

import com.inspirationlogical.receipt.utility.Wrapper;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class TableView {

    private Node view;

    public TableView(Node view, Pane parent, Point2D position) {
        this.view = view;
        configureView(parent, position);
    }

    private void configureView(Pane parent, Point2D position) {
        view.setLayoutX(position.getX());
        view.setLayoutY(position.getY());
        view.setFocusTraversable(false);

        final Wrapper<Point2D> deltaWrapper = new Wrapper<>();

        view.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Point2D delta = new Point2D(view.getLayoutX() - mouseEvent.getSceneX(),
                        view.getLayoutY() - mouseEvent.getSceneY());
                deltaWrapper.setContent(delta);
            }
        });

        view.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                view.setLayoutX(mouseEvent.getSceneX() + deltaWrapper.getContent().getX());
                view.setLayoutY(mouseEvent.getSceneY() + deltaWrapper.getContent().getY());
            }
        });

        parent.getChildren().add(view);
    }

    public Node getView() {
        return view;
    }
}
