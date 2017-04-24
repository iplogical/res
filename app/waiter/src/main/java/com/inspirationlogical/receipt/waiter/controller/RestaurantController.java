package com.inspirationlogical.receipt.waiter.controller;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.waiter.viewstate.RestaurantViewState;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Control;

public interface RestaurantController extends Controller {
    void showCreateTableForm(Point2D position);
    void showEditTableForm(Control control);
    void createTable(Integer number, Integer capacity, Dimension2D dimension);
    void editTable(TableController tableController, String name, Integer guestCount, String note,
                   Integer number, Integer capacity, Dimension2D dimensions);
    void deleteTable(Node node);
    void rotateTable(Node node);
    void moveTable(TableController tableController);

    void moveTable(TableView tableView, Point2D position);

    void mergeTables();
    void splitTables(Node node);
    void selectTable(TableController tableController, boolean selected);
    void updateRestaurant();
    RestaurantViewState getViewState();
}