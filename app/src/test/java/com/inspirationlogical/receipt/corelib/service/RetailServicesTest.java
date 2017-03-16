package com.inspirationlogical.receipt.corelib.service;

import com.inspirationlogical.receipt.corelib.model.adapter.TableAdapter;
import com.inspirationlogical.receipt.corelib.model.view.TableViewImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.persistence.EntityManager;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

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

    @Before
    public void createService() {
        service = new RetailServicesImpl(manager);
    }

    @Test
    public void testSetTableName() {
        //given
        when(tableView.getAdapter()).thenReturn(tableAdapter);
        //when
        service.openTable(tableView);
        //then
        verify(tableAdapter).openTable();
    }
}
