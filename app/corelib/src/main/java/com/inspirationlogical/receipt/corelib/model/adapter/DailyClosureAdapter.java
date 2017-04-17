package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.entity.DailyClosure;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.List;

import static com.inspirationlogical.receipt.corelib.utility.Round.roundToTwoDecimals;
import static java.time.LocalDateTime.now;

/**
 * Created by TheDagi on 2017. 04. 17..
 */
public class DailyClosureAdapter extends AbstractAdapter<DailyClosure> {

    public DailyClosureAdapter(DailyClosure adaptee) {
        super(adaptee);
    }

    public static LocalDateTime getLatestClosureTime() {
        List<DailyClosure> dailyClosureList = GuardedTransaction.runNamedQuery(DailyClosure.GET_LATEST_DAILY_CLOSURE);
        if(dailyClosureList.isEmpty()) {
            return now().minusDays(1);
        }
        return dailyClosureList.get(0).getClosureTime();
    }

    public static DailyClosureAdapter getOpenDailyClosure() {
        List<DailyClosure> dailyClosureList = GuardedTransaction.runNamedQuery(DailyClosure.GET_OPEN_DAILY_CLOSURE);
        if(dailyClosureList.isEmpty()) {
            throw new RuntimeException("The open daily closure has to exist.");
        }
        return new DailyClosureAdapter(dailyClosureList.get(0));
    }

    public void close() {
        GuardedTransaction.run(() -> {
            adaptee.setClosureTime(now());
            adaptee.setProfit(adaptee.getSumSaleGrossPriceTotal() - adaptee.getSumPurchaseNetPriceTotal());
            adaptee.setMarkup(roundToTwoDecimals(((double) adaptee.getProfit() / (double)adaptee.getSumSaleGrossPriceTotal()) * 100D));
            adaptee.setReceiptAverage(adaptee.getSumSaleGrossPriceTotal() / adaptee.getNumberOfReceipts());
        });
        DailyClosure newDailyClosure = DailyClosure.builder().build();
        newDailyClosure.setOwner(adaptee.getOwner());
        GuardedTransaction.persist(newDailyClosure);
    }

    public void update(ReceiptAdapter receipt) {
        GuardedTransaction.run(() -> updateDailyClosure(receipt));
    }

    private void updateDailyClosure(ReceiptAdapter receiptAdapter) {
        // TODO: set discount
        Receipt receipt = receiptAdapter.getAdaptee();
        updateSumPrices(receipt, receipt.getPaymentMethod());
        adaptee.setNumberOfReceipts(adaptee.getNumberOfReceipts() + 1);
    }

    private void updateSumPrices(Receipt receipt, PaymentMethod paymentMethod) {
        if(paymentMethod.equals(PaymentMethod.CASH)) {
            updateCash(receipt);
        } else if(paymentMethod.equals(PaymentMethod.CREDIT_CARD)) {
            updateCreditCard(receipt);
        } else if(paymentMethod.equals(PaymentMethod.COUPON)) {
            updateCoupon(receipt);
        }
        updateTotal(receipt);
    }

    private void updateCash(Receipt receipt) {
        adaptee.setSumPurchaseNetPriceCash(adaptee.getSumPurchaseNetPriceCash() + receipt.getSumPurchaseNetPrice());
        adaptee.setSumPurchaseGrossPriceCash(adaptee.getSumPurchaseGrossPriceCash() + receipt.getSumPurchaseGrossPrice());
        adaptee.setSumSaleNetPriceCash(adaptee.getSumSaleNetPriceCash() + receipt.getSumSaleNetPrice());
        adaptee.setSumSaleGrossPriceCash(adaptee.getSumSaleGrossPriceCash()+ receipt.getSumSaleGrossPrice());
    }

    private void updateTotal(Receipt receipt) {
        adaptee.setSumPurchaseNetPriceTotal(adaptee.getSumPurchaseNetPriceTotal() + receipt.getSumPurchaseNetPrice());
        adaptee.setSumPurchaseGrossPriceTotal(adaptee.getSumPurchaseGrossPriceTotal() + receipt.getSumPurchaseGrossPrice());
        adaptee.setSumSaleNetPriceTotal(adaptee.getSumSaleNetPriceTotal() + receipt.getSumSaleNetPrice());
        adaptee.setSumSaleGrossPriceTotal(adaptee.getSumSaleGrossPriceTotal()+ receipt.getSumSaleGrossPrice());
    }

    private void updateCoupon(Receipt receipt) {
        adaptee.setSumPurchaseNetPriceCoupon(adaptee.getSumPurchaseNetPriceCoupon() + receipt.getSumPurchaseNetPrice());
        adaptee.setSumPurchaseGrossPriceCoupon(adaptee.getSumPurchaseGrossPriceCoupon() + receipt.getSumPurchaseGrossPrice());
        adaptee.setSumSaleNetPriceCoupon(adaptee.getSumSaleNetPriceCoupon() + receipt.getSumSaleNetPrice());
        adaptee.setSumSaleGrossPriceCoupon(adaptee.getSumSaleGrossPriceCoupon()+ receipt.getSumSaleGrossPrice());
    }

    private void updateCreditCard(Receipt receipt) {
        adaptee.setSumPurchaseNetPriceCreditCard(adaptee.getSumPurchaseNetPriceCreditCard() + receipt.getSumPurchaseNetPrice());
        adaptee.setSumPurchaseGrossPriceCreditCard(adaptee.getSumPurchaseGrossPriceCreditCard() + receipt.getSumPurchaseGrossPrice());
        adaptee.setSumSaleNetPriceCreditCard(adaptee.getSumSaleNetPriceCreditCard() + receipt.getSumSaleNetPrice());
        adaptee.setSumSaleGrossPriceCreditCard(adaptee.getSumSaleGrossPriceCreditCard()+ receipt.getSumSaleGrossPrice());
    }

}
