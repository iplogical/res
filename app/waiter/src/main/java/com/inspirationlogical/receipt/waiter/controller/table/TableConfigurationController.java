package com.inspirationlogical.receipt.waiter.controller.table;

import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.TableParams;
import com.inspirationlogical.receipt.waiter.controller.table.TableController;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Control;

import java.util.Set;

public interface TableConfigurationController {

    void initialize();

    void showCreateTableForm(Point2D position);

    void showEditTableForm(Control control);

    void addTable(TableParams tableParams);

    void editTable(TableController tableController, TableParams tableParams);

    void deleteTable(Node node);

    void rotateTable(Node node);

    void moveTable(TableController tableController);

    void moveTable(TableView tableView, Point2D position);

    void mergeTables();

    void splitTables(Node node);

    void exchangeTables();

    void selectTable(TableController tableController, boolean selected);

    void clearSelections();

    boolean hasSelection();

    void openTableOfReservation(Integer number, String name, Integer guestCount, String note);

    void drawTable(TableView tableView);

    TableController getTableController(TableView tableView);

    Set<TableController> getTableControllers();
}
