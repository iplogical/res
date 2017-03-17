package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class ProductTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testProductCreation() {
        assertListSize();
    }

    @Test(expected = PersistenceException.class)
    public void testProductCategoryConstriant() {
        GuardedTransaction.Run(schema.getEntityManager(),()->{
            schema.getProductOne().getCategory().setProduct(null);
            schema.getProductOne().setCategory(null);
        });
    }

    @Test(expected = RollbackException.class)
    public void testShortNameTooLong() {
        GuardedTransaction.Run(schema.getEntityManager(),()->
                schema.getProductOne().setShortName("ExtremelyLongShortNameExceedsItsLimit"));
    }

    @Test(expected = RollbackException.class)
    public void testShortNameEmpty() {
        GuardedTransaction.Run(schema.getEntityManager(),()->
                schema.getProductOne().setShortName(""));
    }

    @Test(expected = RollbackException.class)
    public void testLongNameEmpty() {
        GuardedTransaction.Run(schema.getEntityManager(),()->
                schema.getProductOne().setLongName(""));
    }

    @Test(expected = RollbackException.class)
    public void testQualityUnitNull() {
        GuardedTransaction.Run(schema.getEntityManager(),()->
                schema.getProductOne().setQuantityUnit(null));
    }

    @Test(expected = RollbackException.class)
    public void testEtalonQuantityNull() {
        GuardedTransaction.Run(schema.getEntityManager(),()->
                schema.getProductOne().setEtalonQuantity(null));
    }

    @Test(expected = RollbackException.class)
    public void testProductTypeNull() {
        GuardedTransaction.Run(schema.getEntityManager(),()->
                schema.getProductOne().setType(null));
    }

    @Test(expected = RollbackException.class)
    public void testProductStatusNull() {
        GuardedTransaction.Run(schema.getEntityManager(),()->
                schema.getProductOne().setStatus(null));
    }

    @Test(expected = RollbackException.class)
    public void productWithoutCategory() {
        GuardedTransaction.Run(schema.getEntityManager(),()->{
            schema.getProductTwo().getCategory().setProduct(null);
            schema.getProductTwo().setCategory(null);
        });
    }

    @Test(expected = RollbackException.class)
    public void productWithLeafCategory() {
        GuardedTransaction.Run(schema.getEntityManager(),()->{
            schema.getLeafOne().setProduct(schema.getProductTwo());
            schema.getProductTwo().setCategory(schema.getLeafOne());
        });
    }

    @Test
    public void testNumberOfElements() {
        List<Product> products = getProductList().stream().filter(p -> p.getLongName() == "productFour").collect(Collectors.toList());
        assertEquals(1,products.size());
        assertEquals(3, products.get(0).getRecipe().size());
    }

    @Test
    public void testNumberOfStocks() {
        List<Product> products = getProductList().stream().filter(p -> p.getLongName() == "productFour").collect(Collectors.toList());
        assertEquals(1,products.size());
        assertEquals(3, products.get(0).getStock().size());
    }

    private void assertListSize() {
        assertEquals(4, getProductList().size());
    }

    private List<Product> getProductList() {
        @SuppressWarnings("unchecked")
        List<Product> entries = schema.getEntityManager().createNamedQuery(Product.GET_TEST_PRODUCTS).getResultList();
        return entries;
    }

}
