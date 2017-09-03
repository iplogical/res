package com.inspirationlogical.receipt.corelib.model.adapter;

import static com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase.getReceiptsByClosureTime;
import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.*;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.inspirationlogical.receipt.corelib.model.TestBase;
import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecordCreated;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordViewImpl;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.params.StockParams;
import org.junit.Before;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.exception.IllegalReceiptStateException;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;

/**
 * Created by Bálint on 2017.03.13..
 */
public class ReceiptAdapterTest extends TestBase {

    private TableAdapter tableAdapter;
    private ProductAdapter productOne;
    private ReceiptAdapterBase receiptSaleOne;
    private ReceiptAdapterBase receiptSaleThree;
    private ReceiptAdapterBase receiptPurchase;
    private ReceiptRecordAdapter receiptRecordSaleOne;
    private PaymentParams paymentParams;

    @Before
    public void createAdapters() {
        tableAdapter = new TableAdapter(schema.getTableNormal());
        productOne = new ProductAdapter(schema.getProductOne());
        receiptSaleOne = new ReceiptAdapterBase(schema.getReceiptSaleOne());
        receiptSaleThree = new ReceiptAdapterBase(schema.getReceiptSaleThree());
        receiptPurchase = new ReceiptAdapterBase(schema.getReceiptPurchase());
        receiptRecordSaleOne = new ReceiptRecordAdapter(schema.getReceiptSaleOneRecordOne());
        paymentParams = PaymentParams.builder()
                .paymentMethod(PaymentMethod.CASH)
                .discountAbsolute(0)
                .discountPercent(0D)
                .build();
    }

    @Test
    public void testGetReceipts() {
        assertEquals(NUMBER_OF_RECEIPTS, ReceiptAdapterBase.getReceipts().size());
    }

    @Test
    public void testGetReceiptsByClosureTimeOneHourAgo() {
        assertEquals(NUMBER_OF_CLOSED_RECEIPTS, getReceiptsByClosureTime(now().minusHours(1)).size());
    }

    @Test
    public void testGetReceiptsByClosureTimeNow() {
        assertEquals(0, getReceiptsByClosureTime(now()).size());
    }

    @Test
    public void testDeleteReceipts() {
        List<ReceiptAdapterBase> closedReceipts = getReceiptsByClosureTime(now().minusHours(1));
        int numberOfRecords = closedReceipts.stream()
                .mapToInt(receiptAdapter -> receiptAdapter.getAdaptee().getRecords().size()).sum();
        ReceiptAdapterBase.deleteReceipts();
        assertEquals(0, getReceiptsByClosureTime(now().minusHours(1)).size());
        assertEquals(NUMBER_OF_RECEIPT_RECORDS - numberOfRecords, GuardedTransaction.runNamedQuery(ReceiptRecord.GET_TEST_RECEIPT_RECORDS).size());
        assertEquals(NUMBER_OF_RECEIPT_RECORD_CREATEDS - numberOfRecords, GuardedTransaction.runNamedQuery(ReceiptRecordCreated.GET_TEST_RECEIPT_RECORDS_CREATED).size());
    }

    @Test
    public void testGetSoldProducts() {
        assertEquals(4, receiptSaleOne.getSoldProducts().size());
    }

    @Test
    public void testSellProduct() {
        receiptSaleOne.sellProduct(productOne, 3, true, false);
        assertEquals(5, receiptSaleOne.getSoldProducts().size());
        assertEquals(1, receiptSaleOne.getSoldProducts().stream()
            .filter(receiptRecordAdapter -> receiptRecordAdapter.getAdaptee().getName().equals(productOne.getAdaptee().getLongName()))
            .collect(toList()).size());
        assertEquals(3, receiptSaleOne.getSoldProducts().stream()
                .filter(receiptRecordAdapter -> receiptRecordAdapter.getAdaptee().getName().equals(productOne.getAdaptee().getLongName()))
                .collect(toList()).get(0).getAdaptee().getSoldQuantity(), 0.001);
    }

