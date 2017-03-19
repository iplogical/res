package com.inspirationlogical.receipt.waiter.controller;

import static com.inspirationlogical.receipt.waiter.view.DragAndDropHandler.addDragAndDrop;
import static com.inspirationlogical.receipt.waiter.view.NodeUtility.showNode;
import static java.lang.String.valueOf;

import java.net.URL;
import java.util.ResourceBundle;

import com.inspirationlogical.receipt.corelib.model.view.TableView;

import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.Label;

public class TableControllerImpl implements TableController {

    public static final String TABLE_VIEW_PATH = "/view/fxml/Table.fxml";
    private static double TABLE_WIDTH = 100.0;
    private static double TABLE_HEIGHT = 100.0;

    @FXML
    Label root;

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
        addDragAndDrop(root);
        initVisual();
        updateNode();
    }

    private void initVisual() {
        root.setMinWidth(TABLE_WIDTH);
        root.setMinHeight(TABLE_HEIGHT);
    }

    @Override
    public TableView getView() {
        return tableView;
    }

    @Override
    public Control getRoot() {
        return root;
    }

    @Override
    public void updateNode() {
        name.setText(tableView.getName());
        number.setText(valueOf(tableView.getTableNumber()));
        guests.setText(valueOf(tableView.getGuestCount()));
        capacity.setText(valueOf(tableView.getTableCapacity()));
        showNode(root, tableView.getPosition());
    }
}
