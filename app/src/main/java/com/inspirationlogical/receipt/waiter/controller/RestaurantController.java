package com.inspirationlogical.receipt.waiter.controller;

import javafx.fxml.Initializable;
import javafx.scene.Node;

public interface RestaurantController extends Initializable {
    void showAddTableForm();
    void showEditTableForm(Node node);
    void createTable(int tableNumber, int tableCapacity, boolean isVirtual);
    void editTable(TableController tableController, Integer tableNumber, Integer tableCapacity, boolean isVirtual);
    void deleteTable(Node node);
    void mergeTables(Node node, int consumedTableNumber);
    void splitTables(Node node, int producedTableNumber);
}
