package com.inspirationlogical.receipt.manager.controller.receipt;

import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

import static com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus.CLOSED;
import static java.util.stream.Collectors.toList;

public class SummaryCalculator {

    private ReceiptController receiptController;

    private Map<LocalDate, List<ReceiptView>> receiptsByDate;

    private List<ReceiptView> receiptViews;

    private List<ReceiptView> closedReceiptViews;

    private SummaryValues summaryValues;

    public static SummaryCalculator getInstance(ReceiptController receiptController, Map<LocalDate, List<ReceiptView>> receiptsByDate) {
        SummaryCalculator calculator = new SummaryCalculator(receiptController, receiptsByDate);
        calculator.initialize();
        return calculator;
    }
    private SummaryCalculator(ReceiptController receiptController, Map<LocalDate, List<ReceiptView>> receiptsByDate) {
        this.receiptController = receiptController;
        this.receiptsByDate = receiptsByDate;
    }

    private void initialize() {
        receiptViews = receiptsByDate.values().stream().flatMap(Collection::stream).collect(toList());
        closedReceiptViews = receiptViews.stream().filter(receiptView -> receiptView.getStatus().equals(CLOSED)).collect(toList());
        summaryValues = new SummaryValues();
    }

    public void calculateSummaries() {
        summaryValues.setNumberOfReceipts(closedReceiptViews.size());
        summaryValues.setCashNetIncome(getSumValueByPaymentMethod(PaymentMethod.CASH, ReceiptView::getSumSaleNetPrice));
        summaryValues.setCashGrossIncome(getSumValueByPaymentMethod(PaymentMethod.CASH, ReceiptView::getSumSaleGrossPrice));
        summaryValues.setCreditCardNetIncome(getSumValueByPaymentMethod(PaymentMethod.CREDIT_CARD, ReceiptView::getSumSaleNetPrice));
        summaryValues.setCreditCardGrossIncome(getSumValueByPaymentMethod(PaymentMethod.CREDIT_CARD, ReceiptView::getSumSaleGrossPrice));
        summaryValues.setCouponNetIncome(getSumValueByPaymentMethod(PaymentMethod.COUPON, ReceiptView::getSumSaleNetPrice));
        summaryValues.setCouponGrossIncome(getSumValueByPaymentMethod(PaymentMethod.COUPON, ReceiptView::getSumSaleGrossPrice));
        summaryValues.setTotalNetIncome(getTotalSumValue(ReceiptView::getSumSaleNetPrice));
        summaryValues.setTotalGrossIncome(getTotalSumValue(ReceiptView::getSumSaleGrossPrice));
        summaryValues.setTotalGrossExpenditure(getTotalSumValue(ReceiptView::getSumPurchaseGrossPrice));
        receiptController.setSummaryValues(summaryValues);
    }

    private int getSumValueByPaymentMethod(PaymentMethod paymentMethod, ToIntFunction<? super ReceiptView> extractor) {
        return closedReceiptViews.stream()
                .filter(receiptView -> receiptView.getPaymentMethod().equals(paymentMethod))
                .mapToInt(extractor).sum();
    }

    private int getTotalSumValue(ToIntFunction<? super ReceiptView> extractor) {
        return closedReceiptViews.stream().mapToInt(extractor).sum();
    }
}
