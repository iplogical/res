package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierRepeatPeriod;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierType;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.params.PriceModifierParams;

import java.time.LocalDate;
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

    private static boolean isTimeMatches(PriceModifierAdapter priceModifierAdapter) {
        LocalTime now = LocalTime.now();
        return now.isAfter(priceModifierAdapter.getAdaptee().getStartTime()) && now.isBefore(priceModifierAdapter.getAdaptee().getEndTime());
    }

    private static boolean isDayOfWeekMatches(PriceModifierAdapter priceModifierAdapter) {
        return LocalDate.now().getDayOfWeek().equals(priceModifierAdapter.getAdaptee().getDayOfWeek());
    }
}
