package com.inspirationlogical.receipt.waiter.controller.table;

import com.inspirationlogical.receipt.corelib.model.view.ReservationView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.TableParams;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Control;

public interface TableConfigurationController {

    void initialize();

    void showCreateTableForm(Point2D position);

    void showEditTableForm(Control control);

    void hideTableForm();

    void addTable(TableParams tableParams);

    void editTable(TableController tableController, TableParams tableParams);

    void deleteTable(Node node);

    void rotateTable(Node node);

    void moveTable(TableController tableController);

    void exchangeTables();

    void clearSelections();

    void openTableOfReservation(ReservationView reservation);

    void drawTable(TableView tableView);

    TableController getTableController(int tableNumber);
}
