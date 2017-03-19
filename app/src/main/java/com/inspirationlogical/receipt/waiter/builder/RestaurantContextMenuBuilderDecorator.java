package com.inspirationlogical.receipt.waiter.builder;

import com.inspirationlogical.receipt.waiter.viewstate.ViewState;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class RestaurantContextMenuBuilderDecorator extends ContextMenuBuilderDecorator {

    public RestaurantContextMenuBuilderDecorator(ContextMenuBuilder b) {
        super(b);
    }


    @Override
    public ContextMenu build(ViewState viewState) {
        // add restaurant specific menu items
        ContextMenu c =super.build(viewState);
        c.getItems().add(c.getItems().size(),new MenuItem("Restaurant Menu Item"));
        return c;
    }
}
