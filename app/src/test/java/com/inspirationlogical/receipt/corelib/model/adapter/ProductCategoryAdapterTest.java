package com.inspirationlogical.receipt.corelib.model.adapter;

import static org.junit.Assert.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;

public class ProductCategoryAdapterTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testLeafNumberOfProductsUnderLeafOne() {
        ProductCategoryAdapter leafOne = new ProductCategoryAdapter(schema.getLeafOne(), schema.getEntityManager());
        List<ProductAdapter> products = leafOne.getAllProducts();
        assertEquals(2, products.size());
    }

    @Test
    public void testPrductNamesUnderLeafOne() {
        ProductCategoryAdapter leafOne = new ProductCategoryAdapter(schema.getLeafOne(), schema.getEntityManager());
        List<ProductAdapter> products = leafOne.getAllProducts();
        List<ProductAdapter> list_product_one =  products.stream().filter((elem) -> (elem.getAdaptee().getLongName().equals("product"))).collect(Collectors.toList());
        List<ProductAdapter> list_product_two =  products.stream().filter((elem) -> (elem.getAdaptee().getLongName().equals("productTwo"))).collect(Collectors.toList());
        assertEquals(1,list_product_one.size());
        assertEquals("product",list_product_one.get(0).getAdaptee().getLongName());
        assertEquals(1,list_product_two.size());
        assertEquals("productTwo",list_product_two.get(0).getAdaptee().getLongName());
    }
}