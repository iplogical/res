package com.inspirationlogical.receipt.waiter.viewstate;

public class RestaurantViewState extends AbstractViewState {
    private boolean virtual;

    public boolean isVirtual() {
        return virtual;
    }

    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }
}
