package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptRecordType;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.service.PaymentParams;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Bálint on 2017.03.13..
 */
public class ReceiptAdapterTest {

    private ReceiptAdapter receiptAdapter;
    private ProductAdapter productAdapter;
    private PaymentParams paymentParams;

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void createAdapters() {
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
        assertEquals(2, receiptAdapter.getSoldProducts().size());
    }

    @Test
    public void testSellProduct() {
        receiptAdapter.sellProduct(productAdapter, 1, paymentParams);
        assertEquals(3, receiptAdapter.getSoldProducts().size());
    }

    /*
    *   Soproni 0,5L -> Purchase price = 250
    *           1 x 440 = 440
    *   Jim Beam ->     Purchase price = 300
    *           2 x 560 = 1120
    *   Sum             = 1560
    */
    @Test
    public void testClose() {
        receiptAdapter.close(paymentParams);
        assertEquals(850, receiptAdapter.getAdaptee().getSumPurchaseGrossPrice());
        assertEquals(669, receiptAdapter.getAdaptee().getSumPurchaseNetPrice());
        assertEquals(1560, receiptAdapter.getAdaptee().getSumSaleGrossPrice());
        assertEquals(1228, receiptAdapter.getAdaptee().getSumSaleNetPrice());
    }

    @Test
    public void testCloseWithDiscountPercent() {
        paymentParams.setDiscountPercent(10);
        receiptAdapter.close(paymentParams);
        assertEquals(850, receiptAdapter.getAdaptee().getSumPurchaseGrossPrice());
        assertEquals(669, receiptAdapter.getAdaptee().getSumPurchaseNetPrice());
        assertEquals(1404, receiptAdapter.getAdaptee().getSumSaleGrossPrice());
        assertEquals(1105, receiptAdapter.getAdaptee().getSumSaleNetPrice());
        assertEquals(10, receiptAdapter.getAdaptee().getDiscountPercent(), 0.0001);
    }

    @Test
    public void testCloseWithDiscountAbsolute() {
        paymentParams.setDiscountAbsolute(500);
        receiptAdapter.close(paymentParams);
        assertEquals(850, receiptAdapter.getAdaptee().getSumPurchaseGrossPrice());
        assertEquals(669, receiptAdapter.getAdaptee().getSumPurchaseNetPrice());
        assertEquals(1060, receiptAdapter.getAdaptee().getSumSaleGrossPrice());
        assertEquals(834, receiptAdapter.getAdaptee().getSumSaleNetPrice());
        assertEquals(32.0512, receiptAdapter.getAdaptee().getDiscountPercent(), 0.0001);
    }

    @Test
    public void testCloseWithDiscountForProduct() {
        GuardedTransaction.RunWithRefresh(receiptAdapter.getAdaptee(),
                () -> schema.getReceiptRecordSaleTwo().setDiscountPercent(20));
        receiptAdapter.close(paymentParams);
        assertEquals(850, receiptAdapter.getAdaptee().getSumPurchaseGrossPrice());
        assertEquals(669, receiptAdapter.getAdaptee().getSumPurchaseNetPrice());
        assertEquals(1336, receiptAdapter.getAdaptee().getSumSaleGrossPrice());
        assertEquals(1051, receiptAdapter.getAdaptee().getSumSaleNetPrice());
        assertEquals(0, receiptAdapter.getAdaptee().getDiscountPercent(), 0.0001);
    }
}
