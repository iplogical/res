package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierRepeatPeriod;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierType;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.service.PriceModifierParams;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;

/**
 * Created by Bálint on 2017.04.03..
 */
public class PriceModifierAdapter extends AbstractAdapter<PriceModifier> {

    public  static List<PriceModifierAdapter> getPriceModifiers() {
        List<PriceModifier> priceModifiers = GuardedTransaction.RunNamedQuery(PriceModifier.GET_PRICE_MODIFIERS);
        return priceModifiers.stream().map(PriceModifierAdapter::new).collect(toList());
    }

    public  static PriceModifierAdapter getPriceModifierByName(String name) {
        List<PriceModifier> priceModifiers = GuardedTransaction.RunNamedQuery(PriceModifier.GET_PRICE_MODIFIERS_BY_NAME,
                query -> {query.setParameter("name", name);
                            return query;});
        return priceModifiers.stream().map(PriceModifierAdapter::new).collect(toList()).get(0);
    }

    public static boolean isValidNow(PriceModifierAdapter priceModifierAdapter) {
        if(priceModifierAdapter.getAdaptee().getRepeatPeriod().equals(PriceModifierRepeatPeriod.NO_REPETITION)) {
            return true;
        } else if(priceModifierAdapter.getAdaptee().getRepeatPeriod().equals(PriceModifierRepeatPeriod.DAILY)) {
            if(isTimeMatches(priceModifierAdapter)) {
                return true;
            }
        } else if(priceModifierAdapter.getAdaptee().getRepeatPeriod().equals(PriceModifierRepeatPeriod.WEEKLY)) {
            if(isDayOfWeekMatches(priceModifierAdapter)) {
                return true;
            }
        }
        return false;
    }

    public static void addPriceModifier(PriceModifierParams params) {
        ProductCategory owner;
        if(params.isCategory()) {
            owner = ProductCategoryAdapter.getProductCategoryByName(params.getOwnerName()).get(0);
        } else {
            owner = ProductAdapter.getProductByName(params.getOwnerName()).get(0).getCategory();
        }
        PriceModifier priceModifier = params.getBuilder().build();
        priceModifier.setOwner(owner);
        owner.getPriceModifiers().add(priceModifier);
        GuardedTransaction.Persist(priceModifier);
    }

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

    private static boolean isTimeMatches(PriceModifierAdapter priceModifierAdapter) {
        LocalTime now = LocalTime.now();
        return now.isAfter(priceModifierAdapter.getAdaptee().getStartTime()) && now.isBefore(priceModifierAdapter.getAdaptee().getEndTime());
    }

    private static boolean isDayOfWeekMatches(PriceModifierAdapter priceModifierAdapter) {
        return LocalDate.now().getDayOfWeek().equals(priceModifierAdapter.getAdaptee().getDayOfWeek());
    }
}
