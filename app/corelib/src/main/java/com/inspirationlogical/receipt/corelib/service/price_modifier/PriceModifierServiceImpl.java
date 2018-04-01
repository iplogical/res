package com.inspirationlogical.receipt.corelib.service.price_modifier;

import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierRepeatPeriod;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierType;
import com.inspirationlogical.receipt.corelib.model.view.PriceModifierView;
import com.inspirationlogical.receipt.corelib.model.view.PriceModifierViewImpl;
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
        return priceModifierRepository.findAll().stream().map(PriceModifierViewImpl::new).collect(Collectors.toList());
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

    @Override
    public double getDiscountPercent(PriceModifier priceModifier, ReceiptRecord receiptRecord) {
        if(priceModifier.getType().equals(PriceModifierType.SIMPLE_DISCOUNT)) {
            return priceModifier.getDiscountPercent();
        }
        return calculateDiscount(priceModifier, (int)receiptRecord.getSoldQuantity());
    }

    private double calculateDiscount(PriceModifier priceModifier, int soldQuantity) {
        return ((soldQuantity - (soldQuantity % priceModifier.getQuantityLimit())) * priceModifier.getDiscountPercent()) / soldQuantity;
    }

    @Override
    public boolean isValidNow(PriceModifier priceModifier, LocalDateTime now) {
        if(priceModifier.getRepeatPeriod().equals(PriceModifierRepeatPeriod.NO_REPETITION)) {
            return true;
        } else if(priceModifier.getRepeatPeriod().equals(PriceModifierRepeatPeriod.DAILY)) {
            return isTimeMatches(priceModifier, now.toLocalTime());
        } else if(priceModifier.getRepeatPeriod().equals(PriceModifierRepeatPeriod.WEEKLY)) {
            return isDayOfWeekMatches(priceModifier, now);
        }
        return false;
    }

    private boolean isTimeMatches(PriceModifier priceModifier, LocalTime now) {
        return now.plusMinutes(1).isAfter(priceModifier.getStartTime()) && now.minusMinutes(1).isBefore(priceModifier.getEndTime());
    }

    private boolean isDayOfWeekMatches(PriceModifier priceModifier, LocalDateTime now) {
        boolean isToday = now.getDayOfWeek().equals(priceModifier.getDayOfWeek());
        boolean isYesterday = now.minusDays(1).getDayOfWeek().equals(priceModifier.getDayOfWeek());
        boolean isNight = now.toLocalTime().isBefore(LocalTime.of(4,0));
        boolean isDay = now.toLocalTime().isAfter(LocalTime.of(4, 0));
        return (isToday && isDay) || (isYesterday && isNight);
    }

}
