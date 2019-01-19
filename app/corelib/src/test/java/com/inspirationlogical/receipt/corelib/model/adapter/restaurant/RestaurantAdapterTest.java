package com.inspirationlogical.receipt.corelib.model.adapter.restaurant;

//import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.NUMBER_OF_DISPLAYABLE_TABLES;
//import static com.inspirationlogical.receipt.corelib.model.adapter.TableAdapter.getTable;
//import static java.time.LocalDateTime.now;
//import static java.util.stream.Collectors.toList;
//import static junit.framework.TestCase.assertNotNull;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;
//import static org.junit.Assert.assertTrue;
//
//import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.model.TestBase;
//import org.junit.Before;
//import org.junit.Test;
//
//import com.inspirationlogical.receipt.corelib.model.entity.Table;
//import com.inspirationlogical.receipt.corelib.model.enums.TableType;
//
//import java.util.Arrays;

/**
 * Created by Bálint on 2017.03.13..
 */
public class RestaurantAdapterTest extends TestBase {

//    private RestaurantAdapterImpl restaurantAdapter;
//    private Table.TableBuilder tableBuilder;
//    private TableAdapter tableNormal;
//    private TableAdapter tableNormalClosed;
//    private TableAdapter tableConsumer;
//    private TableAdapter tableConsumed;
//
//    @Before
//    public void setUp() {
//        restaurantAdapter = new RestaurantAdapterImpl(schema.getRestaurant());
//        tableNormal = new TableAdapter(schema.getTableNormal());
//        tableNormalClosed = new TableAdapter(schema.getTableNormalClosed());
//        tableConsumer = new TableAdapter(schema.getTableConsumer());
//        tableConsumed = new TableAdapter(schema.getTableConsumed());
//
//        tableBuilder =  Table.builder()
//                .name("Ittas Juci")
//                .number(88)
//                .type(TableType.NORMAL)
//                .coordinateX(20)
//                .coordinateY(20)
//                .guestCount(5)
//                .capacity(5)
//                .note("Big Chocklate Cake")
//                .visible(true);
//    }
//
//    @Test
//    public void testGetActiveRestaurant() {
//        assertNotNull(RestaurantAdapterImpl.getActiveRestaurant());
//    }
//
//    @Test
//    public void testAddTable() {
//        restaurantAdapter.addTable(tableBuilder);
//        assertEquals(NUMBER_OF_DISPLAYABLE_TABLES + 1, TableAdapter.getDisplayableTables().size());
//    }
//
//    @Test(expected = IllegalTableStateException.class)
//    public void testAddTableNumberAlreadyUsed() {
//        tableBuilder.number(schema.getTableNormal().getNumber());
//        restaurantAdapter.addTable(tableBuilder);
//    }
//
//    @Test
//    public void testAddTableNumberAlreadyUsedHost() {
//        int firstUnsued = TableAdapter.getFirstUnusedNumber();
//        tableBuilder.number(schema.getTableNormal().getNumber())
//                .type(TableType.LOITERER);
//        restaurantAdapter.addTable(tableBuilder);
//        assertNotNull(getTable(firstUnsued));
//        assertTrue(getTable(schema.getTableNormal().getNumber()).isTableHost());
//        assertEquals(NUMBER_OF_DISPLAYABLE_TABLES + 1, TableAdapter.getDisplayableTables().size());
//    }
//
//    @Test
//    public void testMergeTables() {
//        restaurantAdapter.mergeTables(tableNormal, Arrays.asList(tableNormalClosed));
//        Table tableNormalUpdated = getTable(tableNormal.getAdaptee().getNumber()).getAdaptee();
//        Table tableNormalClosedUpdated = getTable(tableNormalClosed.getAdaptee().getNumber()).getAdaptee();
//        assertEquals(1,
//                tableNormalUpdated.getConsumed().stream()
//                        .filter(table -> table.getNumber() == tableNormalClosed.getAdaptee().getNumber())
//                        .collect(toList()).size());
//        assertEquals(tableNormal.getAdaptee().getNumber(), tableNormalClosedUpdated.getConsumer().getNumber());
//    }
//
//    @Test
//    public void testMergerTablesMoveReceiptRecords() {
//        int recordNum = tableNormal.getOpenReceipt().getAdaptee().getRecords().size();
//        restaurantAdapter.mergeTables(tableNormalClosed, Arrays.asList(tableNormal));
//        TableAdapter tableNormalUpdated = getTable(tableNormal.getAdaptee().getNumber());
//        TableAdapter tableNormalClosedUpdated = getTable(tableNormalClosed.getAdaptee().getNumber());
//        assertNull(tableNormalUpdated.getOpenReceipt());
//        assertNotNull(tableNormalClosedUpdated.getOpenReceipt());
//        assertEquals(recordNum, tableNormalClosedUpdated.getOpenReceipt().getAdaptee().getRecords().size());
//    }
//
//    @Test(expected = IllegalTableStateException.class)
//    public void testMergeTablesConsumerTable() {
//        restaurantAdapter.mergeTables(tableNormalClosed, Arrays.asList(tableConsumer));
//    }
//
//    @Test
//    public void testSplitTables() {
//        restaurantAdapter.splitTables(tableConsumer);
//        TableAdapter tableConsumerUpdated = getTable(tableConsumer.getAdaptee().getNumber());
//        TableAdapter tableConsumedUpdated = getTable(tableConsumed.getAdaptee().getNumber());
//        assertNull(tableConsumedUpdated.getAdaptee().getConsumer());
//        assertEquals(0, tableConsumerUpdated.getAdaptee().getConsumed().size());
//    }


}
