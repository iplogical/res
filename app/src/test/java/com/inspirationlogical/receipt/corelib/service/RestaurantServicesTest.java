package com.inspirationlogical.receipt.corelib.service;

import com.inspirationlogical.receipt.corelib.model.adapter.RestaurantAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.TableAdapter;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.*;
import javafx.geometry.Point2D;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.persistence.EntityManager;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by Bálint on 2017.03.13..
 */
@PrepareForTest(RestaurantAdapter.class)
@RunWith(PowerMockRunner.class)
public class RestaurantServicesTest {
    private RestaurantServices service;

    @Mock
    private EntityManager manager;

    @Mock
    private RestaurantViewImpl restaurantView;

    @Mock
    private RestaurantAdapter restaurantAdapter;

    @Mock
    private TableViewImpl tableView;

    @Mock
    private TableAdapter tableAdapter;

    @Mock
    TableViewBuilder builder;

    @Mock
    Point2D position;

    @Before
    public void createService() {
        service = new RestaurantServicesImpl(manager);
    }

    @Test
    public void testGetActiveRestaurant() {
        //given
        mockStatic(RestaurantAdapter.class);
        when(RestaurantAdapter.restaurantAdapterFactory(any())).thenReturn(restaurantAdapter);
        //when
        service.getActiveRestaurant();
        //then
        verifyStatic(times(1));
        RestaurantAdapter.restaurantAdapterFactory(eq(manager));
    }

    @Test
    public void testGetTables() {
        //given
        when(restaurantView.getAdapter()).thenReturn(restaurantAdapter);
        //when
        service.getTables(restaurantView);
        //then
        verify(restaurantAdapter).getDisplayableTables();
    }

    @Test
    public void testSetTableName() {
        //given
        when(tableView.getAdapter()).thenReturn(tableAdapter);
        //when
        service.setTableName(tableView, "TestName");
        //then
        verify(tableAdapter).setTableName(eq("TestName"));
    }

    @Test
    public void testSetTableCapacity() {
        //given
        when(tableView.getAdapter()).thenReturn(tableAdapter);
        //when
        service.setTableCapacity(tableView, 10);
        //then
        verify(tableAdapter).setCapacity(eq(10));
    }

    @Test
    public void testAddTableNote() {
        //given
        when(tableView.getAdapter()).thenReturn(tableAdapter);
        //when
        service.addTableNote(tableView, "Note");
        //then
        verify(tableAdapter).setNote(eq("Note"));
    }

    @Test
    public void testDisplayTable() {
        //given
        when(tableView.getAdapter()).thenReturn(tableAdapter);
        //when
        service.displayTable(tableView);
        //then
        verify(tableAdapter).displayTable();
    }

    @Test
    public void testHideTable() {
        //given
        when(tableView.getAdapter()).thenReturn(tableAdapter);
        //when
        service.hideTable(tableView);
        //then
        verify(tableAdapter).hideTable();
    }

    @Test
    public void testMoveTable() {
        //given
        when(tableView.getAdapter()).thenReturn(tableAdapter);
        //when
        service.moveTable(tableView, position);
        //then
        verify(tableAdapter).moveTable(eq(position));
    }

    @Test
    public void testAddTable() {
        //given
        when(restaurantView.getAdapter()).thenReturn(restaurantAdapter);
        //when
        service.addTable(restaurantView, TableType.NORMAL, 5);
        //then
        verify(restaurantAdapter).addTable(TableType.NORMAL, 5);
    }

    @Test
    public void testAddTableBuilder() {
        //given
        when(restaurantView.getAdapter()).thenReturn(restaurantAdapter);
        //when
        service.addTable(restaurantView, builder);
        //then
        verify(restaurantAdapter).addTable(builder);
    }

    @Test
    public void testGetActiveReceipt() {
        //given
        when(tableView.getAdapter()).thenReturn(tableAdapter);
        //when
        service.getActiveReceipt(tableView);
        //then
        verify(tableAdapter).getActiveReceipt();
    }
}
