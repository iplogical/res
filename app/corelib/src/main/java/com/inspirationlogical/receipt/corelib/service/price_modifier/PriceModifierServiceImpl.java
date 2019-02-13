package com.inspirationlogical.receipt.corelib.service.price_modifier;

import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierRepeatPeriod;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierType;
import com.inspirationlogical.receipt.corelib.model.view.PriceModifierView;
import com.inspirationlogical.receipt.corelib.params.PriceModifierParams;
import com.inspirationlogical.receipt.corelib.repository.PriceModifierRepository;
import com.inspirationlogical.receipt.corelib.repository.ProductCategoryRepository;
import com.inspirationlogical.receipt.corelib.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceModifierServiceImpl implements PriceModifierService {

    @Autowired
    private PriceModifierRepository priceModifierRepository;

    @Autowired
    private ProductRepository productRepository;;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Override
    public List<PriceModifierView> getPriceModifiers() {
        return priceModifierRepository.findAll().stream().map(PriceModifierView::new).collect(Collectors.toList());
    }

    @Override
    public void addPriceModifier(PriceModifierParams params) {
        ProductCategory owner;
        if(params.isCategory()) {
            owner = productCategoryRepository.findByName(params.getOwnerName());
        } else {
            owner = productRepository.findByLongName(params.getOwnerName()).getCategory();
        }
        PriceModifier priceModifier = params.getBuilder().build();
        priceModifier.setOwner(owner);
        owner.getPriceModifiers().add(priceModifier);
        priceModifierRepository.save(priceModifier);
    }

    @Override
    public void updatePriceModifier(PriceModifierParams params) {
        PriceModifier priceModifier = priceModifierRepository.findByName(params.getOriginalName());
        PriceModifier newPriceModifier = params.getBuilder().build();
        priceModifier.setName(newPriceModifier.getName());
        priceModifier.setType(newPriceModifier.getType());
        priceModifier.setQuantityLimit(newPriceModifier.getQuantityLimit());
        priceModifier.setDiscountPercent(newPriceModifier.getDiscountPercent());
        priceModifier.setStartDate(newPriceModifier.getStartDate());
        priceModifier.setEndDate(newPriceModifier.getEndDate());
        priceModifier.setRepeatPeriod(newPriceModifier.getRepeatPeriod());
        priceModifier.setDayOfWeek(newPriceModifier.getDayOfWeek());
        priceModifier.setStartTime(newPriceModifier.getStartTime());
        priceModifier.setEndTime(newPriceModifier.getEndTime());
        priceModifierRepository.save(priceModifier);
    }

    @Override
    public void deletePriceModifier(PriceModifierParams params) {
        PriceModifier priceModifier = priceModifierRepository.findByName(params.getOriginalName());
        priceModifier.getOwner().getPriceModifiers().remove(priceModifier);
        priceModifierRepository.delete(priceModifier);
    }
}
