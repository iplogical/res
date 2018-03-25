package com.inspirationlogical.receipt.corelib.service.price_modifier;

import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierRepeatPeriod;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class PriceModifierServiceImpl implements PriceModifierService {

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
