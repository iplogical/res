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
    private boolean hasSelection;

    public RestaurantViewState() {
        motionViewState = new MotionViewState();
    }
}
