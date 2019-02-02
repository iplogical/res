package com.inspirationlogical.receipt.waiter.viewmodel;

import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryFamily;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.valueOf;
import static java.time.LocalDateTime.now;

public @Data class ProductRowModel {

    private String productName;

    private String productQuantity;

    private String productUnitPrice;

    private String productTotalPrice;

    private int productId;

    private String productDiscount;

    private String productVat;

    private List<LocalDateTime> clickTimes;

    private LocalDateTime foodDeliveryTime;

    private LocalDateTime drinkDeliveryTime;

    private ProductCategoryFamily family;

    public boolean isEqual(ReceiptRecordView receiptRecordView) {
        return receiptRecordView.getId() == productId;
    }

    public boolean isEquivalent(ReceiptRecordView receiptRecordView) {
        return valueOf(receiptRecordView.getName()).equals(getProductNameWithoutStar()) &&
                valueOf(receiptRecordView.getDiscountPercent()).equals(productDiscount) &&
                valueOf(receiptRecordView.getVat()).equals(productVat);
    }

    public ProductRowModel(ReceiptRecordView receiptRecordView, LocalDateTime foodDeliveryTime, LocalDateTime drinkDeliveryTime) {
        this.clickTimes = receiptRecordView.getCreated();
        this.productName = receiptRecordView.getName();
        this.foodDeliveryTime = foodDeliveryTime;
        this.drinkDeliveryTime = drinkDeliveryTime;
        this.family = receiptRecordView.getFamily();
        this.productQuantity = receiptRecordView.getSoldQuantity() + (getRecentClickCount() == 0 ? "" : (" (" + getRecentClickCount() + ")"));
        this.productUnitPrice = valueOf(receiptRecordView.getSalePrice());
        this.productTotalPrice = valueOf(receiptRecordView.getTotalPrice());
        this.productId = receiptRecordView.getId();
        this.productDiscount = valueOf(receiptRecordView.getDiscountPercent());
        this.productVat = valueOf(receiptRecordView.getVat());
        markDiscountedProduct();
    }

    public String getProductQuantity() {
        return productQuantity.split(" ")[0];
    }

    public String getProductQuantityWithRecent() {
        return productQuantity;
    }

    private String getProductNameWithoutStar() {
        return productName.replaceAll("[*]","").trim();
    }

    private long getRecentClickCount() {
        LocalDateTime boundaryTime;
        LocalDateTime deliveryTime = family.equals(ProductCategoryFamily.FOOD) ? foodDeliveryTime : drinkDeliveryTime;
        boundaryTime = getBoundaryTime(deliveryTime);
        return clickTimes.stream().filter(localDateTime -> localDateTime.isAfter(boundaryTime)).count();
    }

    private LocalDateTime getBoundaryTime(LocalDateTime deliveryTime) {
        LocalDateTime boundaryTime;
        if(deliveryTime == null) {
            boundaryTime = now().minusMinutes(30);
        } else {
            boundaryTime = deliveryTime.isAfter(now().minusMinutes(30)) ? deliveryTime : now().minusMinutes(30);
        }
        return boundaryTime;
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
