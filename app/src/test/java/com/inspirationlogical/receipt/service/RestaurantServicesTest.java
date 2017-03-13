package com.inspirationlogical.receipt.service;

import com.inspirationlogical.receipt.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.model.EntityManagerFactoryRule;
import com.inspirationlogical.receipt.model.TestType;
import com.inspirationlogical.receipt.model.view.RestaurantView;
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

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule(TestType.VALIDATE);

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void persistObjects() {
        manager = factory.getEntityManager();
        service = new RestaurantServicesImpl(manager);
    }

    @Test
    public void testGetActiveRestaurant() {
        RestaurantView restaurantView = service.getActiveRestaurant();
        assertNotNull(restaurantView);
        assertEquals("GameUp Pub",restaurantView.getRestaurantName());
    }

    @Test
    public void testGetTables() {
        RestaurantView restaurantView = service.getActiveRestaurant();
        assertNotNull(service.getTables(restaurantView));
    }
}
