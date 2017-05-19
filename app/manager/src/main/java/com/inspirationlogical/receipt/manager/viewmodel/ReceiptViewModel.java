package com.inspirationlogical.receipt.manager.viewmodel;

import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import com.inspirationlogical.receipt.corelib.model.entity.Client;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;

import lombok.Data;

@Data
public class ReceiptViewModel {
    private String date = EMPTY;
    private String type = EMPTY;
    private String status = EMPTY;
    private String paymentMethod = EMPTY;
    private String openTime = EMPTY;
    private String closureTime = EMPTY;
    private String userCode = EMPTY;
    private String sumPurchaseNetPrice = EMPTY;
    private String sumPurchaseGrossPrice = EMPTY;
    private String sumSaleNetPrice = EMPTY;
    private String sumSaleGrossPrice = EMPTY;
    private String discountPercent = EMPTY;
    private String VATSerie = EMPTY;
    private String clientName = EMPTY;
    private String clientAddress = EMPTY;
    private String clientTAXNumber = EMPTY;

    private List<ReceiptRecordView> records = Collections.emptyList();

    public ReceiptViewModel() {
    }

    public ReceiptViewModel(LocalDate localDate) {
        date = valueOf(localDate);
    }

    public ReceiptViewModel(ReceiptView receiptView) {
        if (receiptView != null) {
            type = valueOf(receiptView.getType());
            status = valueOf(receiptView.getStatus());
            paymentMethod = valueOf(receiptView.getPaymentMethod());
            openTime = valueOf(receiptView.getOpenTime());
            closureTime = valueOf(receiptView.getClosureTime());
            userCode = valueOf(receiptView.getUserCode());
            sumPurchaseNetPrice = valueOf(receiptView.getSumPurchaseNetPrice());
            sumPurchaseGrossPrice = valueOf(receiptView.getSumPurchaseGrossPrice());
            sumSaleNetPrice = valueOf(receiptView.getSumSaleNetPrice());
            sumSaleGrossPrice = valueOf(receiptView.getSumSaleGrossPrice());
            discountPercent = valueOf(receiptView.getDiscountPercent());
            VATSerie = valueOf(receiptView.getVATSerie());
            Client client = receiptView.getClient();
            if (client != null) {
                clientName = receiptView.getClient().getName();
                clientAddress = receiptView.getClient().getAddress();
                clientTAXNumber = receiptView.getClient().getTAXNumber();
            }
            records = receiptView.getSoldProducts();
        }
    }
}
