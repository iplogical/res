package com.inspirationlogical.receipt.waiter.controller.restaurant;

import java.util.Set;

import com.inspirationlogical.receipt.corelib.frontend.viewstate.MotionViewState;
import com.inspirationlogical.receipt.corelib.frontend.viewstate.ViewState;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.waiter.controller.table.TableController;

import javafx.beans.property.BooleanProperty;
import lombok.Data;

@Data
public class RestaurantViewState implements ViewState {
    private TableType tableType;
    private BooleanProperty configurable;
    private MotionViewState motionViewState;
//    private Set<TableController> selectedTables;

    public RestaurantViewState(Set<TableController> selectedTables) {
//        this.selectedTables = selectedTables;
        motionViewState = new MotionViewState();
    }

//    public boolean hasSelection() {
//        return selectedTables.size() > 1;
//    }
}
