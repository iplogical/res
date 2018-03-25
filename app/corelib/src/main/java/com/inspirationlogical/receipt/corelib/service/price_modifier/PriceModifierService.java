package com.inspirationlogical.receipt.corelib.service.price_modifier;

import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;

import java.time.LocalDateTime;

public interface PriceModifierService {
    double getDiscountPercent(PriceModifier priceModifier, ReceiptRecord receiptRecord);

    boolean isValidNow(PriceModifier priceModifier, LocalDateTime now);
}
