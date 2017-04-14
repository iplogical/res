package com.inspirationlogical.receipt.waiter.viewmodel;

import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import javafx.collections.ObservableList;
import lombok.Data;

import java.util.Comparator;

import static com.inspirationlogical.receipt.corelib.utility.Round.roundToTwoDecimals;
import static java.lang.String.valueOf;

/**
 * Created by Bálint on 2017.03.22..
 */
public @Data class SoldProductViewModel {

    private String productName;

    private String productQuantity;

    private String productUnitPrice;

    private String productTotalPrice;

    private String productId;

    private String productDiscount;

    private String productVat;

    public SoldProductViewModel(ReceiptRecordView receiptRecordView) {
        this.productName = receiptRecordView.getName();
        this.productQuantity = valueOf(receiptRecordView.getSoldQuantity());
        this.productUnitPrice = valueOf(receiptRecordView.getSalePrice());
        this.productTotalPrice = valueOf(receiptRecordView.getTotalPrice());
        this.productId = valueOf(receiptRecordView.getId());
        this.productDiscount = valueOf(receiptRecordView.getDiscountPercent());
        this.productVat = valueOf(receiptRecordView.getVat());
        markDiscountedProduct();
    }

    public SoldProductViewModel(SoldProductViewModel other) {
        this.productName = other.productName;
        this.productQuantity = other.productQuantity;
        this.productUnitPrice = other.productUnitPrice;
        this.productTotalPrice = other.productTotalPrice;
        this.productId = other.productId;
        this.productDiscount = other.productDiscount;
        this.productVat = other.productVat;
        markDiscountedProduct();
    }

    public boolean isSingleProduct() {
        double quantity = Double.valueOf(productQuantity);
        if(quantity <= 1.0) {
            return true;
        }
        return false;
    }

    public boolean decreaseProductQuantity(double amount) {
        double quantity = Double.valueOf(productQuantity);
        if(quantity <= amount) {
            return true;
        }
        productQuantity = valueOf(roundToTwoDecimals(quantity - amount));
        int totalPrice = (int)(Integer.valueOf(productUnitPrice) * Double.valueOf(productQuantity));
        productTotalPrice = valueOf(totalPrice);
        return false;
    }

    public void increaseProductQuantity(double amount) {
        double quantity = Double.valueOf(productQuantity);
        productQuantity = valueOf(roundToTwoDecimals(quantity + amount));
        int totalPrice = (int)(Integer.valueOf(productUnitPrice) * Double.valueOf(productQuantity));
        productTotalPrice = valueOf(totalPrice);
    }

    public static boolean isEquals(SoldProductViewModel row, ReceiptRecordView receiptRecordView) {
        return valueOf(receiptRecordView.getId()).equals(row.getProductId());
    }

    public static boolean isEquivalent(SoldProductViewModel row, ReceiptRecordView receiptRecordView) {
        return valueOf(receiptRecordView.getName()).equals(row.getProductName()) &&
                valueOf(receiptRecordView.getDiscountPercent()).equals(row.getProductDiscount()) &&
                valueOf(receiptRecordView.getVat()).equals(row.getProductVat());
    }

    public static Comparator<SoldProductViewModel> compareById = (o1, o2) -> o1.getProductId().compareTo(o2.getProductId());

    public static int getTotalPrice(ObservableList<SoldProductViewModel> paidProductsModel) {
        int totalPrice = 0;
        for(SoldProductViewModel model : paidProductsModel) {
            totalPrice += Integer.valueOf(model.getProductTotalPrice());
        }
        return totalPrice;
    }

    private void markDiscountedProduct() {
        if(!this.productDiscount.equals(valueOf(0D))) {
            if(this.productName.contains("*")) {
                return;
            }
            this.productName = this.productName + " *";
        }
    }
}
