package com.inspirationlogical.receipt.waiter.builder;

import com.inspirationlogical.receipt.waiter.viewstate.ViewState;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class RestaurantContextMenuBuilder implements ContextMenuBuilder {

    public RestaurantContextMenuBuilder() {
        super();
    }


    @Override
    public ContextMenu build(ViewState viewState) {
        // add restaurant specific menu items
        ContextMenu c =new ContextMenu();
        c.getItems().add(0,new MenuItem("Lofasz"));
        return c;
    }
}
