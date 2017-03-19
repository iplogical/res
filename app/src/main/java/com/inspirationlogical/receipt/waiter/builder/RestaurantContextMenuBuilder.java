package com.inspirationlogical.receipt.waiter.builder;

import com.inspirationlogical.receipt.waiter.viewstate.ViewState;

import javafx.scene.control.ContextMenu;

public class RestaurantContextMenuBuilder implements ContextMenuBuilder {

    public RestaurantContextMenuBuilder() {
        super();
    }


    @Override
    public ContextMenu build(ViewState viewState) {
        // add restaurant specific menu items
        return new ContextMenu();
    }
}
