package com.inspirationlogical.receipt.waiter.controller.restaurant;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public interface RestaurantController extends Controller {

    void addNodeToPane(Node node, TableType tableType);

    Pane getActiveTab();

    TableType getTableType();

    boolean isConfigurationMode();

    boolean isMotionMode();
}