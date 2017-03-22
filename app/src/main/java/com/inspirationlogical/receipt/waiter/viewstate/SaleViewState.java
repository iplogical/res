package com.inspirationlogical.receipt.waiter.viewstate;

import lombok.Data;

public @Data class SaleViewState implements ViewState {

    private boolean configurable;

    private boolean fullScreen;
}
