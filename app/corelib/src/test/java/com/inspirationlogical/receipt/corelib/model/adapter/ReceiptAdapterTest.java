package com.inspirationlogical.receipt.corelib.model.adapter;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordViewImpl;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.params.StockParams;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.exception.IllegalReceiptStateException;
import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;

/**
 * Created by Bálint on 2017.03.13..
 */
public class ReceiptAdapterTest {

    private TableAdapter tableAdapter;
    private ProductAdapter productOne;
    private ReceiptAdapter receiptSaleOne;
    private ReceiptAdapter receiptPurchase;
    private ReceiptRecordAdapter receiptRecordSaleOne;
    private PaymentParams paymentParams;

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void createAdapters() {
        tableAdapter = new TableAdapter(schema.getTableNormal());
        productOne = new ProductAdapter(schema.getProductOne());
        receiptSaleOne = new ReceiptAdapter(schema.getReceiptSaleOne());
        receiptPurchase = new ReceiptAdapter(schema.getReceiptPurchase());
        receiptRecordSaleOne = new ReceiptRecordAdapter(schema.getReceiptRecordSaleOne());
        paymentParams = PaymentParams.builder()
                .paymentMethod(PaymentMethod.CASH)
                .discountAbsolute(0)
                .discountPercent(0D)
                .build();
    }

    @Test
    public void testGetSoldProducts() {
        assertEquals(4, receiptSaleOne.getSoldProducts().size());
    }

    @Test
    public void testSellProduct() {
        receiptSaleOne.sellProduct(productOne, 1, true, false);
        assertEquals(5, receiptSaleOne.getSoldProducts().size());
    }

    @Test
    public void testSellHocProduct() {
        AdHocProductParams productParams = AdHocProductParams.builder()
                .name("Hot Milk")
                .quantity(1)
                .purchasePrice(200)
                .salePrice(400)
                .build();
        receiptSaleOne.sellAdHocProduct(productParams, true);
        assertEquals(5, receiptSaleOne.getSoldProducts().size());
    }

    /*
    *   Soproni 0,5L -> Purchase price = 250
    *           1 x 440 = 440
    *   Jim Beam ->     Purchase price = 300
    *           2 x 560 = 1120
    *   Edelweiss 0,5L -> Purchase price = 350
    *           2 x 780 = 1560
    *   Game Up Menu -> Purchase price = 2500
    *           2 x 4990 = 4990
    *   Sum              = 13100
    */
    @Test
    public void testClose() {
        receiptSaleOne.close(paymentParams);
        assertEquals(6550, receiptSaleOne.getAdaptee().getSumPurchaseGrossPrice());
        assertEquals(5157, receiptSaleOne.getAdaptee().getSumPurchaseNetPrice());
        assertEquals(13100, receiptSaleOne.getAdaptee().getSumSaleGrossPrice());
        assertEquals(10314, receiptSaleOne.getAdaptee().getSumSaleNetPrice());
    }

    @Test
    public void testCloseWithDiscountPercent() {
        paymentParams.setDiscountPercent(10);
        receiptSaleOne.close(paymentParams);
        assertEquals(6550, receiptSaleOne.getAdaptee().getSumPurchaseGrossPrice());
        assertEquals(5157, receiptSaleOne.getAdaptee().getSumPurchaseNetPrice());
        assertEquals(11790, receiptSaleOne.getAdaptee().getSumSaleGrossPrice());
        assertEquals(9282, receiptSaleOne.getAdaptee().getSumSaleNetPrice());
        assertEquals(10, receiptSaleOne.getAdaptee().getDiscountPercent(), 0.0001);
    }

    @Test
    public void testCloseWithDiscountAbsolute() {
        paymentParams.setDiscountAbsolute(1000);
        receiptSaleOne.close(paymentParams);
        assertEquals(6550, receiptSaleOne.getAdaptee().getSumPurchaseGrossPrice());
        assertEquals(5157, receiptSaleOne.getAdaptee().getSumPurchaseNetPrice());
        assertEquals(12100, receiptSaleOne.getAdaptee().getSumSaleGrossPrice());
        assertEquals(9526, receiptSaleOne.getAdaptee().getSumSaleNetPrice());
        assertEquals(7.6335, receiptSaleOne.getAdaptee().getDiscountPercent(), 0.0001);
    }

