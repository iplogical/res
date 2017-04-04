package com.inspirationlogical.receipt.waiter.viewstate;

import javafx.beans.property.BooleanProperty;
import lombok.Data;

@Data
public class RestaurantViewState implements ViewState {
    private boolean virtual;
    private BooleanProperty configurable;
    private MotionViewState motionViewState;

    public RestaurantViewState() {
        motionViewState = new MotionViewState();
    }

    public boolean isVirtual() {
        return virtual;
    }

    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }
}
