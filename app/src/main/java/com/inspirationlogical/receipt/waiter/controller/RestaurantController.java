package com.inspirationlogical.receipt.waiter.controller;

import javafx.fxml.Initializable;
import javafx.scene.Node;

public interface RestaurantController extends Initializable {
    void showAddTableForm();
    void createTable(int number, int capacity, boolean isVirtual);
    void deleteTable(Node node);
}
