package com.inspirationlogical.receipt.dummy;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Rule;
import org.junit.Test;

public class ProductCategoryTest {

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule();

    @Test
    public void testProductCategoryCreation() {

        Product product = new Product();
        product.setLongName("Jack and Coke");
        product.setShortName("Jack and Coke");
        product.setSalePrice(1000);

        EntityManager manager = factory.getEntityManager();

        manager.getTransaction().begin();

        ProductCategory category = new ProductCategory();

        category.setName("Beverage");
        category.setProduct(product);
        manager.persist(category);
        manager.getTransaction().commit();

        @SuppressWarnings("unchecked")
        List<ProductCategory> entries = manager.createNamedQuery(ProductCategory.GET_TEST_CATEGORIES).getResultList();
        assertEquals(1, entries.size());
       assertEquals("Jack and Coke", entries.get(0).getProduct().getLongName());

    }
}
