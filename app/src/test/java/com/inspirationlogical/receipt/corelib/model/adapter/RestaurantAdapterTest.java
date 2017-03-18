package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.entity.Table.TableBuilder;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import javafx.geometry.Point2D;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Bálint on 2017.03.13..
 */
public class RestaurantAdapterTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testRestaurantHasDisplayableTables() {
        RestaurantAdapter restaurantAdapter = new RestaurantAdapter(schema.getRestaurant());
        assertEquals(3, restaurantAdapter.getDisplayableTables().size());
    }

    @Test
    public void testAddTable() {
        RestaurantAdapter restaurantAdapter = new RestaurantAdapter(schema.getRestaurant());
        restaurantAdapter.addTable(TableType.NORMAL, 4);
        assertEquals(4, restaurantAdapter.getDisplayableTables().size());
    }

    @Test
    public void testAddTableBuilder() {
        RestaurantAdapter restaurantAdapter = new RestaurantAdapter(schema.getRestaurant());
        restaurantAdapter.addTable(Table.builder()
                .name("Ittas Juci")
                .number(88)
                .type(TableType.NORMAL)
                .coordinateX(20)
                .coordinateY(20)
                .guestNumber(5)
                .capacity(5)
                .note("Big Chocklate Cake")
                .visibility(true));
        assertEquals(4, restaurantAdapter.getDisplayableTables().size());
    }
}
