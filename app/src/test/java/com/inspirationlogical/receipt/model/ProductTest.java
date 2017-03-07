package com.inspirationlogical.receipt.model;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import org.junit.Rule;
import org.junit.Test;

public class ProductTest {

    private EntityManager manager;

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule();

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testProductCreation() {
        assertListSize();
    }

    @Test(expected = PersistenceException.class)
    public void testProductCategoryConstriant() {
        schema.getProductOne().setCategory(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testShortNameTooLong() {
        schema.getProductOne().setShortName("ExtremelyLongShortNameExceedsItsLimit");
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testShortNameEmpty() {
        schema.getProductOne().setShortName("");
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testLongNameEmpty() {
        schema.getProductOne().setLongName("");
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testQualityUnitNull() {
        schema.getProductOne().setQuantityUnit(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testEtalonQuantityNull() {
        schema.getProductOne().setEtalonQuantity(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testProductTypeNull() {
        schema.getProductOne().setType(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testProductStatusNull() {
        schema.getProductOne().setStatus(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void productWithoutCategory() {
        schema.getProductTwo().setCategory(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void productWithLeafCategory() {
        schema.getProductTwo().setCategory(schema.getLeafOne());
        assertListSize();
    }

    @Test
    public void testNumberOfElements() {
        for(Product p : persistProductAndGetList()) {
            if(p.getLongName() == "productFour") {
                assertEquals(3, p.getRecipe().size());
            }
        }
    }

    @Test
    public void testNumberOfStocks() {
        for(Product p : persistProductAndGetList()) {
            if(p.getLongName() == "productFour") {
                assertEquals(3, p.getStock().size());
            }
        }
    }

    private void assertListSize() {
        assertEquals(4, persistProductAndGetList().size());
    }

    private List<Product> persistProductAndGetList() {
        persistProduct();
        @SuppressWarnings("unchecked")
        List<Product> entries = manager.createNamedQuery(Product.GET_TEST_PRODUCTS).getResultList();
        return entries;
    }

    private void persistProduct() {
        manager = factory.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getProductOne());
        manager.getTransaction().commit();
    }
}
