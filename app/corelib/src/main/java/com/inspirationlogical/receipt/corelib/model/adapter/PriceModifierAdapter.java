package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierRepeatPeriod;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierType;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.params.PriceModifierParams;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.inspirationlogical.receipt.corelib.model.entity.PriceModifier.GET_PRICE_MODIFIERS_BY_NAME;
import static java.util.stream.Collectors.toList;

/**
 * Created by Bálint on 2017.04.03..
 */
public class PriceModifierAdapter extends AbstractAdapter<PriceModifier> {

    public  static List<PriceModifierAdapter> getPriceModifiers() {
        List<PriceModifier> priceModifiers = GuardedTransaction.runNamedQuery(PriceModifier.GET_PRICE_MODIFIERS);
        return priceModifiers.stream().map(PriceModifierAdapter::new).collect(toList());
    }

    public static void addPriceModifier(PriceModifierParams params) {
        ProductCategory owner;
        if(params.isCategory()) {
            owner = ProductCategoryAdapter.getProductCategoryByName(params.getOwnerName()).getAdaptee();
        } else {
            owner = ProductAdapter.getProductByName(params.getOwnerName()).getAdaptee().getCategory();
        }
        PriceModifier priceModifier = params.getBuilder().build();
        priceModifier.setOwner(owner);
        owner.getPriceModifiers().add(priceModifier);
        GuardedTransaction.persist(priceModifier);
    }

    public static void updatePriceModifier(PriceModifierParams params) {
        GuardedTransaction.run(() -> {
            PriceModifier priceModifier = getPriceModifier(params.getOriginalName());
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
        });
    }

    private static PriceModifier getPriceModifier(String name) {
        return (PriceModifier) GuardedTransaction.runNamedQuery(GET_PRICE_MODIFIERS_BY_NAME,
                query -> query.setParameter("name", name)).get(0);
    }

    public static void deletePriceModifier(PriceModifierParams params) {
        PriceModifier priceModifier = getPriceModifier(params.getOriginalName());
        GuardedTransaction.delete(priceModifier,() -> priceModifier.getOwner().getPriceModifiers().remove(priceModifier));
    }

    public PriceModifierAdapter(PriceModifier adaptee) {
        super(adaptee);
    }

    double getDiscountPercent(ReceiptRecordAdapter receiptRecordAdapter) {
        if(adaptee.getType().equals(PriceModifierType.SIMPLE_DISCOUNT)) {
            return adaptee.getDiscountPercent();
        }
        return calculateDiscount((int)receiptRecordAdapter.getAdaptee().getSoldQuantity());
    }

    private double calculateDiscount(int soldQuantity) {
        return ((soldQuantity - (soldQuantity % adaptee.getQuantityLimit())) * adaptee.getDiscountPercent()) / soldQuantity;
    }

    public boolean isValidNow(LocalDateTime now) {
        if(getAdaptee().getRepeatPeriod().equals(PriceModifierRepeatPeriod.NO_REPETITION)) {
            return true;
        } else if(getAdaptee().getRepeatPeriod().equals(PriceModifierRepeatPeriod.DAILY)) {
            return isTimeMatches(now.toLocalTime());
        } else if(getAdaptee().getRepeatPeriod().equals(PriceModifierRepeatPeriod.WEEKLY)) {
            return isDayOfWeekMatches(now);
        }
        return false;
    }

    private boolean isTimeMatches(LocalTime now) {
        return now.plusMinutes(1).isAfter(getAdaptee().getStartTime()) && now.minusMinutes(1).isBefore(getAdaptee().getEndTime());
    }

    private boolean isDayOfWeekMatches(LocalDateTime now) {
        boolean isToday = now.getDayOfWeek().equals(getAdaptee().getDayOfWeek());
        boolean isYesterday = now.minusDays(1).getDayOfWeek().equals(getAdaptee().getDayOfWeek());
        boolean isNight = now.toLocalTime().isBefore(LocalTime.of(4,0));
        boolean isDay = now.toLocalTime().isAfter(LocalTime.of(4, 0));
        return (isToday && isDay) || (isYesterday && isNight);
    }

}
