package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.exception.IllegalReceiptStateException;
import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptRecordType;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordViewImpl;
import com.inspirationlogical.receipt.corelib.service.PaymentParams;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Bálint on 2017.03.13..
 */
public class ReceiptAdapterTest {

    private TableAdapter tableAdapter;
    private ReceiptAdapter receiptAdapter;
    private ProductAdapter productAdapter;
    private PaymentParams paymentParams;

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void createAdapters() {
        tableAdapter = new TableAdapter(schema.getTableNormal());
        receiptAdapter = new ReceiptAdapter(schema.getReceiptSaleOne());
        productAdapter = new ProductAdapter(schema.getProductOne());
        paymentParams = PaymentParams.builder()
                .receiptRecordType(ReceiptRecordType.HERE)
                .paymentMethod(PaymentMethod.CASH)
                .discountAbsolute(0)
                .discountPercent(0D)
                .build();
    }

    @Test
    public void testGetSoldProducts() {
        assertEquals(4, receiptAdapter.getSoldProducts().size());
    }

    @Test
    public void testSellProduct() {
        receiptAdapter.sellProduct(productAdapter, 1, paymentParams);
        assertEquals(5, receiptAdapter.getSoldProducts().size());
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
        receiptAdapter.close(paymentParams);
        assertEquals(6550, receiptAdapter.getAdaptee().getSumPurchaseGrossPrice());
        assertEquals(5157, receiptAdapter.getAdaptee().getSumPurchaseNetPrice());
        assertEquals(13100, receiptAdapter.getAdaptee().getSumSaleGrossPrice());
        assertEquals(10314, receiptAdapter.getAdaptee().getSumSaleNetPrice());
    }

    @Test
    public void testCloseWithDiscountPercent() {
        paymentParams.setDiscountPercent(10);
        receiptAdapter.close(paymentParams);
        assertEquals(6550, receiptAdapter.getAdaptee().getSumPurchaseGrossPrice());
        assertEquals(5157, receiptAdapter.getAdaptee().getSumPurchaseNetPrice());
        assertEquals(11790, receiptAdapter.getAdaptee().getSumSaleGrossPrice());
        assertEquals(9282, receiptAdapter.getAdaptee().getSumSaleNetPrice());
        assertEquals(10, receiptAdapter.getAdaptee().getDiscountPercent(), 0.0001);
    }

    @Test
    public void testCloseWithDiscountAbsolute() {
        paymentParams.setDiscountAbsolute(1000);
        receiptAdapter.close(paymentParams);
        assertEquals(6550, receiptAdapter.getAdaptee().getSumPurchaseGrossPrice());
        assertEquals(5157, receiptAdapter.getAdaptee().getSumPurchaseNetPrice());
        assertEquals(12100, receiptAdapter.getAdaptee().getSumSaleGrossPrice());
        assertEquals(9526, receiptAdapter.getAdaptee().getSumSaleNetPrice());
        assertEquals(7.6335, receiptAdapter.getAdaptee().getDiscountPercent(), 0.0001);
    }

    @Test
    public void testCloseWithDiscountForProduct() {
        GuardedTransaction.RunWithRefresh(receiptAdapter.getAdaptee(),
                () -> schema.getReceiptRecordSaleTwo().setDiscountPercent(20));
        receiptAdapter.close(paymentParams);
        assertEquals(6550, receiptAdapter.getAdaptee().getSumPurchaseGrossPrice());
        assertEquals(5157, receiptAdapter.getAdaptee().getSumPurchaseNetPrice());
        assertEquals(12876, receiptAdapter.getAdaptee().getSumSaleGrossPrice());
        assertEquals(10138, receiptAdapter.getAdaptee().getSumSaleNetPrice());
        assertEquals(0, receiptAdapter.getAdaptee().getDiscountPercent(), 0.0001);
    }

    @Test(expected = IllegalReceiptStateException.class)
    public void testCloseAClosedReceipt() {
        GuardedTransaction.RunWithRefresh(receiptAdapter.getAdaptee(),
                () -> {
            schema.getReceiptSaleOne().setStatus(ReceiptStatus.CLOSED);
                    schema.getReceiptSaleOne().setClosureTime(Calendar.getInstance());
        });
        receiptAdapter.close(paymentParams);
        assertEquals(850, receiptAdapter.getAdaptee().getSumPurchaseGrossPrice());
    }

    @Test
    public void testPaySelective() {
        ReceiptRecordView receiptRecordViewTwo = new ReceiptRecordViewImpl(new ReceiptRecordAdapter(schema.getReceiptRecordSaleTwo()));
        receiptRecordViewTwo.setPaidQuantity(1);
        ReceiptRecordView receiptRecordViewFive = new ReceiptRecordViewImpl(new ReceiptRecordAdapter(schema.getReceiptRecordSaleFive()));
        receiptRecordViewFive.setPaidQuantity(2);
        ReceiptRecordView receiptRecordViewSix = new ReceiptRecordViewImpl(new ReceiptRecordAdapter(schema.getReceiptRecordSaleSix()));
        receiptRecordViewSix.setPaidQuantity(0.5);
        List<ReceiptRecordView> recordsToPay = new ArrayList<>(Arrays.asList(receiptRecordViewTwo, receiptRecordViewFive, receiptRecordViewSix));
        receiptAdapter.paySelective(tableAdapter, recordsToPay, paymentParams);
        // 1 x Soproni, 1 x Jim Beam, 1,5 x Game Up Menu
        assertEquals(8485, receiptAdapter.getAdaptee().getRecords().stream()
                .mapToInt(record -> (int)(record.getSalePrice() * record.getSoldQuantity())).sum());
        List<Receipt> closedReceipts = schema.getEntityManager().createNamedQuery(Receipt.GET_RECEIPT_BY_STATUS_AND_OWNER)
                .setParameter("status", ReceiptStatus.CLOSED)
                .setParameter("number", tableAdapter.getAdaptee().getNumber())
                .getResultList();
        assertEquals(2, closedReceipts.size());
        // 1 x Jim Beam, 2 x Edelweiss, 0,5 x Game Up Menu
        assertEquals(4615, closedReceipts.get(1).getRecords().stream()
                        .mapToInt(record -> (int)(record.getSalePrice() * record.getSoldQuantity())).sum());
    }
}
