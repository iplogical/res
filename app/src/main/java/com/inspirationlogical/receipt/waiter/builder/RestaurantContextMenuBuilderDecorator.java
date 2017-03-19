package com.inspirationlogical.receipt.waiter.builder;

import com.inspirationlogical.receipt.waiter.controller.RestaurantController;
import com.inspirationlogical.receipt.waiter.viewstate.RestaurantViewState;
import com.inspirationlogical.receipt.waiter.viewstate.ViewState;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;

public class RestaurantContextMenuBuilderDecorator extends ContextMenuBuilderDecorator {

    private RestaurantController restaurantController;

    public RestaurantContextMenuBuilderDecorator(RestaurantController restaurantController, ContextMenuBuilder b) {
        super(b);
        this.restaurantController = restaurantController;
    }


    @Override
    public ContextMenu build(ViewState viewState) {
        RestaurantViewState restaurantViewState = (RestaurantViewState) viewState;
        ContextMenu contextMenu = super.build(viewState);
        MenuItem menuItem = new MenuItem();
        Label label = new Label("Asztal hozzáadása " + restaurantViewState.isConfigurationEnabled());
        label.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            restaurantController.showAddTableForm(new Point2D(event.getSceneX(), event.getSceneY()));
        });
        menuItem.setGraphic(label);
        contextMenu.getItems().add(contextMenu.getItems().size(), menuItem);
        return contextMenu;
    }
}
