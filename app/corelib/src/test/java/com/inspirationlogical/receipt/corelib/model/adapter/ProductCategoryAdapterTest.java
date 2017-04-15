package com.inspirationlogical.receipt.corelib.model.adapter;

import static com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule.NUMBER_OF_PRODUCTS;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import com.inspirationlogical.receipt.corelib.exception.IllegalProductCategoryStateException;
import com.inspirationlogical.receipt.corelib.exception.IllegalProductStateException;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.enums.QuantityUnit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;

public class ProductCategoryAdapterTest {

    ReceiptRecordAdapter receiptRecordAdapter;
    ProductCategoryAdapter root, aggregateOne, leafOne, pseudoTwo, pseudoFour;
    Product.ProductBuilder builder;

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void setUp() {
        receiptRecordAdapter = new ReceiptRecordAdapter(schema.getReceiptRecordSaleTwo());
        root = new ProductCategoryAdapter(schema.getRoot());
        aggregateOne = new ProductCategoryAdapter(schema.getAggregateOne());
        leafOne = new ProductCategoryAdapter(schema.getLeafOne());
        pseudoTwo = new ProductCategoryAdapter(schema.getPseudoTwo());
        pseudoFour = new ProductCategoryAdapter(schema.getPseudoFour());
        builder = Product.builder()
                .longName("newProduct")
                .shortName("newProduct")
                .type(ProductType.SELLABLE)
                .status(ProductStatus.ACTIVE)
                .rapidCode(1010)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(Double.valueOf(100))
                .purchasePrice(440)
                .salePrice(220)
                .minimumStock(14)
                .stockWindow(60);

    }

    @Test
    public void testGetRootCategory() {
        ProductCategoryAdapter rootCategory = ProductCategoryAdapter.getRootCategory();
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
        List<ProductAdapter> products = root.getAllStorableProducts();
        assertEquals(8, products.size());
        assertEquals(0, products.stream().filter(productAdapter ->
                productAdapter.getAdaptee().getLongName().equals("productFour")).collect(toList()).size());
    }

    @Test
    public void testGetAggregateCategories() {
        assertEquals(7, ProductCategoryAdapter.getCategoriesByType(ProductCategoryType.AGGREGATE).size());
    }

    @Test
    public void testGetAllCategories() {
        assertEquals(13, ProductCategoryAdapter.getRootCategory().getAllProductCategories().size());
    }

    @Test
    public void testAddChildCategory() {
        aggregateOne.addChildCategory("newChild", ProductCategoryType.LEAF);
        assertEquals(3, aggregateOne.getChildrenCategories().size());
        assertEquals(1, aggregateOne.getChildrenCategories().stream()
        .filter(productCategoryAdapter -> productCategoryAdapter.getAdaptee().getName().equals("newChild"))
        .collect(toList()).size());
    }

    @Test(expected = IllegalProductCategoryStateException.class)
    public void testAddChildCategoryAlreadyExist() {
        aggregateOne.addChildCategory("leafOne", ProductCategoryType.LEAF);
        assertEquals(3, aggregateOne.getChildrenCategories().size());
    }

    @Test(expected = IllegalProductCategoryStateException.class)
    public void testAddChildCategoryAlreadyHasLeaf() {
        aggregateOne.addChildCategory("newChild", ProductCategoryType.AGGREGATE);
        assertEquals(3, aggregateOne.getChildrenCategories().size());
    }

    @Test
    public void testUpdateChildCategory() {
        aggregateOne.updateChildCategory("newChild", "leafOne", ProductCategoryType.LEAF);
        assertEquals(2, aggregateOne.getChildrenCategories().size());
        assertEquals(1, aggregateOne.getChildrenCategories().stream()
                .filter(productCategoryAdapter -> productCategoryAdapter.getAdaptee().getName().equals("newChild"))
                .collect(toList()).size());
    }

    @Test
    public void testAddProduct() {
        leafOne.addProduct(builder);
        assertEquals(7, leafOne.getAllProducts().size());
        assertEquals(1, leafOne.getAllProducts().stream()
                .filter(productAdapter -> productAdapter.getAdaptee().getLongName().equals("newProduct"))
                .collect(toList()).size());
    }

    @Test(expected = IllegalProductStateException.class)
    public void testAddProductNameAlreadyExists() {
        builder.longName("productSix");
        leafOne.addProduct(builder);
        assertEquals(7, leafOne.getAllProducts().size());
    }

    @Test
    public void testGetCategoryByName() {
        assertEquals(1, ProductCategoryAdapter.getProductCategoryByName("leafOne").size());
    }
}
