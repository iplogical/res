package com.inspirationlogical.receipt.dummy;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

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
        product.setCategory(category);
        category.setProduct(product);
    }

    @Test
    public void testProductCreation() {
        assertEquals(1, persistProductAndGetList().size());
    }

    @Test(expected = PersistenceException.class)
    public void testProductCategoryConstriant() {
        product.setCategory(null);
        assertEquals(1, persistProductAndGetList().size());
    }

    private void persistProduct() {
        manager = factory.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(product);
        manager.getTransaction().commit();
    }

    private List<Product> persistProductAndGetList() {
        persistProduct();
        @SuppressWarnings("unchecked")
        List<Product> entries = manager.createNamedQuery(Product.GET_TEST_PRODUCTS).getResultList();
        return entries;
    }
}
