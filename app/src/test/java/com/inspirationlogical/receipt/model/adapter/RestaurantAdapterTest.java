package com.inspirationlogical.receipt.model.adapter;

import com.inspirationlogical.receipt.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.model.EntityManagerFactoryRule;
import com.inspirationlogical.receipt.model.enums.TableType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

/**
 * Created by Bálint on 2017.03.13..
 */
public class RestaurantAdapterTest {

    private EntityManager manager;

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule();

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void persistObjects() {
        manager = factory.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getRestaurant());
        manager.getTransaction().commit();
    }

    @Test
    public void testRestaurantHasDisplayableTables() {
        RestaurantAdapter restaurantAdapter = new RestaurantAdapter(schema.getRestaurant(), manager);
        assertEquals(2, restaurantAdapter.getDisplayableTables().size());
    }

    @Test
    public void testAddTable() {
        RestaurantAdapter restaurantAdapter = new RestaurantAdapter(schema.getRestaurant(), manager);
        restaurantAdapter.addTable(TableType.NORMAL, 3);
        assertEquals(3, restaurantAdapter.getDisplayableTables().size());
    }
}
