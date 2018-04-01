package com.inspirationlogical.receipt.corelib.service.price_modifier;

import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.view.PriceModifierView;
import com.inspirationlogical.receipt.corelib.params.PriceModifierParams;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceModifierService {

    List<PriceModifierView> getPriceModifiers();

    void addPriceModifier(PriceModifierParams params);

    void updatePriceModifier(PriceModifierParams params);

    void deletePriceModifier(PriceModifierParams params);

    double getDiscountPercent(PriceModifier priceModifier, ReceiptRecord receiptRecord);

    boolean isValidNow(PriceModifier priceModifier, LocalDateTime now);
}
