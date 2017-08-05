package com.inspirationlogical.receipt.waiter.controller.restaurant;

import com.inspirationlogical.receipt.corelib.params.TableParams;
import com.inspirationlogical.receipt.waiter.controller.table.TableController;
import javafx.geometry.Point2D;
import javafx.scene.control.Control;

public interface TableConfigurationController {

    void showCreateTableForm(Point2D position);

    void showEditTableForm(Control control);

    void setViewLoader(com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader viewLoader);
    void setRestaurantController(RestaurantController restaurantController);
    void setRestaurantViewState(RestaurantViewState restaurantViewState);

    void createTable(TableParams tableParams);

    void editTable(TableController tableController, TableParams tableParams);

    void initialize();
}
