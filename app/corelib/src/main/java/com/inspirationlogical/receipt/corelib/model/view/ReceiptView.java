package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.entity.Client;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@ToString
public class ReceiptView {

    private long id;
    private List<ReceiptRecordView> soldProducts;
    private long totalPrice;
    private ReceiptType type;
    private ReceiptStatus status;
    private PaymentMethod paymentMethod;
    private LocalDateTime openTime;
    private LocalDateTime closureTime;
    private int userCode;
    private int sumPurchaseNetPrice;
    private int sumPurchaseGrossPrice;
    private int sumSaleNetPrice;
    private int sumSaleGrossPrice;
    private double discountPercent;
    private Client client;

    public ReceiptView(Receipt receipt) {
        id = receipt.getId();
        soldProducts = initSoldProducts(receipt);
        totalPrice = initTotalPrice(receipt);
        type = receipt.getType();
        status = receipt.getStatus();
        paymentMethod = receipt.getPaymentMethod();
        openTime = receipt.getOpenTime();
        closureTime = receipt.getClosureTime();
        userCode = receipt.getUserCode();
        sumPurchaseNetPrice = receipt.getSumPurchaseNetPrice();
        sumPurchaseGrossPrice = receipt.getSumPurchaseGrossPrice();
        sumSaleNetPrice = receipt.getSumSaleNetPrice();
        sumSaleGrossPrice = receipt.getSumSaleGrossPrice();
        discountPercent = receipt.getDiscountPercent();
        client = receipt.getClient();
    }

    private List<ReceiptRecordView> initSoldProducts(Receipt receipt) {
        if(receipt == null) {
            return Collections.emptyList();
        }
        return receipt.getRecords().stream()
                .map(ReceiptRecordView::new)
                .collect(toList());
    }

    private int initTotalPrice(Receipt receipt) {
        return receipt.getRecords().stream()
                .mapToInt(record -> (int)(record.getSalePrice() * record.getSoldQuantity())).sum();
    }
}
