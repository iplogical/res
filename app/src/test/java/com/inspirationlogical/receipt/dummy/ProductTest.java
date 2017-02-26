package com.inspirationlogical.receipt.dummy;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ProductTest {

    private EntityManager manager;
    private Product product;
    private ProductCategory category;

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule();

    @Before
    public void createObjects() {
        category = new ProductCategory();
        category.setName("Beverage");

        product = new Product();
        product.setLongName("Jack and Coke");
        product.setShortName("Jack and Coke");
        product.setSalePrice(1000);
        product.setQuantityUnit(QunatityUnit.LITER);
        product.setEtalonQuantity(EtalonQuantity.LITER);
        product.setType(ProductType.SELLABLE);
        product.setCategory(category);
        category.setProduct(product);
    }

    @Test
    public void testProductCreation() {
        assertListSize();
    }

    @Test(expected = PersistenceException.class)
    public void testProductCategoryConstriant() {
        product.setCategory(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testShortNameTooLong() {
        product.setShortName("ExtremelyLongShortNameExceedsItsLimit");
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testShortNameEmpty() {
        product.setShortName("");
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testLongNameEmpty() {
        product.setLongName("");
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testQualityUnitNull() {
        product.setQuantityUnit(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testEtalonQuantityNull() {
        product.setEtalonQuantity(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testProductTypeNull() {
        product.setType(null);
        assertListSize();
    }

    private void assertListSize() {
        assertEquals(1, persistProductAndGetList().size());
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
        manager.persist(product);
        manager.getTransaction().commit();
    }
}
