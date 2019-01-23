package com.inspirationlogical.receipt.waiter.controller.restaurant;

import com.inspirationlogical.receipt.corelib.frontend.viewstate.MotionViewState;
import com.inspirationlogical.receipt.corelib.frontend.viewstate.ViewState;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.waiter.controller.table.TableController;
import javafx.beans.property.BooleanProperty;
import lombok.Data;

import java.util.Set;

@Data
public class RestaurantViewState implements ViewState {
    private TableType tableType;
    private BooleanProperty configurable;
    private MotionViewState motionViewState;
    private Set<TableController> selectedTables;

    public RestaurantViewState(Set<TableController> selectedTables) {
        this.selectedTables = selectedTables;
        this.tableType = TableType.NORMAL;
        motionViewState = new MotionViewState();
    }
}
