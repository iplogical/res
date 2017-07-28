package com.inspirationlogical.receipt.corelib.model.adapter;

import static com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule.NUMBER_OF_DISPLAYABLE_TABLES;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;

/**
 * Created by Bálint on 2017.03.13..
 */
public class RestaurantAdapterTest {

    private RestaurantAdapter restaurantAdapter;
    private Table.TableBuilder tableBuilder;

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void setUp() {
        restaurantAdapter = new RestaurantAdapter(schema.getRestaurant());
        tableBuilder =  Table.builder()
                .name("Ittas Juci")
                .number(88)
                .type(TableType.NORMAL)
                .coordinateX(20)
                .coordinateY(20)
                .guestCount(5)
                .capacity(5)
                .note("Big Chocklate Cake")
                .visible(true);
    }

    @Test
    public void testGetActiveRestaurant() {
        assertNotNull(RestaurantAdapter.getActiveRestaurant());
    }

    @Test
    public void testAddTable() {
        restaurantAdapter.addTable(tableBuilder);
        assertEquals(NUMBER_OF_DISPLAYABLE_TABLES + 1, TableAdapter.getDisplayableTables().size());
    }

    @Test(expected = IllegalTableStateException.class)
    public void testAddTableNumberAlreadyUsed() {
        tableBuilder.number(schema.getTableNormal().getNumber());
        restaurantAdapter.addTable(tableBuilder);
    }

    @Test
    public void testAddTableNumberAlreadyUsedHost() {
        int firstUnsued = TableAdapter.getFirstUnusedNumber();
        tableBuilder.number(schema.getTableNormal().getNumber())
                .type(TableType.LOITERER);
        restaurantAdapter.addTable(tableBuilder);
        assertNotNull(TableAdapter.getTableByNumber(firstUnsued));
        assertTrue(TableAdapter.getTableByNumber(schema.getTableNormal().getNumber()).isTableHost());
        assertEquals(NUMBER_OF_DISPLAYABLE_TABLES + 1, TableAdapter.getDisplayableTables().size());
    }

}
