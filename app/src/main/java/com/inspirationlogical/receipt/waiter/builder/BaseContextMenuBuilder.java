package com.inspirationlogical.receipt.waiter.builder;

import com.inspirationlogical.receipt.waiter.viewstate.ViewState;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

/**
 * Created by Ferenc on 2017. 03. 19..
 */
public class BaseContextMenuBuilder implements ContextMenuBuilder{
    @Override
    public ContextMenu build(ViewState viewState) {
        ContextMenu c =new ContextMenu();
        MenuItem menuItem = new MenuItem();
        Label label = new Label("Common menuitem");
        menuItem.setGraphic(label);
        c.getItems().add(0, menuItem);
        return c;
    }
}
