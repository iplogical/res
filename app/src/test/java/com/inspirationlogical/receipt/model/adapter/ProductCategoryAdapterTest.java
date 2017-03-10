package com.inspirationlogical.receipt.model.adapter;

import static org.junit.Assert.*;

import java.util.List;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.model.EntityManagerFactoryRule;

public class ProductCategoryAdapterTest {


    private EntityManager manager;

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule();

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void persistObjects() {
        manager = factory.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getRoot());
        manager.getTransaction().commit();
    }

    @Test
    public void testLeafNumberOfProductsUnderLeafOne() {
        ProductCategoryAdapter leafOne = new ProductCategoryAdapterImpl(schema.getLeafOne(), manager);
        List<ProductAdapter> products = leafOne.getAllProducts();
        assertEquals(2, products.size());
    }

    @Test
    public void testPrductNamesUnderLeafOne() {
        ProductCategoryAdapter leafOne = new ProductCategoryAdapterImpl(schema.getLeafOne(), manager);
        List<ProductAdapter> products = leafOne.getAllProducts();
        Stream<ProductAdapter> stream_product =  products.stream().filter((elem) -> (elem.getAdaptee().getLongName().equals("product")));
        Stream<ProductAdapter> stream_product_two =  products.stream().filter((elem) -> (elem.getAdaptee().getLongName().equals("productTwo")));
        assertEquals(1,stream_product.count());
        assertEquals(1,stream_product_two.count());
    }
}