package com.inspirationlogical.receipt.corelib.model.entity;

import static com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule.NUMBER_OF_RESTAURANT;
import static com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule.NUMBER_OF_TABLES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.List;
import javax.persistence.RollbackException;

import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

public class RestaurantTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testRestaurantCreation() {
        assertEquals(NUMBER_OF_RESTAURANT, getRestaurants().size());
    }

    @Test
    public void numberOfTables() {
        assertEquals(NUMBER_OF_TABLES, getRestaurants().get(0).getTables().size());
    }

    @Test(expected = RollbackException.class)
    public void restaurantNameIsNull() {
        GuardedTransaction.run(() ->
                schema.getRestaurant().setRestaurantName(null));
    }

    @Test(expected = RollbackException.class)
    public void companyNameIsNull() {
        GuardedTransaction.run(() ->
                schema.getRestaurant().setCompanyName(null));
    }

    @Test(expected = RollbackException.class)
    public void companyTaxPayerIdNull() {
        GuardedTransaction.run(() ->
                schema.getRestaurant().setCompanyTaxPayerId(null));
    }

    @Test(expected = RollbackException.class)
    public void restaurantAddressIsNull() {
        GuardedTransaction.run(() ->
                schema.getRestaurant().setRestaurantAddress(null));
    }

    @Test(expected = RollbackException.class)
    public void companyAddressIsNull() {
        GuardedTransaction.run(() ->
                schema.getRestaurant().setCompanyAddress(null));
    }

    @Test(expected = RollbackException.class)
    public void noPurchaseTable() {
        GuardedTransaction.run(() ->
                schema.getTablePurchase().setType(TableType.NORMAL));
    }

    @Test(expected = RollbackException.class)
    public void toManyDisposalTables() {
        GuardedTransaction.run(() ->
                schema.getTableNormal().setType(TableType.DISPOSAL));
    }


    @Test
    public void equalityAndHashCode() {
        Restaurant restaurant = getRestaurants().get(0);
        assertEquals(restaurant, restaurant.toBuilder().build());
        assertNotEquals(restaurant, restaurant.toBuilder().restaurantName("THIS_STRING_SHOULD_NOT_BE_USED_IN_THE_TEST_SHCEMA_RULE!!!!@#@#@").build());
        assertNotEquals(restaurant, restaurant.toBuilder().companyName("THIS_STRING_SHOULD_NOT_BE_USED_IN_THE_TEST_SHCEMA_RULE!!!!@#@#@").build());
        assertNotEquals(restaurant, restaurant.toBuilder().companyTaxPayerId("THIS_STRING_SHOULD_NOT_BE_USED_IN_THE_TEST_SHCEMA_RULE!!!!@#@#@").build());
        assertNotEquals(restaurant, restaurant.toBuilder().restaurantAddress(restaurant.getRestaurantAddress().toBuilder().city(restaurant.getRestaurantAddress().getCity() + "LOLOLO").build()).build());
        assertNotEquals(restaurant, restaurant.toBuilder().companyAddress(restaurant.getCompanyAddress().toBuilder().city(restaurant.getCompanyAddress().getCity() + "LOLO").build()).build());
        assertNotEquals(restaurant, restaurant.toBuilder().phoneNumber("THIS_STRING_SHOULD_NOT_BE_USED_IN_THE_TEST_SHCEMA_RULE!!!!@#@#@").build());
        assertNotEquals(restaurant, restaurant.toBuilder().webSite("THIS_STRING_SHOULD_NOT_BE_USED_IN_THE_TEST_SHCEMA_RULE!!!!@#@#@").build());
        assertNotEquals(restaurant, restaurant.toBuilder().socialMediaInfo("THIS_STRING_SHOULD_NOT_BE_USED_IN_THE_TEST_SHCEMA_RULE!!!!@#@#@").build());
        assertNotEquals(restaurant, restaurant.toBuilder().receiptNote("THIS_STRING_SHOULD_NOT_BE_USED_IN_THE_TEST_SHCEMA_RULE!!!!@#@#@").build());
        assertNotEquals(restaurant, restaurant.toBuilder().receiptDisclaimer("THIS_STRING_SHOULD_NOT_BE_USED_IN_THE_TEST_SHCEMA_RULE!!!!@#@#@").build());

    }

    private List<Restaurant> getRestaurants() {
        @SuppressWarnings("unchecked")
        List<Restaurant> entries = schema.getEntityManager().createNamedQuery(Restaurant.GET_TEST_RESTAURANTS).getResultList();
        return entries;
    }

}