    @Test
    public void testCloseWithDiscountForProduct() {
        GuardedTransaction.runWithRefresh(receiptSaleOne.getAdaptee(),
                () -> {
            schema.getReceiptRecordSaleTwo().setDiscountPercent(20);
            schema.getReceiptRecordSaleTwo().setSalePrice(240);
        });
        receiptSaleOne.close(paymentParams);
        assertEquals(6550, receiptSaleOne.getAdaptee().getSumPurchaseGrossPrice());
        assertEquals(5157, receiptSaleOne.getAdaptee().getSumPurchaseNetPrice());
        assertEquals(12460, receiptSaleOne.getAdaptee().getSumSaleGrossPrice());
        assertEquals(9811, receiptSaleOne.getAdaptee().getSumSaleNetPrice());
        assertEquals(0, receiptSaleOne.getAdaptee().getDiscountPercent(), 0.0001);
    }

    @Test(expected = IllegalReceiptStateException.class)
    public void testCloseAClosedReceipt() {
        GuardedTransaction.runWithRefresh(receiptSaleOne.getAdaptee(),
                () -> {
            schema.getReceiptSaleOne().setStatus(ReceiptStatus.CLOSED);
                    schema.getReceiptSaleOne().setClosureTime(LocalDateTime.now());
        });
        receiptSaleOne.close(paymentParams);
        assertEquals(850, receiptSaleOne.getAdaptee().getSumPurchaseGrossPrice());
    }

    @Test
    public void testPaySelective() {
        ReceiptRecordView receiptRecordViewTwo = new ReceiptRecordViewImpl(new ReceiptRecordAdapter(schema.getReceiptRecordSaleTwo()));
        ReceiptRecordView receiptRecordViewFive = new ReceiptRecordViewImpl(new ReceiptRecordAdapter(schema.getReceiptRecordSaleFive()));
        ReceiptRecordView receiptRecordViewSix = new ReceiptRecordViewImpl(new ReceiptRecordAdapter(schema.getReceiptRecordSaleSix()));
        List<ReceiptRecordView> recordsToPay = new ArrayList<>(Arrays.asList(receiptRecordViewTwo, receiptRecordViewFive, receiptRecordViewSix));
        receiptSaleOne.paySelective(tableAdapter, recordsToPay, paymentParams);
        // 1 x Soproni
        assertEquals(440, receiptSaleOne.getAdaptee().getRecords().stream()
                .mapToInt(record -> (int)(record.getSalePrice() * record.getSoldQuantity())).sum());
        List<Receipt> closedReceipts = GuardedTransaction.runNamedQuery(Receipt.GET_RECEIPT_BY_STATUS_AND_OWNER,
                query -> {query.setParameter("status", ReceiptStatus.CLOSED);
                                query.setParameter("number", tableAdapter.getAdaptee().getNumber());
                                return query;});
        assertEquals(2, closedReceipts.size());
        // 2 x Jim Beam, 2 x Edelweiss, 2 x Game Up Menu
        assertEquals(12660, closedReceipts.get(1).getRecords().stream()
                        .mapToInt(record -> (int)(record.getSalePrice() * record.getSoldQuantity())).sum());
    }

    @Test
    public void testGetTotalPrice() {
        assertEquals(13100, receiptSaleOne.getTotalPrice());
    }

    @Test
    public void testAddStockRecords() {
        List<StockParams> paramsList = Arrays.asList(StockParams.builder()
                .productName("productTwo")
                .quantity(Double.valueOf(2))
                .isAbsoluteQuantity(true)
                .build());
        receiptPurchase.addStockRecords(paramsList);
        assertEquals(1, receiptPurchase.getSoldProducts().size());
    }

    @Test
    public void testCancelReceiptRecord() {
        int recordNum = receiptSaleOne.getAdaptee().getRecords().size();
        receiptSaleOne.cancelReceiptRecord(receiptRecordSaleOne);
        assertEquals(recordNum - 1, receiptSaleOne.getAdaptee().getRecords().size());
    }

    @Test
    public void testCloneReceiptRecordAdapter() {
        int recordNum = receiptSaleOne.getAdaptee().getRecords().size();
        int totalReceiptRecordNum = GuardedTransaction.runNamedQuery(ReceiptRecord.GET_TEST_RECEIPT_RECORDS).size();
        receiptSaleOne.cloneReceiptRecordAdapter(receiptRecordSaleOne, 1);
        assertEquals(recordNum + 1, receiptSaleOne.getAdaptee().getRecords().size());
        assertEquals(totalReceiptRecordNum + 1,
                GuardedTransaction.runNamedQuery(ReceiptRecord.GET_TEST_RECEIPT_RECORDS).size());
    }
}
