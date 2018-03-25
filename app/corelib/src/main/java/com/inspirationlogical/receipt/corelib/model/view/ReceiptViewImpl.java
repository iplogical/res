package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.entity.Client;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.VATSerie;
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
public class ReceiptViewImpl implements ReceiptView {

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
    private VATSerie VATSerie;
    private Client client;

    public ReceiptViewImpl(Receipt receipt) {
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
        VATSerie = receipt.getVATSerie();
        client = receipt.getClient();
    }

    private List<ReceiptRecordView> initSoldProducts(Receipt receipt) {
        if(receipt == null) {
            return Collections.emptyList();
        }
        return receipt.getRecords().stream()
                .map(ReceiptRecordViewImpl::new)
                .collect(toList());
    }

    private int initTotalPrice(Receipt receipt) {
        return receipt.getRecords().stream()
                .mapToInt(record -> (int)(record.getSalePrice() * record.getSoldQuantity())).sum();
    }

//    @Override
//    public long getId() {
//        return adapter.getAdaptee().getId();
//    }

//    @Override
//    public List<ReceiptRecordView> getSoldProducts() {
//        if(adapter == null) {
//            return Collections.emptyList();
//        }
//        return adapter.getSoldProducts().stream()
//                .map(ReceiptRecordViewImpl::new)
//                .collect(toList());
//    }
//
//    @Override
//    public long getTotalPrice() {
//        if(adapter == null ) {
//            return 0;
//        }
//        return adapter.getTotalPrice();
//    }
//
//    @Override
//    public ReceiptType getType() {
//        if(adapter == null ) {
//            return null;
//        }
//        return adapter.getAdaptee().getType();
//    }
//
//    @Override
//    public ReceiptStatus getStatus() {
//        if(adapter == null ) {
//            return null;
//        }
//        return adapter.getAdaptee().getStatus();
//    }
//
//    @Override
//    public PaymentMethod getPaymentMethod() {
//        if(adapter == null ) {
//            return null;
//        }
//        return adapter.getAdaptee().getPaymentMethod();
//    }
//
//    @Override
//    public LocalDateTime getOpenTime() {
//        if(adapter == null ) {
//            return null;
//        }
//        return adapter.getAdaptee().getOpenTime();
//    }
//
//    @Override
//    public LocalDateTime getClosureTime() {
//        if(adapter == null ) {
//            return null;
//        }
//        return adapter.getAdaptee().getClosureTime();
//    }
//
//    @Override
//    public int getUserCode() {
//        if(adapter == null ) {
//            return 0;
//        }
//        return adapter.getAdaptee().getUserCode();
//    }
//
//    @Override
//    public int getSumPurchaseNetPrice() {
//        if(adapter == null ) {
//            return 0;
//        }
//        return adapter.getAdaptee().getSumPurchaseNetPrice();
//    }
//
//    @Override
//    public int getSumPurchaseGrossPrice() {
//        if(adapter == null ) {
//            return 0;
//        }
//        return adapter.getAdaptee().getSumPurchaseGrossPrice();
//    }
//
//    @Override
//    public int getSumSaleNetPrice() {
//        if(adapter == null ) {
//            return 0;
//        }
//        return adapter.getAdaptee().getSumSaleNetPrice();
//    }
//
//    @Override
//    public int getSumSaleGrossPrice() {
//        if(adapter == null ) {
//            return 0;
//        }
//        return adapter.getAdaptee().getSumSaleGrossPrice();
//    }
//
//    @Override
//    public double getDiscountPercent() {
//        if(adapter == null ) {
//            return 0.0;
//        }
//        return adapter.getAdaptee().getDiscountPercent();
//    }
//
//    @Override
//    public VATSerie getVATSerie() {
//        if(adapter == null ) {
//            return null;
//        }
//        return adapter.getAdaptee().getVATSerie();
//    }
//
//    @Override
//    public Client getClient() {
//        if(adapter == null ) {
//            return null;
//        }
//        return adapter.getAdaptee().getClient();
//    }

//    @Override
//    public String toString() {
//        return adapter.toString();
//    }
}
