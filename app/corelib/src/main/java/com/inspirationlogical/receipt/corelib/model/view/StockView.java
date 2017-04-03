package com.inspirationlogical.receipt.corelib.model.view;

import java.time.LocalDateTime;

public interface StockView {

    ProductView getProduct();

    double getSoldQuantity();

    double getInitialQuantity();

    LocalDateTime getDate();
}
