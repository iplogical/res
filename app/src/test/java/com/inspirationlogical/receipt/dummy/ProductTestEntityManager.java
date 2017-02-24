package com.inspirationlogical.receipt.dummy;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Rule;
import org.junit.Test;

public class ProductTestEntityManager {

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule();

    @Test
    public void testProductCreation() {
        EntityManager manager = factory.getEntityManager();

        manager.getTransaction().begin();

        Product product = new Product();
        product.setLongName("My first test product");
        product.setShortName("FirstProd");
        product.setSalePrice(1000);

//        manager.persist(product);
        manager.getTransaction().commit();

        @SuppressWarnings("unchecked")
        List<Product> entries = manager.createNamedQuery(Product.GET_TEST_PRODUCTS).getResultList();
        assertEquals(1, entries.size());
        assertEquals("My first test product", entries.get(0).getLongName());
    }
}
