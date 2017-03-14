package com.inspirationlogical.receipt.controller;

import static java.lang.String.valueOf;

import java.net.URL;
import java.util.ResourceBundle;

import com.inspirationlogical.receipt.model.view.TableView;
import com.inspirationlogical.receipt.utility.Wrapper;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class TableControllerImpl implements TableController {

    public static final String TABLE_VIEW_PATH = "/view/fxml/Table.fxml";
    private static double TABLE_WIDTH = 100.0;
    private static double TABLE_HEIGHT = 100.0;

    @FXML
    VBox view;

    @FXML
    Label name;

    @FXML
    Label number;

    @FXML
    Label guests;

    @FXML
    Label capacity;

    private TableView tableView;

    public TableControllerImpl(TableView tableView) {
        this.tableView = tableView;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        initHandlers();

        initVisual();

        initData();
    }

    private void initHandlers() {
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
    }

    private void initVisual() {
        Point2D position = tableView.getPosition();

        view.setLayoutX(position.getX());
        view.setLayoutY(position.getY());
        view.setMinWidth(TABLE_WIDTH);
        view.setMinHeight(TABLE_HEIGHT);
    }

    private void initData() {
        name.setText(tableView.getName());
        number.setText(valueOf(tableView.getTableNumber()));
        guests.setText(valueOf(tableView.getGuestCount()));
        capacity.setText(valueOf(tableView.getCapacity()));
    }

    @Override
    public Node getView() {
        return view;
    }
}
