package com.inspirationlogical.receipt.model;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.model.Product;

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
        schema.getProduct().setCategory(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testShortNameTooLong() {
        schema.getProduct().setShortName("ExtremelyLongShortNameExceedsItsLimit");
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testShortNameEmpty() {
        schema.getProduct().setShortName("");
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testLongNameEmpty() {
        schema.getProduct().setLongName("");
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testQualityUnitNull() {
        schema.getProduct().setQuantityUnit(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testEtalonQuantityNull() {
        schema.getProduct().setEtalonQuantity(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testProductTypeNull() {
        schema.getProduct().setType(null);
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
        manager.persist(schema.getProduct());
        manager.getTransaction().commit();
    }
}
