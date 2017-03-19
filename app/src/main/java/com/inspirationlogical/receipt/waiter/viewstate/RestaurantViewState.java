package com.inspirationlogical.receipt.waiter.viewstate;

public class RestaurantViewState implements ViewState {

    private boolean configurationEnabled;

    public boolean isConfigurationEnabled() {
        return configurationEnabled;
    }

    public void setConfigurationEnabled(boolean configurationEnabled) {
        this.configurationEnabled = configurationEnabled;
    }
}
