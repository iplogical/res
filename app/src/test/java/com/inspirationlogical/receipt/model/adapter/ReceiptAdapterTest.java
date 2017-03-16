package com.inspirationlogical.receipt.model.adapter;

import com.inspirationlogical.receipt.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.model.EntityManagerFactoryRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

/**
 * Created by Bálint on 2017.03.13..
 */
public class ReceiptAdapterTest {
    private EntityManager manager;

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testGetSoldProducts() {
        ReceiptAdapter receiptAdapter = new ReceiptAdapter(schema.getReceiptSaleOne(), schema.getEntityManager());
        assertEquals(2, receiptAdapter.getSoldProducts().size());
    }
}
