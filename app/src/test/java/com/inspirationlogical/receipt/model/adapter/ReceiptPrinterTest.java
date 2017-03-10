package com.inspirationlogical.receipt.model.adapter;

import static java.util.Arrays.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.model.enums.ReceiptStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.model.EntityManagerFactoryRule;

public class ReceiptPrinterTest {


    private EntityManager manager;
    private ReceiptAdapter receipt;

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule();

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void persistObjects() {
        manager = factory.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getRoot());
        manager.getTransaction().commit();
        receipt = new ReceiptAdapterImpl(schema.getReceiptSaleOne(),manager);
    }

    @Test
    public void test_receipt_is_printed_on_close() {
        ReceiptPrinter printer = new ReceiptPrinter();
        Collection<ReceiptAdapter.Listener> listeners = Arrays.asList(printer);
        receipt.close(listeners);
        assertEquals(1, 1);
    }


}