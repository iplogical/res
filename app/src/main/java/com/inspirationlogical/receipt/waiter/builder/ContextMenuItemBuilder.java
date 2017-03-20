package com.inspirationlogical.receipt.waiter.builder;

import java.util.function.Consumer;

import javafx.geometry.Point2D;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;

public class ContextMenuItemBuilder {
    private MenuItem menuItem;

    public ContextMenuItemBuilder() {
        menuItem = new MenuItem();
    }

    public ContextMenuItemBuilder withLabel(String text) {
        menuItem.setText(text);
        return this;
    }

    public ContextMenuItemBuilder withClickHandlerPoint2D(Consumer<Point2D> consumer) {
        menuItem.setOnAction(event -> {
            consumer.accept(new Point2D(menuItem.getParentPopup().getAnchorX(), menuItem.getParentPopup().getAnchorY()));
        });
        return this;
    }

    public ContextMenuItemBuilder withClickHandlerControl(Consumer<Control> consumer) {
        menuItem.setOnAction(event -> {
            Control control = (Control) menuItem.getParentPopup().getUserData();
            consumer.accept(control);
        });
        return this;
    }

    public MenuItem build() {
        return menuItem;
    }

}
