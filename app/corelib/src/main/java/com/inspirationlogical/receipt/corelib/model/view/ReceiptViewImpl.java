package com.inspirationlogical.receipt.corelib.model.view;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.Client;
import com.inspirationlogical.receipt.corelib.model.entity.VATSerie;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;

public class ReceiptViewImpl extends AbstractModelViewImpl<ReceiptAdapter>
    implements ReceiptView {

    public ReceiptViewImpl(ReceiptAdapter adapter) {
        super(adapter);
    }

    @Override
    public List<ReceiptRecordView> getSoldProducts() {
        if(adapter == null) {
            return Collections.emptyList();
        }
        return adapter.getSoldProducts().stream()
                .map(ReceiptRecordViewImpl::new)
                .collect(Collectors.toList());
    }

    @Override
    public long getTotalPrice() {
        if(adapter == null ) {
            return 0;
        }
        return adapter.getTotalPrice();
    }

    @Override
    public ReceiptType getType() {
        if(adapter == null ) {
            return null;
        }
        return adapter.getAdaptee().getType();
    }

    @Override
    public ReceiptStatus getStatus() {
        if(adapter == null ) {
            return null;
        }
        return adapter.getAdaptee().getStatus();
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        if(adapter == null ) {
            return null;
        }
        return adapter.getAdaptee().getPaymentMethod();
    }

    @Override
    public LocalDateTime getOpenTime() {
        if(adapter == null ) {
            return null;
        }
        return adapter.getAdaptee().getOpenTime();
    }

    @Override
    public LocalDateTime getClosureTime() {
        if(adapter == null ) {
            return null;
        }
        return adapter.getAdaptee().getClosureTime();
    }

    @Override
    public int getUserCode() {
        if(adapter == null ) {
            return 0;
        }
        return adapter.getAdaptee().getUserCode();
    }

    @Override
    public int getSumPurchaseNetPrice() {
        if(adapter == null ) {
            return 0;
        }
        return adapter.getAdaptee().getSumPurchaseNetPrice();
    }

    @Override
    public int getSumPurchaseGrossPrice() {
        if(adapter == null ) {
            return 0;
        }
        return adapter.getAdaptee().getSumPurchaseGrossPrice();
    }

    @Override
    public int getSumSaleNetPrice() {
        if(adapter == null ) {
            return 0;
        }
        return adapter.getAdaptee().getSumSaleNetPrice();
    }

    @Override
    public int getSumSaleGrossPrice() {
        if(adapter == null ) {
            return 0;
        }
        return adapter.getAdaptee().getSumSaleGrossPrice();
    }

    @Override
    public double getDiscountPercent() {
        if(adapter == null ) {
            return 0.0;
        }
        return adapter.getAdaptee().getDiscountPercent();
    }

    @Override
    public VATSerie getVATSerie() {
        if(adapter == null ) {
            return null;
        }
        return adapter.getAdaptee().getVATSerie();
    }

    @Override
    public Client getClient() {
        if(adapter == null ) {
            return null;
        }
        return adapter.getAdaptee().getClient();
    }
}
