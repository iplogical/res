package com.inspirationlogical.receipt.waiter.controller;

import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Control;

public interface RestaurantController extends Initializable {
    void showCreateTableForm(Point2D position);
    void showEditTableForm(Control control);
    void createTable(int tableNumber, int tableCapacity, boolean isVirtual);
    void editTable(TableController tableController, Integer tableNumber, Integer tableCapacity, boolean isVirtual);
    void deleteTable(Node node);
    void mergeTables(Node node, int consumedTableNumber);
    void splitTables(Node node, int producedTableNumber);
}
