package com.inspirationlogical.receipt.waiter.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import lombok.Builder;
import lombok.Data;

/**
 * Created by Bálint on 2017.03.22..
 */
public @Data class SoldProductsTableModel {
    public SoldProductsTableModel(String productName, String productQuantity, String productUnitPrice, String productTotalPrice) {
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productUnitPrice = productUnitPrice;
        this.productTotalPrice = productTotalPrice;
    }

    String productName;

    String productQuantity;

    String productUnitPrice;

    String productTotalPrice;
}
