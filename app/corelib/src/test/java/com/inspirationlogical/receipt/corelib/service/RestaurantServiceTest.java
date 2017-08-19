package com.inspirationlogical.receipt.corelib.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.inspirationlogical.receipt.corelib.model.adapter.RestaurantAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.TableAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.Table.TableBuilder;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantViewImpl;
import com.inspirationlogical.receipt.corelib.model.view.TableViewImpl;

import javafx.geometry.Point2D;

/**
 * Created by Bálint on 2017.03.13..
 */
@PrepareForTest(RestaurantAdapter.class)
@RunWith(PowerMockRunner.class)
public class RestaurantServiceTest {
    private RestaurantService service;

    @Mock
    private EntityViews entityViews;

    @Mock
    private RestaurantViewImpl restaurantView;

    @Mock
    private RestaurantAdapter restaurantAdapter;

    @Mock
    private TableViewImpl tableView;

    @Mock
    private TableAdapter tableAdapter;

    @Mock
    TableBuilder builder;

    @Mock
    Point2D position;

    @Before
    public void createService() {
        service = new RestaurantServiceImpl(entityViews);
    }

    @Test
    public void testGetActiveRestaurant() {
        //given
        mockStatic(RestaurantAdapter.class);
        when(RestaurantAdapter.getActiveRestaurant()).thenReturn(restaurantAdapter);
        //when
        service.getActiveRestaurant();
        //then
        verifyStatic(times(1));
        RestaurantAdapter.getActiveRestaurant();
    }

    @Test
    public void testSetTableNumber() {
        //given
        when(tableView.getAdapter()).thenReturn(tableAdapter);
        when(restaurantView.getAdapter()).thenReturn(restaurantAdapter);
        //when
        service.setTableNumber(tableView, 8, restaurantView);
        //then
        verify(tableAdapter).setNumber(eq(8));
    }

    @Test
    public void testMoveTable() {
        //given
        when(tableView.getAdapter()).thenReturn(tableAdapter);
        //when
        service.setTablePosition(tableView, position);
        //then
        verify(tableAdapter).setPosition(eq(position));
    }

    @Test
    public void testRotateTable() {
        //given
        when(tableView.getAdapter()).thenReturn(tableAdapter);
        //when
        service.rotateTable(tableView);
        //then
        verify(tableAdapter).rotateTable();
    }

    @Test
    public void testDeleteTable() {
        //given
        when(tableView.getAdapter()).thenReturn(tableAdapter);
        //when
        service.deleteTable(tableView);
        //then
        verify(tableAdapter).deleteTable();
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
        service.getOpenReceipt(tableView);
        //then
        verify(tableAdapter).getOpenReceipt();
    }
}
