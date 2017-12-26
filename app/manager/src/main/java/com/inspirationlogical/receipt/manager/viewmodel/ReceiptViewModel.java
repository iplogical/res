package com.inspirationlogical.receipt.manager.viewmodel;

import static java.lang.String.valueOf;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import com.inspirationlogical.receipt.corelib.model.entity.Client;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;

import lombok.Data;

@Data
public class ReceiptViewModel {
    private String date = EMPTY;
    private String type = EMPTY;
    private String status = EMPTY;
    private String paymentMethod = EMPTY;
    private String openTime = EMPTY;
    private String closureTime = EMPTY;
    private String userCode = EMPTY;
    private String sumPurchaseNetPrice = EMPTY;
    private String sumPurchaseGrossPrice = EMPTY;
    private String sumSaleNetPrice = EMPTY;
    private String sumSaleGrossPrice = EMPTY;
    private String discountPercent = EMPTY;
    private String VATSerie = EMPTY;
    private String clientName = EMPTY;
    private String clientAddress = EMPTY;
    private String clientTAXNumber = EMPTY;
    private List<ReceiptRecordView> records = Collections.emptyList();

    public static ReceiptViewModel getSumReceiptViewModel(LocalDate localDate, List<ReceiptView> receiptsOfDate) {
        ReceiptViewModel sumViewModel = new ReceiptViewModel();
        sumViewModel.setDate(valueOf(localDate));
        sumViewModel.setSumSaleGrossPrice(sumViewModel.getSumValue(receiptsOfDate, ReceiptView::getSumSaleGrossPrice));
        sumViewModel.setSumSaleNetPrice(sumViewModel.getSumValue(receiptsOfDate, ReceiptView::getSumSaleNetPrice));
        sumViewModel.setSumPurchaseGrossPrice(sumViewModel.getSumValue(receiptsOfDate, ReceiptView::getSumPurchaseGrossPrice));
        sumViewModel.setSumPurchaseNetPrice(sumViewModel.getSumValue(receiptsOfDate, ReceiptView::getSumPurchaseNetPrice));
        return sumViewModel;
    }

    private String getSumValue(List<ReceiptView> receiptsOfDate, ToIntFunction<? super ReceiptView> getter) {
        return String.valueOf(receiptsOfDate.stream().mapToInt(getter).sum());
    }

    public ReceiptViewModel() {
    }

    public ReceiptViewModel(ReceiptView receiptView) {
        if (receiptView != null) {
            type = valueOf(receiptView.getType());
            status = valueOf(receiptView.getStatus());
            paymentMethod = valueOf(receiptView.getPaymentMethod());
            openTime = valueOf(receiptView.getOpenTime());
            closureTime = valueOf(receiptView.getClosureTime());
            userCode = valueOf(receiptView.getUserCode());
            sumPurchaseNetPrice = valueOf(receiptView.getSumPurchaseNetPrice());
            sumPurchaseGrossPrice = valueOf(receiptView.getSumPurchaseGrossPrice());
            sumSaleNetPrice = valueOf(receiptView.getSumSaleNetPrice());
            sumSaleGrossPrice = valueOf(receiptView.getSumSaleGrossPrice());
            discountPercent = valueOf(receiptView.getDiscountPercent());
            VATSerie = valueOf(receiptView.getVATSerie());
            Client client = receiptView.getClient();
            if (client != null) {
                clientName = receiptView.getClient().getName();
                clientAddress = receiptView.getClient().getAddress();
                clientTAXNumber = receiptView.getClient().getTAXNumber();
            }
            records = receiptView.getSoldProducts();
        }
    }

    public ReceiptViewModel(PaymentMethod paymentMethod, List<ReceiptView> receiptViews) {
        this.paymentMethod = paymentMethod.toString();
        records = receiptViews
                .stream()
                .flatMap(receiptView -> receiptView.getSoldProducts().stream())
                .collect(toList());
        sumPurchaseNetPrice = valueOf(receiptViews
                .stream()
                .mapToInt(ReceiptView::getSumPurchaseNetPrice)
                .sum());
        sumPurchaseGrossPrice = valueOf(receiptViews
                .stream()
                .mapToInt(ReceiptView::getSumPurchaseGrossPrice)
                .sum());
        sumSaleNetPrice = valueOf(receiptViews
                .stream()
                .mapToInt(ReceiptView::getSumSaleNetPrice)
                .sum());
        sumSaleGrossPrice = valueOf(receiptViews
                .stream()
                .mapToInt(ReceiptView::getSumSaleGrossPrice)
                .sum());
    }
}
