package com.inspirationlogical.receipt.model.adapter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.model.listeners.ReceiptPrinter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.model.EntityManagerFactoryRule;
import org.mockito.Mock;
import org.mockito.Mockito;

public class ReceiptPrinterTest {

    private ReceiptAdapter receipt;

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void persistObjects() {
        receipt = new ReceiptAdapter(schema.getReceiptSaleOne(), schema.getEntityManager());
    }


    @Test
    public void test_receipt_is_printed_on_close() {
        ReceiptAdapter.Listener printer = Mockito.mock(ReceiptAdapter.Listener.class);
        Collection<ReceiptAdapter.Listener> listeners = Arrays.asList(printer);
        receipt.close(listeners);
        verify(printer).onClose(receipt);
    }
}