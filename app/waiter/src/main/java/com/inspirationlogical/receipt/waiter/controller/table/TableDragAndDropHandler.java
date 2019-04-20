package com.inspirationlogical.receipt.waiter.controller.table;

import com.inspirationlogical.receipt.corelib.utility.Wrapper;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantController;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import lombok.Setter;

import static com.inspirationlogical.receipt.corelib.frontend.view.DragAndDropHandler.savePosition;
import static com.inspirationlogical.receipt.corelib.frontend.view.DragAndDropHandler.updatePosition;

public class TableDragAndDropHandler {

    @Setter
    static RestaurantController restaurantController;

    static void addTableDragAndDrop(Node view) {
        final Wrapper<Point2D> deltaWrapper = new Wrapper<>();

        view.setOnMousePressed(mouseEvent -> {
            if (restaurantController.isMotionMode()) {
                savePosition(mouseEvent, deltaWrapper);
            }
        });

        view.setOnMouseDragged(mouseEvent -> {
            if (restaurantController.isMotionMode()) {
                updatePosition(mouseEvent, deltaWrapper);
            }
        });
    }
}
