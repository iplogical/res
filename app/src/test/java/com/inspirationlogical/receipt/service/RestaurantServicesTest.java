package com.inspirationlogical.receipt.service;

import com.inspirationlogical.receipt.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.model.EntityManagerFactoryRule;
import com.inspirationlogical.receipt.model.TestType;
import com.inspirationlogical.receipt.model.adapter.RestaurantAdapter;
import com.inspirationlogical.receipt.model.entity.Restaurant;
import com.inspirationlogical.receipt.model.enums.TableType;
import com.inspirationlogical.receipt.model.view.RestaurantView;
import com.inspirationlogical.receipt.model.view.RestaurantViewImpl;
import com.inspirationlogical.receipt.model.view.TableView;
import com.inspirationlogical.receipt.model.view.TableViewBuilder;
import com.inspirationlogical.receipt.view.NodeUtility;
import com.inspirationlogical.receipt.view.PressAndHoldHandler;
import com.inspirationlogical.receipt.view.ViewLoader;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.persistence.EntityManager;

import java.util.List;

import static org.junit.Assert.*;
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
    private RestaurantAdapter restaurantAdapter;

    @Mock
    private RestaurantViewImpl restaurantView;

    @Mock
    TableViewBuilder builder;

    @Before
    public void persistObjects() {
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
    }}
