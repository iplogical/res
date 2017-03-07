package com.inspirationlogical.receipt.model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;

import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.model.enums.TableType;

public class RestaurantTest {


    private EntityManager manager;

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule();

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testRestaurantCreation() {
        assertListSize();
    }

    @Test
    public void numberOfTables() {
        assertEquals(6, persistRestaurantAndGetList().get(0).getTable().size());
    }

    @Test(expected = RollbackException.class)
    public void nameIsNull() {
        schema.getRestaurant().setName(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void companyNameIsNull() {
        schema.getRestaurant().setCompanyName(null);;
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void addressIsNull() {
        schema.getRestaurant().setAddress(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void noPurchaseTable() {
        schema.getTablePurchase().setType(TableType.NORMAL);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void toManyDisposalTables() {
        schema.getTableNormal().setType(TableType.DISPOSAL);
        assertListSize();
    }

    private void assertListSize() {
        assertEquals(1, persistRestaurantAndGetList().size());
    }

    private List<Restaurant> persistRestaurantAndGetList() {
        persistRestaurant();
        @SuppressWarnings("unchecked")
        List<Restaurant> entries = manager.createNamedQuery(Restaurant.GET_TEST_RESTAURANTS).getResultList();
        return entries;
    }

    private void persistRestaurant() {
        manager = factory.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getRestaurant());
        manager.getTransaction().commit();
    }
}