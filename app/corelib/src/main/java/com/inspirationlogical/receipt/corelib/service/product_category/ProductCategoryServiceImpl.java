package com.inspirationlogical.receipt.corelib.service.product_category;

import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.repository.PriceModifierRepository;
import com.inspirationlogical.receipt.corelib.service.price_modifier.PriceModifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    @Autowired
    private PriceModifierService priceModifierService;

    @Autowired
    private PriceModifierRepository priceModifierRepository;

    @Override
    public double getDiscount(ProductCategory category, ReceiptRecord receiptRecord) {
        List<PriceModifier> priceModifiers = new ArrayList<>();
        do {
            List<PriceModifier> loopModifiers = priceModifierRepository.getPriceModifierByProductAndDates(category.getId(), now());
            category = category.getParent();
            priceModifiers.addAll(loopModifiers);
        } while(!category.getType().equals(ProductCategoryType.ROOT));

        return priceModifiers.stream()
                .filter(priceModifier -> priceModifierService.isValidNow(priceModifier, LocalDateTime.now()))
                .map(pm -> priceModifierService.getDiscountPercent(pm, receiptRecord))
                .max(Double::compareTo)
                .orElse(0D);
    }
}
