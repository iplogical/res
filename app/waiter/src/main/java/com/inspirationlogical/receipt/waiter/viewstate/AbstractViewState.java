package com.inspirationlogical.receipt.waiter.viewstate;

import lombok.Data;

/**
 * Created by Bálint on 2017.03.23..
 */
public abstract @Data class AbstractViewState implements ViewState {

    private boolean configurable;

    private boolean fullScreen;

}
