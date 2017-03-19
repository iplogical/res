package com.inspirationlogical.receipt.waiter.controller;

import static com.inspirationlogical.receipt.waiter.view.DragAndDropHandler.addDragAndDrop;
import static com.inspirationlogical.receipt.waiter.view.NodeUtility.showNode;
import static java.lang.String.valueOf;

import java.net.URL;
import java.util.ResourceBundle;

import com.inspirationlogical.receipt.corelib.model.view.TableView;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class TableControllerImpl implements TableController {

    public static final String TABLE_VIEW_PATH = "/view/fxml/Table.fxml";
    private static double TABLE_WIDTH = 100.0;
    private static double TABLE_HEIGHT = 100.0;

    @FXML
    VBox node;

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
        addDragAndDrop(node);
        initVisual();
        updateNode();
    }

    private void initVisual() {
        node.setMinWidth(TABLE_WIDTH);
        node.setMinHeight(TABLE_HEIGHT);
    }

    @Override
    public TableView getView() {
        return tableView;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public void updateNode() {
        name.setText(tableView.getName());
        number.setText(valueOf(tableView.getTableNumber()));
        guests.setText(valueOf(tableView.getGuestCount()));
        capacity.setText(valueOf(tableView.getTableCapacity()));
        showNode(node, tableView.getPosition());
    }
}
