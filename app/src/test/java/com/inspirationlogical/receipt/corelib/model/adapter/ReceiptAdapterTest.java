package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptRecordType;
import com.inspirationlogical.receipt.corelib.service.PaymentParams;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;

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
        receiptAdapter = new ReceiptAdapter(schema.getReceiptSaleOne(), schema.getEntityManager());
        productAdapter = new ProductAdapter(schema.getProductOne(), schema.getEntityManager());
        paymentParams = PaymentParams.builder()
                .receiptRecordType(ReceiptRecordType.HERE)
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
}
