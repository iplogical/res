package com.inspirationlogical.receipt.waiter.visitor;

import com.inspirationlogical.receipt.waiter.builder.ContextMenuBuilder;
import com.inspirationlogical.receipt.waiter.builder.RestaurantContextMenuBuilder;
import com.inspirationlogical.receipt.waiter.builder.TableContextMenuBuilderDecorator;
import com.inspirationlogical.receipt.waiter.decorator.TableContextMenuDecorator;
import com.inspirationlogical.receipt.waiter.viewstate.RestaurantViewState;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class ContextMenuVisitorImpl implements ContextMenuVisitor {

    @Override
    public ContextMenuBuilder visit(VBox table, RestaurantViewState restaurantViewState) {

        ContextMenuBuilder contextMenuBuilder = new TableContextMenuBuilderDecorator(new RestaurantContextMenuBuilder());

        return decorateWithConfiguration(contextMenuBuilder, restaurantViewState.isConfigurationEnabled())
                .addCreateTableMenuItem()
                .addEditTableMenuItem();
    }

    @Override
    public ContextMenuBuilder visit(AnchorPane restaurant, RestaurantViewState restaurantViewState) {

        ContextMenuBuilder contextMenuBuilder = new RestaurantContextMenuBuilder();

        return decorateWithConfiguration(contextMenuBuilder, restaurantViewState.isConfigurationEnabled());
    }

    private ContextMenuBuilder decorateWithConfiguration(ContextMenuBuilder contextMenuBuilder, boolean configurationEnabled) {
        if (configurationEnabled) {
            return new TableContextMenuDecorator(contextMenuBuilder);
        } else {
            return contextMenuBuilder;
        }
    }
}
