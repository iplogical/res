package com.inspirationlogical.receipt.manager.viewmodel;

import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;

import lombok.Data;

@Data
public class ReceiptRecordViewModel {
    private String name = EMPTY;
    private String type = EMPTY;
    private String soldQuantity = EMPTY;
    private String absoluteQuantity = EMPTY;
    private String purchasePrice = EMPTY;
    private String salePrice = EMPTY;
    private String VAT = EMPTY;
    private String discountPercent = EMPTY;

    public ReceiptRecordViewModel(ReceiptRecordView receiptRecordView) {
        if (receiptRecordView != null) {
            name = receiptRecordView.getName();
            type = valueOf(receiptRecordView.getType());
            soldQuantity = valueOf(receiptRecordView.getSoldQuantity());
            absoluteQuantity = valueOf(receiptRecordView.getAbsoluteQuantity());
            purchasePrice = valueOf(receiptRecordView.getPurchasePrice());
            salePrice = valueOf(receiptRecordView.getSalePrice());
            VAT = valueOf(receiptRecordView.getVat());
            discountPercent = valueOf(receiptRecordView.getDiscountPercent());
        }
    }
}
