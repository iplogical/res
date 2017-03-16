package com.inspirationlogical.receipt.model.adapter;

import com.inspirationlogical.receipt.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.model.EntityManagerFactoryRule;
import com.inspirationlogical.receipt.model.enums.TableType;
import com.inspirationlogical.receipt.model.view.TableViewBuilder;
import javafx.geometry.Point2D;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

/**
 * Created by Bálint on 2017.03.13..
 */
public class RestaurantAdapterTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testRestaurantHasDisplayableTables() {
        RestaurantAdapter restaurantAdapter = new RestaurantAdapter(schema.getRestaurant(), schema.getEntityManager());
        assertEquals(3, restaurantAdapter.getDisplayableTables().size());
    }

    @Test
    public void testAddTable() {
        RestaurantAdapter restaurantAdapter = new RestaurantAdapter(schema.getRestaurant(), schema.getEntityManager());
        restaurantAdapter.addTable(TableType.NORMAL, 4);
        assertEquals(4, restaurantAdapter.getDisplayableTables().size());
    }

    @Test
    public void testAddTableBuilder() {
        RestaurantAdapter restaurantAdapter = new RestaurantAdapter(schema.getRestaurant(), schema.getEntityManager());
        restaurantAdapter.addTable(new TableViewBuilder(TableType.NORMAL, 4)
                .name("Ittas Juci")
                .position(new Point2D(20, 20))
                .guestNumber(5)
                .tableCapacity(5)
                .note("Big Chocklate Cake")
                .visibility(true));
        assertEquals(4, restaurantAdapter.getDisplayableTables().size());
    }
}
