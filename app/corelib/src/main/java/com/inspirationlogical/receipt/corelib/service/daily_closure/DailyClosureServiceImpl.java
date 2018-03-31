package com.inspirationlogical.receipt.corelib.service.daily_closure;

import com.inspirationlogical.receipt.corelib.model.entity.DailyClosure;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.repository.DailyClosureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.inspirationlogical.receipt.corelib.utility.Round.roundToTwoDecimals;
import static java.time.LocalDateTime.now;

/**
 * Created by TheDagi on 2017. 04. 17..
 */
@Service
public class DailyClosureServiceImpl implements DailyClosureService{

    @Autowired
    private DailyClosureRepository dailyClosureRepository;

    @Override
    public List<LocalDateTime> getClosureTimes(LocalDate startDate, LocalDate endDate) {
        List<DailyClosure> closureTimes = new ArrayList<>();
        closureTimes.add(dailyClosureRepository.getDailyClosureBeforeDate(startDate.atTime(5, 0)));
        closureTimes.add(dailyClosureRepository.getDailyClosureAfterDate(endDate.atTime(21, 0)));
        return closureTimes.stream().map(DailyClosure::getClosureTime).collect(Collectors.toList());
    }

    @Override
    public void close() {
        DailyClosure openDailyClosure = getOpenDailyClosure();
        if(openDailyClosure.getNumberOfReceipts() == 0) {
            return; //  Do not close a day with 0 receipt.
        }
        closeOpenDailyClosure(openDailyClosure);
        createNewDailyClosure(openDailyClosure);
    }

    private DailyClosure getOpenDailyClosure() {
        DailyClosure dailyClosure = dailyClosureRepository.findByClosureTimeIsNull();
        if(dailyClosure == null) {
            throw new RuntimeException("The open daily closure has to exist.");
        }
        return dailyClosure;
    }

    private void closeOpenDailyClosure(DailyClosure openDailyClosure) {
        openDailyClosure.setClosureTime(now());
        openDailyClosure.setProfit(openDailyClosure.getSumSaleGrossPriceTotal() - openDailyClosure.getSumPurchaseNetPriceTotal());
        openDailyClosure.setMarkup(roundToTwoDecimals(((double) openDailyClosure.getProfit() / (double)openDailyClosure.getSumSaleGrossPriceTotal()) * 100D));
        openDailyClosure.setReceiptAverage(openDailyClosure.getSumSaleGrossPriceTotal() / openDailyClosure.getNumberOfReceipts());
        dailyClosureRepository.save(openDailyClosure);
    }

    private void createNewDailyClosure(DailyClosure openDailyClosure) {
        DailyClosure newDailyClosure = DailyClosure.builder().build();
        newDailyClosure.setOwner(openDailyClosure.getOwner());
        dailyClosureRepository.save(newDailyClosure);
    }
    
    @Override
    public void update(Receipt receipt) {
        DailyClosure openDailyClosure = getOpenDailyClosure();
        // TODO: set discount
        updateSumPrices(receipt, openDailyClosure);
        openDailyClosure.setNumberOfReceipts(openDailyClosure.getNumberOfReceipts() + 1);
        dailyClosureRepository.save(openDailyClosure);
    }

    private void updateSumPrices(Receipt receipt, DailyClosure openDailyClosure) {
        PaymentMethod paymentMethod = receipt.getPaymentMethod();
        if(paymentMethod.equals(PaymentMethod.CASH)) {
            updateCash(receipt, openDailyClosure);
        } else if(paymentMethod.equals(PaymentMethod.CREDIT_CARD)) {
            updateCreditCard(receipt, openDailyClosure);
        } else if(paymentMethod.equals(PaymentMethod.COUPON)) {
            updateCoupon(receipt, openDailyClosure);
        }
        updateTotal(receipt, openDailyClosure);
    }

    private void updateCash(Receipt receipt, DailyClosure openDailyClosure) {
        openDailyClosure.setSumPurchaseNetPriceCash(openDailyClosure.getSumPurchaseNetPriceCash() + receipt.getSumPurchaseNetPrice());
        openDailyClosure.setSumPurchaseGrossPriceCash(openDailyClosure.getSumPurchaseGrossPriceCash() + receipt.getSumPurchaseGrossPrice());
        openDailyClosure.setSumSaleNetPriceCash(openDailyClosure.getSumSaleNetPriceCash() + receipt.getSumSaleNetPrice());
        openDailyClosure.setSumSaleGrossPriceCash(openDailyClosure.getSumSaleGrossPriceCash()+ receipt.getSumSaleGrossPrice());
    }

    private void updateTotal(Receipt receipt, DailyClosure openDailyClosure) {
        openDailyClosure.setSumPurchaseNetPriceTotal(openDailyClosure.getSumPurchaseNetPriceTotal() + receipt.getSumPurchaseNetPrice());
        openDailyClosure.setSumPurchaseGrossPriceTotal(openDailyClosure.getSumPurchaseGrossPriceTotal() + receipt.getSumPurchaseGrossPrice());
        openDailyClosure.setSumSaleNetPriceTotal(openDailyClosure.getSumSaleNetPriceTotal() + receipt.getSumSaleNetPrice());
        openDailyClosure.setSumSaleGrossPriceTotal(openDailyClosure.getSumSaleGrossPriceTotal()+ receipt.getSumSaleGrossPrice());
    }

    private void updateCoupon(Receipt receipt, DailyClosure openDailyClosure) {
        openDailyClosure.setSumPurchaseNetPriceCoupon(openDailyClosure.getSumPurchaseNetPriceCoupon() + receipt.getSumPurchaseNetPrice());
        openDailyClosure.setSumPurchaseGrossPriceCoupon(openDailyClosure.getSumPurchaseGrossPriceCoupon() + receipt.getSumPurchaseGrossPrice());
        openDailyClosure.setSumSaleNetPriceCoupon(openDailyClosure.getSumSaleNetPriceCoupon() + receipt.getSumSaleNetPrice());
        openDailyClosure.setSumSaleGrossPriceCoupon(openDailyClosure.getSumSaleGrossPriceCoupon()+ receipt.getSumSaleGrossPrice());
    }

    private void updateCreditCard(Receipt receipt, DailyClosure openDailyClosure) {
        openDailyClosure.setSumPurchaseNetPriceCreditCard(openDailyClosure.getSumPurchaseNetPriceCreditCard() + receipt.getSumPurchaseNetPrice());
        openDailyClosure.setSumPurchaseGrossPriceCreditCard(openDailyClosure.getSumPurchaseGrossPriceCreditCard() + receipt.getSumPurchaseGrossPrice());
        openDailyClosure.setSumSaleNetPriceCreditCard(openDailyClosure.getSumSaleNetPriceCreditCard() + receipt.getSumSaleNetPrice());
        openDailyClosure.setSumSaleGrossPriceCreditCard(openDailyClosure.getSumSaleGrossPriceCreditCard()+ receipt.getSumSaleGrossPrice());
    }

}
