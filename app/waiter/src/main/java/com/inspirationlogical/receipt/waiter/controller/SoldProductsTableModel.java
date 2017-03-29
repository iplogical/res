package com.inspirationlogical.receipt.waiter.controller;

import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import lombok.Builder;
import lombok.Data;

import java.util.Comparator;

/**
 * Created by Bálint on 2017.03.22..
 */
public @Data class SoldProductsTableModel {
    public SoldProductsTableModel(String productName,
                                  String productQuantity,
                                  String productUnitPrice,
                                  String productTotalPrice,
                                  String productId,
                                  String productDiscount,
                                  String productVat) {
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productUnitPrice = productUnitPrice;
        this.productTotalPrice = productTotalPrice;
        this.productId = productId;
        this.productDiscount = productDiscount;
        this.productVat = productVat;
    }

    public SoldProductsTableModel(SoldProductsTableModel other) {
        this.productName = other.productName;
        this.productQuantity = other.productQuantity;
        this.productUnitPrice = other.productUnitPrice;
        this.productTotalPrice = other.productTotalPrice;
        this.productId = other.productId;
        this.productDiscount = other.productDiscount;
        this.productVat = other.productVat;
    }
    
    String productName;

    String productQuantity;

    String productUnitPrice;

    String productTotalPrice;

    String productId;

    String productDiscount;

    String productVat;

    boolean isSingleProduct() {
        double quantity = Double.valueOf(productQuantity);
        if(quantity <= 1.0) {
            return true;
        }
        return false;
    }

    boolean decreaseProductQuantity(double amount) {
        double quantity = Double.valueOf(productQuantity);
        if(quantity <= amount) {
            return true;
        }
        productQuantity = String.valueOf(roundToTwoDecimals(quantity - amount));
        int totalPrice = (int)(Integer.valueOf(productUnitPrice) * Double.valueOf(productQuantity));
        productTotalPrice = String.valueOf(totalPrice);
        return false;
    }

    void increaseProductQuantity(double amount) {
        double quantity = Double.valueOf(productQuantity);
        productQuantity = String.valueOf(roundToTwoDecimals(quantity + amount));
        int totalPrice = (int)(Integer.valueOf(productUnitPrice) * Double.valueOf(productQuantity));
        productTotalPrice = String.valueOf(totalPrice);
    }

    public static boolean isEquals(SoldProductsTableModel row, ReceiptRecordView receiptRecordView) {
        return String.valueOf(receiptRecordView.getId()).equals(row.getProductId());
    }

    public static boolean isEquivalent(SoldProductsTableModel row, ReceiptRecordView receiptRecordView) {
        return String.valueOf(receiptRecordView.getName()).equals(row.getProductName()) &&
                String.valueOf(receiptRecordView.getDiscountPercent()).equals(row.getProductDiscount()) &&
                String.valueOf(receiptRecordView.getVat()).equals(row.getProductVat());
    }

    public static Comparator<SoldProductsTableModel> compareById = (o1, o2) -> o1.getProductId().compareTo(o2.getProductId());

    public static int getTotalPrice(ObservableList<SoldProductsTableModel> paidProductsModel) {
        int totalPrice = 0;
        for(SoldProductsTableModel model : paidProductsModel) {
            totalPrice += Integer.valueOf(model.getProductTotalPrice());
        }
        return totalPrice;
    }

    private static double roundToTwoDecimals(double number) {
        double rounded = Math.round(number * 100);
        return rounded / 100;
    }
}
