package com.inspirationlogical.receipt.corelib.model.entity;

import static com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule.NUMBER_OF_PRODUCTS;
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
        GuardedTransaction.run(()->{
            schema.getProductOne().getCategory().setProduct(null);
            schema.getProductOne().setCategory(null);
        });
    }

    @Test(expected = RollbackException.class)
    public void testShortNameTooLong() {
        GuardedTransaction.run(()->
                schema.getProductOne().setShortName("ExtremelyLongShortNameExceedsItsLimit"));
    }

    @Test(expected = RollbackException.class)
    public void testShortNameEmpty() {
        GuardedTransaction.run(()->
                schema.getProductOne().setShortName(""));
    }

    @Test(expected = RollbackException.class)
    public void testLongNameEmpty() {
        GuardedTransaction.run(()->
                schema.getProductOne().setLongName(""));
    }

    @Test(expected = RollbackException.class)
    public void testQualityUnitNull() {
        GuardedTransaction.run(()->
                schema.getProductOne().setQuantityUnit(null));
    }

    @Test(expected = RollbackException.class)
    public void testProductTypeNull() {
        GuardedTransaction.run(()->
                schema.getProductOne().setType(null));
    }

    @Test(expected = RollbackException.class)
    public void testProductStatusNull() {
        GuardedTransaction.run(()->
                schema.getProductOne().setStatus(null));
    }

    @Test(expected = RollbackException.class)
    public void productWithoutCategory() {
        GuardedTransaction.run(()->{
            schema.getProductTwo().getCategory().setProduct(null);
            schema.getProductTwo().setCategory(null);
        });
    }

    @Test(expected = RollbackException.class)
    public void productWithLeafCategory() {
        GuardedTransaction.run(()->{
            schema.getLeafOne().setProduct(schema.getProductTwo());
            schema.getProductTwo().setCategory(schema.getLeafOne());
        });
    }

    @Test
    public void testNumberOfElements() {
        List<Product> products = getProductList().stream().filter(p -> p.getLongName().equals("productFour"))
                .collect(Collectors.toList());
        assertEquals(1,products.size());
        assertEquals(3, products.get(0).getRecipes().size());
    }

    @Test
    public void testNumberOfStocks() {
        List<Product> products = getProductList().stream().filter(p -> p.getLongName().equals("productRecipeElementOne"))
                .collect(Collectors.toList());
        assertEquals(1,products.size());
        assertEquals(NUMBER_OF_STOCKS, products.get(0).getStocks().size());
    }

    private List<Product> getProductList() {
        @SuppressWarnings("unchecked")
        List<Product> entries = schema.getEntityManager().createNamedQuery(Product.GET_TEST_PRODUCTS).getResultList();
        return entries;
    }

}
