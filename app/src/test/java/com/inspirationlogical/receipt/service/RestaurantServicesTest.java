package com.inspirationlogical.receipt.service;

import com.inspirationlogical.receipt.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.model.EntityManagerFactoryRule;
import com.inspirationlogical.receipt.model.TestType;
import com.inspirationlogical.receipt.model.enums.TableType;
import com.inspirationlogical.receipt.model.view.RestaurantView;
import com.inspirationlogical.receipt.model.view.TableView;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

/**
 * Created by Bálint on 2017.03.13..
 */
public class RestaurantServicesTest {
    private EntityManager manager;
    RestaurantServices service;
    RestaurantView restaurantView;

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule(TestType.VALIDATE);

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void persistObjects() {
        manager = factory.getEntityManager();
        service = new RestaurantServicesImpl(manager);
        restaurantView = service.getActiveRestaurant();
    }

    @Test
    public void testGetActiveRestaurant() {
        restaurantView = service.getActiveRestaurant();
        assertNotNull(restaurantView);
        assertEquals("GameUp Pub",restaurantView.getRestaurantName());
    }

    @Test
    public void testGetTables() {
        RestaurantView restaurantView = service.getActiveRestaurant();
        assertNotNull(service.getTables(restaurantView));
    }

    @Test
    public void testAddTable() {
        TableView tableView = service.addTable(restaurantView, TableType.NORMAL, 3);
        assertEquals(3, tableView.getTableNumber());
        assertEquals(TableType.NORMAL, tableView.getType());
    }
}
