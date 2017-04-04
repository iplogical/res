package com.inspirationlogical.receipt.corelib.model.adapter;

import static org.junit.Assert.*;

import java.util.List;
import java.util.stream.Collectors;

import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;

public class ProductCategoryAdapterTest {

    ReceiptRecordAdapter receiptRecordAdapter;
    ProductCategoryAdapter pseudoTwo, pseudoFour;

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void setUp() {
        receiptRecordAdapter = new ReceiptRecordAdapter(schema.getReceiptRecordSaleTwo());
        pseudoTwo = new ProductCategoryAdapter(schema.getPseudoTwo());
        pseudoFour = new ProductCategoryAdapter(schema.getPseudoFour());
    }

    @Test
    public void testGetRootCategory() {
        ProductCategoryAdapter rootCategory = ProductCategoryAdapter.getRootCategory(schema.getEntityManager());
        assertEquals(ProductCategoryType.ROOT, rootCategory.getAdaptee().getType());
    }

    @Test
    public void testLeafNumberOfProductsUnderLeafOne() {
        ProductCategoryAdapter leafOne = new ProductCategoryAdapter(schema.getLeafOne());
        List<ProductAdapter> products = leafOne.getAllProducts();
        assertEquals(6, products.size());
    }

    @Test
    public void testProductNamesUnderLeafOne() {
        ProductCategoryAdapter leafOne = new ProductCategoryAdapter(schema.getLeafOne());
        List<ProductAdapter> products = leafOne.getAllProducts();
        List<ProductAdapter> list_product_one =  products.stream().filter((elem) -> (elem.getAdaptee().getLongName().equals("product"))).collect(Collectors.toList());
        List<ProductAdapter> list_product_two =  products.stream().filter((elem) -> (elem.getAdaptee().getLongName().equals("productTwo"))).collect(Collectors.toList());
        assertEquals(1,list_product_one.size());
        assertEquals("product",list_product_one.get(0).getAdaptee().getLongName());
        assertEquals(1,list_product_two.size());
        assertEquals("productTwo",list_product_two.get(0).getAdaptee().getLongName());
    }

    @Test
    public void testGetAdHocProduct() {

    }

    @Test
    public void testGetDiscountNoPriceModifier() {
        assertEquals(0, pseudoFour.getDiscount(receiptRecordAdapter), 0.01);
    }


    @Test
    public void testGetDiscountQuantityBelow() {
        assertEquals(0, pseudoTwo.getDiscount(receiptRecordAdapter), 0.01);
    }

    @Test
    public void testGetDiscountQuantityEnough() {
        receiptRecordAdapter.getAdaptee().setSoldQuantity(3);
        assertEquals(33.333, pseudoTwo.getDiscount(receiptRecordAdapter), 0.01);
    }
}