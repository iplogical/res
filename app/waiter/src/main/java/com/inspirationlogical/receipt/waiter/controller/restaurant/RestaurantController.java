package com.inspirationlogical.receipt.waiter.controller.restaurant;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.TableParams;
import com.inspirationlogical.receipt.waiter.controller.table.TableController;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;

public interface RestaurantController extends Controller {

    void openTableOfReservation(Integer number, String name, Integer guestCount, String note);

    void updateRestaurant();

    RestaurantViewState getViewState();

    void addNodeToPane(Node node, TableType tableType);

    Pane getActiveTab();

    Pane getTab(TableType tableType);

    void updateRestaurantSummary();
}