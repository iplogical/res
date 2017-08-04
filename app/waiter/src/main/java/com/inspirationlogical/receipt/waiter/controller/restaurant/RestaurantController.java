package com.inspirationlogical.receipt.waiter.controller.restaurant;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.TableParams;
import com.inspirationlogical.receipt.waiter.controller.table.TableController;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Control;

public interface RestaurantController extends Controller {
    int getFirstUnusedTableNumber();
    void showCreateTableForm(Point2D position);
    void showEditTableForm(Control control);
    void createTable(TableParams params);
    void editTable(TableController tableController, TableParams params);
    void openTableOfReservation(Integer number, String name, Integer guestCount, String note);
    void deleteTable(Node node);
    void rotateTable(Node node);

    void moveTable(TableController tableController);

    void moveTable(TableView tableView, Point2D position);

    void mergeTables();
    void splitTables(Node node);
    void selectTable(TableController tableController, boolean selected);
    void updateRestaurant();
    RestaurantViewState getViewState();
    TableController getTableController(TableView tableView);
}