package com.inspirationlogical.receipt.dummy;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;

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
        assertEquals("root", persistCategoryAndGetList().get(0).getName());
    }

    @Test
    public void testProductConstraint() {
        schema.getRoot().setProduct(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testNameNotEmpty() {
        schema.getLeafOne().setName("");
        assertListSize();
    }
    
    @Test(expected = RollbackException.class)
    public void testPseudoCategoryProductNull() {
        schema.getPseudoOne().setProduct(null);
        assertListSize();
    }
    
    @Test(expected = RollbackException.class)
    public void testLeafCategotyProductNotNull() {
        schema.getLeafTwo().setProduct(schema.getProduct());
        assertListSize();
    }
    
    @Test(expected = RollbackException.class)
    public void rootHasParent() {
        schema.getRoot().setParent(schema.getAggregate());
        assertListSize();
    }
 
    @Test(expected = RollbackException.class)
    public void aggregateHasNoParent() {
        schema.getAggregate().setParent(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void aggregateHasLeafAsParent() {
        schema.getAggregate().setParent(schema.getLeafOne());
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void aggregateHasPseudoAsParent() {
        schema.getAggregate().setParent(schema.getPseudoOne());
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void leafHasNoParent() {
        schema.getLeafTwo().setParent(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void leafHasRootAsParent() {
        schema.getLeafTwo().setParent(schema.getRoot());
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void leafHasPseudoAsParent() {
        schema.getLeafTwo().setParent(schema.getPseudoOne());
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void pseudoHasNoParent() {
        schema.getPseudoThree().setParent(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void pseudoHasRootAsParent() {
        schema.getPseudoFour().setParent(schema.getRoot());
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void pseudoHasAggregateAsParent() {
        schema.getPseudoFour().setParent(schema.getAggregate());
        assertListSize();
    }

    private void assertListSize() {
        assertEquals(8, persistCategoryAndGetList().size());
    }

    private List<ProductCategory> persistCategoryAndGetList() {
        persistCategory();
        @SuppressWarnings("unchecked")
        List<ProductCategory> entries = manager.createNamedQuery(ProductCategory.GET_TEST_CATEGORIES).getResultList();
        return entries;
    }

    private void persistCategory() {
        manager = factory.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getRoot());
        manager.getTransaction().commit();
    }
}
