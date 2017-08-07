package com.inspirationlogical.receipt.corelib.model.entity;

import static com.inspirationlogical.receipt.corelib.model.BuildTestSchema.NUMBER_OF_PRODUCT_CATEGORIES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.RollbackException;

import com.inspirationlogical.receipt.corelib.model.AbstractTest;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

public class ProductCategoryTest extends AbstractTest {

    @Test
    public void testProductCategoryCreation() {
        assertEquals(NUMBER_OF_PRODUCT_CATEGORIES, getAllCategories().size());
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
        assertEquals(2, categories.get(0).getPriceModifiers().size());
    }

    @Test
    public void testProductConstraint() {
        GuardedTransaction.run(()->
            schema.getRoot().setProduct(null));
    }

    @Test(expected = RollbackException.class)
    public void testNameNotEmpty() {
        GuardedTransaction.run(()->
            schema.getLeafOne().setName(""));
    }
    
    @Test(expected = RollbackException.class)
    public void testPseudoCategoryWithoutProduct() {
        GuardedTransaction.run(()->
            schema.getPseudoOne().setProduct(null));
    }
    
    @Test(expected = RollbackException.class)
    public void testLeafCategoryProductNotNull() {
        GuardedTransaction.run(()->
            schema.getLeafTwo().setProduct(schema.getProductOne()));
    }
    
    @Test(expected = RollbackException.class)
    public void rootHasParent() {
        GuardedTransaction.run(()->
            schema.getRoot().setParent(schema.getAggregateOne()));
    }
 
    @Test(expected = RollbackException.class)
    public void aggregateHasNoParent() {
        GuardedTransaction.run(()->
            schema.getAggregateOne().setParent(null));
    }

    @Test(expected = RollbackException.class)
    public void aggregateHasLeafAsParent() {
        GuardedTransaction.run(()->
            schema.getAggregateOne().setParent(schema.getLeafOne()));
    }

    @Test(expected = RollbackException.class)
    public void aggregateHasPseudoAsParent() {
        GuardedTransaction.run(()->
            schema.getAggregateOne().setParent(schema.getPseudoOne()));
    }

    @Test(expected = RollbackException.class)
    public void leafHasNoParent() {
        GuardedTransaction.run(()->
            schema.getLeafTwo().setParent(null));
    }

    @Test(expected = RollbackException.class)
    public void leafHasRootAsParent() {
        GuardedTransaction.run(()->
            schema.getLeafTwo().setParent(schema.getRoot()));
    }

    @Test(expected = RollbackException.class)
    public void leafHasPseudoAsParent() {
        GuardedTransaction.run(()->
            schema.getLeafTwo().setParent(schema.getPseudoOne()));
    }

    @Test(expected = RollbackException.class)
    public void pseudoHasNoParent() {
        GuardedTransaction.run(()->
            schema.getPseudoThree().setParent(null));
    }

    @Test(expected = RollbackException.class)
    public void pseudoHasRootAsParent() {

        GuardedTransaction.run(()->
            schema.getPseudoFour().setParent(schema.getRoot()));
    }

    @Test(expected = RollbackException.class)
    public void pseudoHasAggregateAsParent() {

        GuardedTransaction.run(()->
            schema.getPseudoFour().setParent(schema.getAggregateOne()));
    }

    private List<ProductCategory> getAllCategories() {
        @SuppressWarnings("unchecked")
        List<ProductCategory> entries = schema.getEntityManager().createNamedQuery(ProductCategory.GET_ALL_CATEGORIES).getResultList();
        return entries;
    }

}
