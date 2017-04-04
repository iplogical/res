package com.inspirationlogical.receipt.waiter.viewstate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import lombok.Data;

@Data
public class MotionViewState {

    private BooleanProperty movableProperty;

    private BooleanProperty snapToGridProperty;

    private DoubleProperty gridSizeProperty;
}
