package com.inspirationlogical.receipt.waiter.builder;

import com.inspirationlogical.receipt.waiter.viewstate.ViewState;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

/**
 * Created by Ferenc on 2017. 03. 19..
 */
public class BaseContextMenuBuilder implements ContextMenuBuilder{
    @Override
    public ContextMenu build(ViewState viewState) {
        ContextMenu c =new ContextMenu();
        c.getItems().add(0,new MenuItem("Common menuitem"));
        return c;
    }
}
