package com.inspirationlogical.receipt.corelib.model.entity;

import static com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule.NUMBER_OF_PRODUCT_CATEGORIES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.RollbackException;

import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

public class ProductCategoryTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

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
        assertEquals(2, categories.get(0).getPriceModifier().size());
    }

    @Test
    public void testProductConstraint() {
        GuardedTransaction.Run(()->
            schema.getRoot().setProduct(null));
    }

    @Test(expected = RollbackException.class)
    public void testNameNotEmpty() {
        GuardedTransaction.Run(()->
            schema.getLeafOne().setName(""));
    }
    
    @Test(expected = RollbackException.class)
    public void testPseudoCategoryWithoutProduct() {
        GuardedTransaction.Run(()->
            schema.getPseudoOne().setProduct(null));
    }
    
    @Test(expected = RollbackException.class)
    public void testLeafCategoryProductNotNull() {
        GuardedTransaction.Run(()->
            schema.getLeafTwo().setProduct(schema.getProductOne()));
    }
    
    @Test(expected = RollbackException.class)
    public void rootHasParent() {
        GuardedTransaction.Run(()->
            schema.getRoot().setParent(schema.getAggregateOne()));
    }
 
    @Test(expected = RollbackException.class)
    public void aggregateHasNoParent() {
        GuardedTransaction.Run(()->
            schema.getAggregateOne().setParent(null));
    }

    @Test(expected = RollbackException.class)
    public void aggregateHasLeafAsParent() {
        GuardedTransaction.Run(()->
            schema.getAggregateOne().setParent(schema.getLeafOne()));
    }

    @Test(expected = RollbackException.class)
    public void aggregateHasPseudoAsParent() {
        GuardedTransaction.Run(()->
            schema.getAggregateOne().setParent(schema.getPseudoOne()));
    }

    @Test(expected = RollbackException.class)
    public void leafHasNoParent() {
        GuardedTransaction.Run(()->
            schema.getLeafTwo().setParent(null));
    }

    @Test(expected = RollbackException.class)
    public void leafHasRootAsParent() {
        GuardedTransaction.Run(()->
            schema.getLeafTwo().setParent(schema.getRoot()));
    }

    @Test(expected = RollbackException.class)
    public void leafHasPseudoAsParent() {
        GuardedTransaction.Run(()->
            schema.getLeafTwo().setParent(schema.getPseudoOne()));
    }

    @Test(expected = RollbackException.class)
    public void pseudoHasNoParent() {
        GuardedTransaction.Run(()->
            schema.getPseudoThree().setParent(null));
    }

    @Test(expected = RollbackException.class)
    public void pseudoHasRootAsParent() {

        GuardedTransaction.Run(()->
            schema.getPseudoFour().setParent(schema.getRoot()));
    }

    @Test(expected = RollbackException.class)
    public void pseudoHasAggregateAsParent() {

        GuardedTransaction.Run(()->
            schema.getPseudoFour().setParent(schema.getAggregateOne()));
    }

    private List<ProductCategory> getAllCategories() {
        @SuppressWarnings("unchecked")
        List<ProductCategory> entries = schema.getEntityManager().createNamedQuery(ProductCategory.GET_ALL_CATEGORIES).getResultList();
        return entries;
    }

}
