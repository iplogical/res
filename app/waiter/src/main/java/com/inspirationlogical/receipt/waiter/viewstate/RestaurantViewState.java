package com.inspirationlogical.receipt.waiter.viewstate;

import com.inspirationlogical.receipt.corelib.frontend.viewstate.MotionViewState;
import com.inspirationlogical.receipt.corelib.frontend.viewstate.ViewState;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;

import javafx.beans.property.BooleanProperty;
import lombok.Data;

@Data
public class RestaurantViewState implements ViewState {
    private TableType tableType;
    private BooleanProperty configurable;
    private MotionViewState motionViewState;

    public RestaurantViewState() {
        motionViewState = new MotionViewState();
    }

    public TableType getTableType() {
        return tableType;
    }

    public void setTableType(TableType tableType) {
        this.tableType = tableType;
    }
}
