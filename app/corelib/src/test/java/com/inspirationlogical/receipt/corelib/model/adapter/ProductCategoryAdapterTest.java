package com.inspirationlogical.receipt.corelib.model.adapter;

import static com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule.NUMBER_OF_PRODUCTS;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;

public class ProductCategoryAdapterTest {

    ReceiptRecordAdapter receiptRecordAdapter;
    ProductCategoryAdapter pseudoTwo, pseudoFour, root;

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void setUp() {
        receiptRecordAdapter = new ReceiptRecordAdapter(schema.getReceiptRecordSaleTwo());
        pseudoTwo = new ProductCategoryAdapter(schema.getPseudoTwo());
        pseudoFour = new ProductCategoryAdapter(schema.getPseudoFour());
        root = new ProductCategoryAdapter(schema.getRoot());
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
        List<ProductAdapter> list_product_one =  products.stream().filter((elem) -> (elem.getAdaptee().getLongName().equals("product"))).collect(toList());
        List<ProductAdapter> list_product_two =  products.stream().filter((elem) -> (elem.getAdaptee().getLongName().equals("productTwo"))).collect(toList());
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

    @Test
    public void testGetAllActiveProducts() {
        List<ProductAdapter> products = root.getAllActiveProducts();
        assertEquals(NUMBER_OF_PRODUCTS, products.size());
    }

    @Test
    public void testGetAllSellableProducts() {
        List<ProductAdapter> products = root.getAllSellableProducts();
        assertEquals(6, products.size());
        assertEquals(0, products.stream().filter(productAdapter ->
                productAdapter.getAdaptee().getLongName().equals("productAdHoc")).collect(toList()).size());
    }

    @Test
    public void testGetAllStorableProducts() {
        Set<ProductAdapter> products = root.getAllStorableProducts();
        assertEquals(8, products.size());
        assertEquals(0, products.stream().filter(productAdapter ->
                productAdapter.getAdaptee().getLongName().equals("productFour")).collect(toList()).size());
    }
}