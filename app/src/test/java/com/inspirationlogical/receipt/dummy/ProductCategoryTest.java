package com.inspirationlogical.receipt.dummy;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Rule;
import org.junit.Test;

public class ProductCategoryTest {

    private EntityManager manager;

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule();

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testProductCategoryCreation() {
        assertListSize();
    }

    @Test
    public void testProductCategoryName() {
        assertEquals("leafOne", persistCategoryAndGetList().get(0).getName());
    }

    @Test
    public void testProductConstraint() {
        schema.getCategory().setProduct(null);
        assertListSize();
    }

    private void assertListSize() {
        assertEquals(4, persistCategoryAndGetList().size());
    }

    private List<ProductCategory> persistCategoryAndGetList() {
        persistCategory();
        @SuppressWarnings("unchecked")
        List<ProductCategory> entries = manager.createNamedQuery(ProductCategory.GET_TEST_CATEGORIES).getResultList();
        for(ProductCategory p : entries) {
            System.out.println(p.getName());
        }
        return entries;
    }

    private void persistCategory() {
        manager = factory.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getCategory());
        manager.getTransaction().commit();
    }
}