    @Test
    public void testSellProductTwiceWithinTimeLimit() {
        receiptSaleOne.sellProduct(productOne, 3, true, false);
        assertEquals(5, receiptSaleOne.getSoldProducts().size());
        assertEquals(1, receiptSaleOne.getSoldProducts().stream()
                .filter(receiptRecordAdapter -> receiptRecordAdapter.getAdaptee().getName().equals(productOne.getAdaptee().getLongName()))
                .collect(toList()).size());
        receiptSaleOne.sellProduct(productOne, 1, true, false);
        assertEquals(5, receiptSaleOne.getSoldProducts().size());
        assertEquals(1, receiptSaleOne.getSoldProducts().stream()
                .filter(receiptRecordAdapter -> receiptRecordAdapter.getAdaptee().getName().equals(productOne.getAdaptee().getLongName()))
                .collect(toList()).size());
        assertEquals(4, receiptSaleOne.getSoldProducts().stream()
                .filter(receiptRecordAdapter -> receiptRecordAdapter.getAdaptee().getName().equals(productOne.getAdaptee().getLongName()))
                .collect(toList()).get(0).getAdaptee().getSoldQuantity(), 0.001);

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

    @Test
    public void testSellGameFee() {
        ReceiptRecordAdapter gameFeeRecord = receiptSaleOne.sellGameFee(2);
        assertEquals(5, receiptSaleOne.getSoldProducts().size());
        assertEquals(1, receiptSaleOne.getSoldProducts().stream()
            .filter(receiptRecordAdapter -> receiptRecordAdapter.getAdaptee().getName().equals(gameFeeRecord.getAdaptee().getName()))
            .collect(toList()).size());
        assertEquals(2, receiptSaleOne.getSoldProducts().stream()
                .filter(receiptRecordAdapter -> receiptRecordAdapter.getAdaptee().getName().equals(gameFeeRecord.getAdaptee().getName()))
                .collect(toList()).get(0).getAdaptee().getCreatedList().size());
    }

    @Test
    public void testSellGameFeeTwiceWithinTimeLimit() {
        receiptSaleOne.sellGameFee(2);
        ReceiptRecordAdapter gameFeeRecord = receiptSaleOne.sellGameFee(1);
        assertEquals(5, receiptSaleOne.getSoldProducts().size());
        assertEquals(1, receiptSaleOne.getSoldProducts().stream()
                .filter(receiptRecordAdapter -> receiptRecordAdapter.getAdaptee().getName().equals(gameFeeRecord.getAdaptee().getName()))
                .collect(toList()).size());
        assertEquals(3, receiptSaleOne.getSoldProducts().stream()
                .filter(receiptRecordAdapter -> receiptRecordAdapter.getAdaptee().getName().equals(gameFeeRecord.getAdaptee().getName()))
                .collect(toList()).get(0).getAdaptee().getCreatedList().size());
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
        GuardedTransaction.run(() -> {
            schema.getReceiptSaleOneRecordTwo().setDiscountPercent(20);
            schema.getReceiptSaleOneRecordTwo().setSalePrice(240);
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
        GuardedTransaction.run(() -> {
            schema.getReceiptSaleOne().setStatus(ReceiptStatus.CLOSED);
            schema.getReceiptSaleOne().setClosureTime(now());
        });
        receiptSaleOne.close(paymentParams);
    }

    @Test
    public void testCloseReceiptWithoutRecords() {
        receiptSaleThree.close(paymentParams);
        assertEquals(NUMBER_OF_RECEIPTS - 1,
                GuardedTransaction.runNamedQuery(Receipt.GET_RECEIPTS).size());
    }

    @Test
    public void testPaySelective() {
        ReceiptRecordView receiptRecordViewTwo = new ReceiptRecordViewImpl(new ReceiptRecordAdapter(schema.getReceiptSaleOneRecordTwo()));
        ReceiptRecordView receiptRecordViewFive = new ReceiptRecordViewImpl(new ReceiptRecordAdapter(schema.getReceiptSaleOneRecordThree()));
        ReceiptRecordView receiptRecordViewSix = new ReceiptRecordViewImpl(new ReceiptRecordAdapter(schema.getReceiptSaleOneRecordFour()));
        List<ReceiptRecordView> recordsToPay = new ArrayList<>(Arrays.asList(receiptRecordViewTwo, receiptRecordViewFive, receiptRecordViewSix));
        receiptSaleOne.paySelective(recordsToPay, paymentParams);
        // 1 x Soproni
        assertEquals(440, receiptSaleOne.getAdaptee().getRecords().stream()
                .mapToInt(record -> (int)(record.getSalePrice() * record.getSoldQuantity())).sum());
        List<Receipt> closedReceipts = getClosedReceipts();
        assertEquals(2, closedReceipts.size());
        // 2 x Jim Beam, 2 x Edelweiss, 2 x Game Up Menu
        assertEquals(12660, closedReceipts.get(1).getRecords().stream()
                        .mapToInt(record -> (int)(record.getSalePrice() * record.getSoldQuantity())).sum());
    }

    private List<Receipt> getClosedReceipts() {
        return GuardedTransaction.runNamedQuery(Receipt.GET_RECEIPT_BY_STATUS_AND_OWNER,
                query -> query.setParameter("status", ReceiptStatus.CLOSED).setParameter("number", tableAdapter.getAdaptee().getNumber()));
    }


    @Test
    public void testPayPartialHalf() {
        int closedReceiptsNumber = getClosedReceipts().size();
        double soldQuantityOne = receiptSaleOne.getAdaptee().getRecords().get(0).getSoldQuantity();
        double soldQuantityTwo = receiptSaleOne.getAdaptee().getRecords().get(1).getSoldQuantity();
        double soldQuantityThree = receiptSaleOne.getAdaptee().getRecords().get(2).getSoldQuantity();
        double soldQuantityFour = receiptSaleOne.getAdaptee().getRecords().get(3).getSoldQuantity();
        receiptSaleOne.payPartial(0.33, paymentParams);
        assertEquals(soldQuantityOne * (1 - 0.33), receiptSaleOne.getAdaptee().getRecords().get(0).getSoldQuantity(), 0.001);
        assertEquals(soldQuantityTwo * (1 - 0.33), receiptSaleOne.getAdaptee().getRecords().get(1).getSoldQuantity(), 0.001);
        assertEquals(soldQuantityThree * (1 - 0.33), receiptSaleOne.getAdaptee().getRecords().get(2).getSoldQuantity(), 0.001);
        assertEquals(soldQuantityFour * (1 - 0.33), receiptSaleOne.getAdaptee().getRecords().get(3).getSoldQuantity(), 0.001);
        List<ReceiptAdapterBase> closedReceipts = getReceiptsByClosureTime(now().minusSeconds(2));
        closedReceipts.sort(Comparator.comparing(receiptAdapter -> ((ReceiptAdapterBase)receiptAdapter).getAdaptee().getClosureTime()).reversed());
        ReceiptAdapterBase newClosedReceipt = closedReceipts.get(0);
        assertEquals(soldQuantityOne * 0.33, newClosedReceipt.getAdaptee().getRecords().get(0).getSoldQuantity(), 0.001);
        assertEquals(soldQuantityTwo * 0.33, newClosedReceipt.getAdaptee().getRecords().get(1).getSoldQuantity(), 0.001);
        assertEquals(soldQuantityThree * 0.33, newClosedReceipt.getAdaptee().getRecords().get(2).getSoldQuantity(), 0.001);
        assertEquals(soldQuantityFour * 0.33, newClosedReceipt.getAdaptee().getRecords().get(3).getSoldQuantity(), 0.001);
        assertEquals(closedReceiptsNumber + 1, getClosedReceipts().size());

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

    @Test
    public void testMergeReceiptRecords() {
        int numberOfRecords = receiptSaleOne.getAdaptee().getRecords().size();
        ReceiptRecordAdapter originalRecord = new ReceiptRecordAdapter(receiptSaleOne.getAdaptee().getRecords().get(0));
        double initialQuantity = originalRecord.getAdaptee().getSoldQuantity();
        receiptSaleOne.cloneReceiptRecordAdapter(originalRecord, 1);
        assertEquals(numberOfRecords + 1, receiptSaleOne.getAdaptee().getRecords().size());
        receiptSaleOne.mergeReceiptRecords();
        assertEquals(numberOfRecords, receiptSaleOne.getAdaptee().getRecords().size());
        assertEquals(initialQuantity + 1, receiptSaleOne.getAdaptee().getRecords().stream()
            .filter(record -> record.getName().equals(originalRecord.getAdaptee().getName()))
            .collect(toList()).get(0).getSoldQuantity(), 0.001);

    }
}
