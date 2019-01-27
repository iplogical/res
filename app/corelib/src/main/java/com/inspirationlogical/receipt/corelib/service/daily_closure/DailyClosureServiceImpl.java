package com.inspirationlogical.receipt.corelib.service.daily_closure;

import com.inspirationlogical.receipt.corelib.model.entity.DailyClosure;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.repository.DailyClosureRepository;
import com.inspirationlogical.receipt.corelib.service.stock.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.inspirationlogical.receipt.corelib.utility.Round.roundToTwoDecimals;
import static java.time.LocalDateTime.now;

@Service
@Transactional
public class DailyClosureServiceImpl implements DailyClosureService{

    @Autowired
    private StockService stockService;

    @Autowired
    private DailyClosureRepository dailyClosureRepository;

    @Override
    public List<LocalDateTime> getClosureTimes(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startClosureTime = getStartClosureTime(startDate);
        LocalDateTime endClosureTime = getEndClosureTime(endDate);
        return Arrays.asList(startClosureTime, endClosureTime);
    }

    private LocalDateTime getStartClosureTime(LocalDate startDate) {
        LocalDateTime startTime = startDate.atTime(5, 0);
        Optional<DailyClosure> startDailyClosure = dailyClosureRepository.findTopByClosureTimeBeforeOrderByClosureTimeDesc(startTime);
        return startDailyClosure.map(DailyClosure::getClosureTime).orElse(startTime);
    }

    private LocalDateTime getEndClosureTime(LocalDate endDate) {
        LocalDateTime endTime = endDate.atTime(21, 0);
        Optional<DailyClosure> endDailyClosure = dailyClosureRepository.findTopByClosureTimeAfterOrderByClosureTimeAsc(endTime);
        return endDailyClosure.map(DailyClosure::getClosureTime).orElse(endTime.plusDays(1));
    }

    @Override
    public void closeDay() {
        stockService.closeLatestStockEntries();
        closeDailyClosure();
    }

    private void closeDailyClosure() {
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
