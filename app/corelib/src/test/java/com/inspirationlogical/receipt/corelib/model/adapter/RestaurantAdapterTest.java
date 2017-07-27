package com.inspirationlogical.receipt.corelib.model.adapter;

import static com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule.NUMBER_OF_DISPLAYABLE_TABLES;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;

/**
 * Created by Bálint on 2017.03.13..
 */
public class RestaurantAdapterTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testAddTableBuilder() {
        RestaurantAdapter restaurantAdapter = new RestaurantAdapter(schema.getRestaurant());
        restaurantAdapter.addTable(Table.builder()
                .name("Ittas Juci")
                .number(88)
                .type(TableType.NORMAL)
                .coordinateX(20)
                .coordinateY(20)
                .guestCount(5)
                .capacity(5)
                .note("Big Chocklate Cake")
                .visible(true));
        assertEquals(NUMBER_OF_DISPLAYABLE_TABLES + 1, TableAdapter.getDisplayableTables().size());
    }
}
