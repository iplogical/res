package com.inspirationlogical.receipt.corelib.model.entity;

import static com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule.NUMBER_OF_PRODUCTS;
import static com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule.NUMBER_OF_RECIPES;
import static com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule.NUMBER_OF_STOCKS;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

public class ProductTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testProductCreation() {
        assertEquals(NUMBER_OF_PRODUCTS, getProductList().size());
    }

    @Test(expected = PersistenceException.class)
    public void testProductCategoryConstriant() {
        GuardedTransaction.Run(()->{
            schema.getProductOne().getCategory().setProduct(null);
            schema.getProductOne().setCategory(null);
        });
    }

    @Test(expected = RollbackException.class)
    public void testShortNameTooLong() {
        GuardedTransaction.Run(()->
                schema.getProductOne().setShortName("ExtremelyLongShortNameExceedsItsLimit"));
    }

    @Test(expected = RollbackException.class)
    public void testShortNameEmpty() {
        GuardedTransaction.Run(()->
                schema.getProductOne().setShortName(""));
    }

    @Test(expected = RollbackException.class)
    public void testLongNameEmpty() {
        GuardedTransaction.Run(()->
                schema.getProductOne().setLongName(""));
    }

    @Test(expected = RollbackException.class)
    public void testQualityUnitNull() {
        GuardedTransaction.Run(()->
                schema.getProductOne().setQuantityUnit(null));
    }

    @Test(expected = RollbackException.class)
    public void testEtalonQuantityNull() {
        GuardedTransaction.Run(()->
                schema.getProductOne().setEtalonQuantity(null));
    }

    @Test(expected = RollbackException.class)
    public void testProductTypeNull() {
        GuardedTransaction.Run(()->
                schema.getProductOne().setType(null));
    }

    @Test(expected = RollbackException.class)
    public void testProductStatusNull() {
        GuardedTransaction.Run(()->
                schema.getProductOne().setStatus(null));
    }

    @Test(expected = RollbackException.class)
    public void productWithoutCategory() {
        GuardedTransaction.Run(()->{
            schema.getProductTwo().getCategory().setProduct(null);
            schema.getProductTwo().setCategory(null);
        });
    }

    @Test(expected = RollbackException.class)
    public void productWithLeafCategory() {
        GuardedTransaction.Run(()->{
            schema.getLeafOne().setProduct(schema.getProductTwo());
            schema.getProductTwo().setCategory(schema.getLeafOne());
        });
    }

    @Test
    public void testNumberOfElements() {
        List<Product> products = getProductList().stream().filter(p -> p.getLongName() == "productFour").collect(Collectors.toList());
        assertEquals(1,products.size());
        assertEquals(NUMBER_OF_RECIPES, products.get(0).getRecipe().size());
    }

    @Test
    public void testNumberOfStocks() {
        List<Product> products = getProductList().stream().filter(p -> p.getLongName() == "productFour").collect(Collectors.toList());
        assertEquals(1,products.size());
        assertEquals(NUMBER_OF_STOCKS, products.get(0).getStock().size());
    }

    private List<Product> getProductList() {
        @SuppressWarnings("unchecked")
        List<Product> entries = schema.getEntityManager().createNamedQuery(Product.GET_TEST_PRODUCTS).getResultList();
        return entries;
    }

}
