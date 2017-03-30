package com.inspirationlogical.receipt.corelib.service;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Collection;
import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import com.inspirationlogical.receipt.corelib.model.adapter.ProductAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.TableAdapter;
import com.inspirationlogical.receipt.corelib.model.view.ProductViewImpl;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.TableViewImpl;

/**
 * Created by Bálint on 2017.03.15..
 */
@RunWith(PowerMockRunner.class)
public class RetailServicesTest {
    private RetailServices service;

    @Mock
    private EntityManager manager;

    @Mock
    private TableViewImpl tableView;

    @Mock
    private TableAdapter tableAdapter;

    @Mock
    private ReceiptAdapter receiptAdapter;

    @Mock
    private ProductViewImpl productView;

    @Mock
    private Collection<ReceiptRecordView> records;

    @Mock
    private PaymentParams paymentParams;

    @Mock
    private AdHocProductParams adHocParams;

    @Mock
    private ProductAdapter productAdapter;

    @Before
    public void createService() {
        service = new RetailServicesImpl(manager);
    }

    @Test
    public void testOpenTable() {
        //given
        when(tableView.getAdapter()).thenReturn(tableAdapter);
        //when
        service.openTable(tableView);
        //then
        verify(tableAdapter).openTable();
    }

    @Test
    public void testSellProduct() {
        //given
        when(tableView.getAdapter()).thenReturn(tableAdapter);
        when(productView.getAdapter()).thenReturn(productAdapter);
        when(tableAdapter.getActiveReceipt()).thenReturn(receiptAdapter);
        //when
        service.sellProduct(tableView, productView, 1, false);
        //then
        verify(tableAdapter).getActiveReceipt();
        verify(receiptAdapter).sellProduct(productAdapter, 1, false);
    }

    @Test
    public void testSellAdHocProduct() {
        //given
        when(tableView.getAdapter()).thenReturn(tableAdapter);
        when(productView.getAdapter()).thenReturn(productAdapter);
        when(tableAdapter.getActiveReceipt()).thenReturn(receiptAdapter);
        //when
        service.sellAdHocProduct(tableView, adHocParams, true);
        //then
        verify(tableAdapter).getActiveReceipt();
        verify(receiptAdapter).sellAdHocProduct(adHocParams, true);
    }

    @Test
    public void testPayTable() {
        //given
        when(tableView.getAdapter()).thenReturn(tableAdapter);
        //when
        service.payTable(tableView, paymentParams);
        //then
        verify(tableAdapter).payTable(paymentParams);
    }

    @Test
    public void testPaySelective() {
        //given
        when(tableView.getAdapter()).thenReturn(tableAdapter);
        //when
        service.paySelective(tableView, records, paymentParams);
        //then
        verify(tableAdapter).paySelective(records, paymentParams);
    }
}
