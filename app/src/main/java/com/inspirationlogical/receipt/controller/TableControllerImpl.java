package com.inspirationlogical.receipt.controller;

import static com.inspirationlogical.receipt.view.DragAndDropHandler.addDragAndDrop;
import static com.inspirationlogical.receipt.view.NodeUtility.showNode;
import static java.lang.String.valueOf;

import java.net.URL;
import java.util.ResourceBundle;

import com.inspirationlogical.receipt.model.view.TableView;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
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
        addDragAndDrop(view);
        initVisual();
        initData();
    }

    private void initVisual() {
        view.setMinWidth(TABLE_WIDTH);
        view.setMinHeight(TABLE_HEIGHT);
        showNode(view, tableView.getPosition());
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
