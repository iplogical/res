package com.inspirationlogical.receipt.manager.viewmodel;

import com.inspirationlogical.receipt.corelib.model.entity.Client;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import lombok.Data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.function.ToIntFunction;

import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.EMPTY;

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
        DateTimeFormatter openTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter closureTimeFormatter = DateTimeFormatter.ofPattern("MM.dd HH:mm");
        if (receiptView != null) {
            type = valueOf(receiptView.getType());
            status = valueOf(receiptView.getStatus());
            paymentMethod = valueOf(receiptView.getPaymentMethod());
            openTime = receiptView.getOpenTime().format(openTimeFormatter);
            closureTime = receiptView.getClosureTime() == null ? "" : receiptView.getClosureTime().format(closureTimeFormatter);
            userCode = valueOf(receiptView.getUserCode());
            sumPurchaseNetPrice = valueOf(receiptView.getSumPurchaseNetPrice());
            sumPurchaseGrossPrice = valueOf(receiptView.getSumPurchaseGrossPrice());
            sumSaleNetPrice = valueOf(receiptView.getSumSaleNetPrice());
            sumSaleGrossPrice = valueOf(receiptView.getSumSaleGrossPrice());
            discountPercent = valueOf(receiptView.getDiscountPercent());
            Client client = receiptView.getClient();
            if (client != null) {
                clientName = receiptView.getClient().getName();
                clientAddress = receiptView.getClient().getAddress();
                clientTAXNumber = receiptView.getClient().getTAXNumber();
            }
            records = receiptView.getSoldProducts();
        }
    }
}
