package com.inspirationlogical.receipt.waiter.controller;

import com.inspirationlogical.receipt.waiter.viewstate.RestaurantViewState;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Control;

public interface RestaurantController extends Controller {
    void showCreateTableForm(Point2D position);
    void showEditTableForm(Control control);
    void createTable(int tableNumber, int tableCapacity, boolean isVirtual);
    void editTable(TableController tableController, Integer tableNumber, Integer tableCapacity, boolean isVirtual);
    void deleteTable(Node node);
    void mergeTables();
    void selectTable(TableController tableController, boolean selected);
    RestaurantViewState getViewState();
}