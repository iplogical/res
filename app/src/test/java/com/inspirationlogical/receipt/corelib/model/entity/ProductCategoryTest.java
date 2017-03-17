package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.RollbackException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ProductCategoryTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testProductCategoryCreation() {
        assertEquals(8, getAllCategories().size());
    }

    @Test
    public void testProductCategoryName() {
        assertNotEquals("",getAllCategories().get(0).getName());
    }

    @Test
    public void testPriceModifierNumber() {
        List<ProductCategory> categories = getAllCategories()
                .stream().filter(cat -> cat.getName() == "pseudoOne").collect(Collectors.toList());
        assertEquals(1,categories.size());
        assertEquals(2, categories.get(0).getPriceModifier().size());
    }

    @Test
    public void testProductConstraint() {
        GuardedTransaction.Run(schema.getEntityManager(),()->
            schema.getRoot().setProduct(null));
    }

    @Test(expected = RollbackException.class)
    public void testNameNotEmpty() {
        GuardedTransaction.Run(schema.getEntityManager(),()->
            schema.getLeafOne().setName(""));
    }
    
    @Test(expected = RollbackException.class)
    public void testPseudoCategoryWithoutProduct() {
        GuardedTransaction.Run(schema.getEntityManager(),()->
            schema.getPseudoOne().setProduct(null));
    }
    
    @Test(expected = RollbackException.class)
    public void testLeafCategotyProductNotNull() {
        GuardedTransaction.Run(schema.getEntityManager(),()->
            schema.getLeafTwo().setProduct(schema.getProductOne()));
    }
    
    @Test(expected = RollbackException.class)
    public void rootHasParent() {
        GuardedTransaction.Run(schema.getEntityManager(),()->
            schema.getRoot().setParent(schema.getAggregate()));
    }
 
    @Test(expected = RollbackException.class)
    public void aggregateHasNoParent() {
        GuardedTransaction.Run(schema.getEntityManager(),()->
            schema.getAggregate().setParent(null));
    }

    @Test(expected = RollbackException.class)
    public void aggregateHasLeafAsParent() {
        GuardedTransaction.Run(schema.getEntityManager(),()->
            schema.getAggregate().setParent(schema.getLeafOne()));
    }

    @Test(expected = RollbackException.class)
    public void aggregateHasPseudoAsParent() {
        GuardedTransaction.Run(schema.getEntityManager(),()->
            schema.getAggregate().setParent(schema.getPseudoOne()));
    }

    @Test(expected = RollbackException.class)
    public void leafHasNoParent() {
        GuardedTransaction.Run(schema.getEntityManager(),()->
            schema.getLeafTwo().setParent(null));
    }

    @Test(expected = RollbackException.class)
    public void leafHasRootAsParent() {
        GuardedTransaction.Run(schema.getEntityManager(),()->
            schema.getLeafTwo().setParent(schema.getRoot()));
    }

    @Test(expected = RollbackException.class)
    public void leafHasPseudoAsParent() {
        GuardedTransaction.Run(schema.getEntityManager(),()->
            schema.getLeafTwo().setParent(schema.getPseudoOne()));
    }

    @Test(expected = RollbackException.class)
    public void pseudoHasNoParent() {
        GuardedTransaction.Run(schema.getEntityManager(),()->
            schema.getPseudoThree().setParent(null));
    }

    @Test(expected = RollbackException.class)
    public void pseudoHasRootAsParent() {

        GuardedTransaction.Run(schema.getEntityManager(),()->
            schema.getPseudoFour().setParent(schema.getRoot()));
    }

    @Test(expected = RollbackException.class)
    public void pseudoHasAggregateAsParent() {

        GuardedTransaction.Run(schema.getEntityManager(),()->
            schema.getPseudoFour().setParent(schema.getAggregate()));
    }

    private List<ProductCategory> getAllCategories() {
        @SuppressWarnings("unchecked")
        List<ProductCategory> entries = schema.getEntityManager().createNamedQuery(ProductCategory.GET_ALL_CATEGORIES).getResultList();
        return entries;
    }

}
