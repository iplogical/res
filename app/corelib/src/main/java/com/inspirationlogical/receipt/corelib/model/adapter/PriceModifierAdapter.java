package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierType;

/**
 * Created by Bálint on 2017.04.03..
 */
public class PriceModifierAdapter extends AbstractAdapter<PriceModifier> {

    public PriceModifierAdapter(PriceModifier adaptee) {
        super(adaptee);
    }

    double getDiscountPercent(ReceiptRecordAdapter receiptRecordAdapter) {
        if(adaptee.getType().equals(PriceModifierType.SIMPLE_DISCOUNT)) {
            return adaptee.getDiscountPercent();
        } else if(adaptee.getType().equals(PriceModifierType.QUANTITY_DISCOUNT)) {
            return calculateDiscount((int)receiptRecordAdapter.getAdaptee().getSoldQuantity());
        }
        return 0;
    }

    private double calculateDiscount(int soldQuantity) {
        return ((soldQuantity - (soldQuantity % adaptee.getQuantityLimit())) * adaptee.getDiscountPercent()) / soldQuantity;
    }
}
