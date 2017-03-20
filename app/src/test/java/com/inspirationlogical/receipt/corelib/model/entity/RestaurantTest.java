package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.RollbackException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RestaurantTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testRestaurantCreation() {
        assertEquals(1, getRestaurants().size());
    }

    @Test
    public void numberOfTables() {
        assertEquals(8, getRestaurants().get(0).getTable().size());
    }

    @Test(expected = RollbackException.class)
    public void restaurantNameIsNull() {
        GuardedTransaction.Run(()->
                schema.getRestaurant().setRestaurantName(null));
    }

    @Test(expected = RollbackException.class)
    public void companyNameIsNull() {
        GuardedTransaction.Run(()->
                schema.getRestaurant().setCompanyName(null));
    }

    @Test(expected = RollbackException.class)
    public void companyTaxPayerIdNull() {
        GuardedTransaction.Run(()->
                schema.getRestaurant().setCompanyTaxPayerId(null));
    }

    @Test(expected = RollbackException.class)
    public void restaurantAddressIsNull() {
        GuardedTransaction.Run(()->
                schema.getRestaurant().setRestaurantAddress(null));
    }

    @Test(expected = RollbackException.class)
    public void companyAddressIsNull() {
        GuardedTransaction.Run(()->
                schema.getRestaurant().setCompanyAddress(null));
    }

    @Test(expected = RollbackException.class)
    public void noPurchaseTable() {
        GuardedTransaction.Run(()->
            schema.getTablePurchase().setType(TableType.NORMAL));
    }

    @Test(expected = RollbackException.class)
    public void toManyDisposalTables() {
        GuardedTransaction.Run(()->
                schema.getTableNormal().setType(TableType.DISPOSAL));
    }

    private List<Restaurant> getRestaurants() {
        @SuppressWarnings("unchecked")
        List<Restaurant> entries = schema.getEntityManager().createNamedQuery(Restaurant.GET_TEST_RESTAURANTS).getResultList();
        return entries;
    }

}
