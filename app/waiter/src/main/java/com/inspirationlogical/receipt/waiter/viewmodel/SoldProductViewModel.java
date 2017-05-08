package com.inspirationlogical.receipt.waiter.viewmodel;

import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import javafx.collections.ObservableList;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.inspirationlogical.receipt.corelib.utility.Round.roundToTwoDecimals;
import static java.lang.String.valueOf;
import static java.time.LocalDateTime.now;

/**
 * Created by Bálint on 2017.03.22..
 */
public @Data class SoldProductViewModel {

    private String productName;

    private String productQuantity;

    private String recentQuantity;

    private String productUnitPrice;

    private String productTotalPrice;

    private String productId;

    private String productDiscount;

    private String productVat;

    private List<LocalDateTime> clickTimes;

    public static boolean isEquals(SoldProductViewModel row, ReceiptRecordView receiptRecordView) {
        return valueOf(receiptRecordView.getId()).equals(row.getProductId());
    }

    public static boolean isEquivalent(SoldProductViewModel row, ReceiptRecordView receiptRecordView) {
        return valueOf(receiptRecordView.getName()).equals(row.getProductNameWithoutStar()) &&
                valueOf(receiptRecordView.getDiscountPercent()).equals(row.getProductDiscount()) &&
                valueOf(receiptRecordView.getVat()).equals(row.getProductVat());
    }

    public static int getTotalPrice(ObservableList<SoldProductViewModel> paidProductsModel) {
        int totalPrice = 0;
        for(SoldProductViewModel model : paidProductsModel) {
            totalPrice += Integer.valueOf(model.getProductTotalPrice());
        }
        return totalPrice;
    }
    
    public SoldProductViewModel(ReceiptRecordView receiptRecordView) {
        this.clickTimes = receiptRecordView.getCreated();
        this.productName = receiptRecordView.getName();
        this.productQuantity = valueOf(receiptRecordView.getSoldQuantity()) + (getRecentClickCount() == 0 ? "" : (" (" + getRecentClickCount() + ")"));
        this.productUnitPrice = valueOf(receiptRecordView.getSalePrice());
        this.productTotalPrice = valueOf(receiptRecordView.getTotalPrice());
        this.productId = valueOf(receiptRecordView.getId());
        this.productDiscount = valueOf(receiptRecordView.getDiscountPercent());
        this.productVat = valueOf(receiptRecordView.getVat());
        markDiscountedProduct();
    }

    public SoldProductViewModel(SoldProductViewModel other) {
        this.clickTimes = other.clickTimes;
        this.productName = other.productName;
        this.productQuantity = other.productQuantity;
        this.productUnitPrice = other.productUnitPrice;
        this.productTotalPrice = other.productTotalPrice;
        this.productId = other.productId;
        this.productDiscount = other.productDiscount;
        this.productVat = other.productVat;
        markDiscountedProduct();
    }

    public boolean decreaseProductQuantity(double amount) {
        double quantity = Double.valueOf(productQuantity.split(" ")[0]);
        if(quantity <= amount) {
            return true;
        }
        productQuantity = valueOf(roundToTwoDecimals(quantity - amount));
        int totalPrice = (int)(Integer.valueOf(productUnitPrice) * (quantity - amount));
        productTotalPrice = valueOf(totalPrice);
        return false;
    }

    public void increaseProductQuantity(double amount) {
        double quantity = Double.valueOf(productQuantity.split(" ")[0]);
        long recent = getRecentClickCount();
        int totalPrice = (int)(Integer.valueOf(productUnitPrice) * (quantity + amount));
        productQuantity = valueOf(roundToTwoDecimals(quantity + amount)) + " (" + valueOf(recent) + ")";
        productTotalPrice = valueOf(totalPrice);
    }

    public String getProductQuantity() {
        return productQuantity.split(" ")[0];
    }

    public String getProductQuantityWithRecent() {
        return productQuantity;
    }

    public String getProductNameWithoutStar() {
        String name = productName.replaceAll("[*]","").trim();
        return name;
    }

    public LocalDateTime getLatestClickTime() {
        if(clickTimes.size() == 0) return now();
        return clickTimes.get(clickTimes.size() - 1);
    }

    private long getRecentClickCount() {
        return clickTimes.stream().filter(localDateTime -> localDateTime.isAfter(now().minusMinutes(20))).count();
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
