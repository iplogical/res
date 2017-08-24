package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.exception.IllegalProductCategoryStateException;
import com.inspirationlogical.receipt.corelib.exception.IllegalProductStateException;
import com.inspirationlogical.receipt.corelib.model.TestBase;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.enums.QuantityUnit;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.params.ProductCategoryParams;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.NUMBER_OF_PRODUCT_CATEGORIES;
import static com.inspirationlogical.receipt.corelib.model.adapter.ProductCategoryAdapter.getProductCategoryByName;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ProductCategoryAdapterTest extends TestBase {

    private ReceiptRecordAdapter receiptRecordAdapter;
    private ProductCategoryAdapter aggregateOne, leafOne, pseudoTwo, pseudoFour;
    private Product.ProductBuilder builder;
    private ProductCategoryParams productCategoryParams;

    @Before
    public void setUp() {
        receiptRecordAdapter = new ReceiptRecordAdapter(schema.getReceiptSaleOneRecordTwo());
        aggregateOne = new ProductCategoryAdapter(schema.getAggregateOne());
        leafOne = new ProductCategoryAdapter(schema.getLeafOne());
        pseudoTwo = new ProductCategoryAdapter(schema.getPseudoTwo());
        pseudoFour = new ProductCategoryAdapter(schema.getPseudoFour());
        builder = Product.builder()
                .longName("newProduct")
                .shortName("newProduct")
                .type(ProductType.SELLABLE)
                .status(ProductStatus.ACTIVE)
                .rapidCode(110)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .purchasePrice(440)
                .salePrice(220)
                .minimumStock(14)
                .stockWindow(60);
        productCategoryParams = ProductCategoryParams.builder()
                .name("leafSeven")
                .type(ProductCategoryType.LEAF)
                .orderNumber(0)
                .build();

    }

    @Test
    public void testGetRootCategory() {
        ProductCategoryAdapter rootCategory = ProductCategoryAdapter.getRootCategory();
        assertEquals(ProductCategoryType.ROOT, rootCategory.getAdaptee().getType());
    }

    @Test
    public void testGetProductCategories() {
        List<ProductCategoryAdapter> categoryAdapters = ProductCategoryAdapter.getProductCategories();
        assertEquals(NUMBER_OF_PRODUCT_CATEGORIES, categoryAdapters.size());
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
    public void testGetAggregateCategories() {
        assertEquals(7, ProductCategoryAdapter.getCategoriesByType(ProductCategoryType.AGGREGATE).size());
    }

    @Test
    public void testAddProduct() {
        leafOne.addProduct(builder);
        assertEquals(6, leafOne.getAdaptee().getChildren().size());
        assertEquals(1, leafOne.getAdaptee().getChildren().stream()
                .filter(productCategory -> productCategory.getProduct().getLongName().equals("newProduct"))
                .collect(toList()).size());
    }

    @Test(expected = IllegalProductStateException.class)
    public void testAddProductNameAlreadyExists() {
        builder.longName("productSix");
        leafOne.addProduct(builder);
    }

    @Test
    public void testGetCategoryByName() {
        assertEquals("leafOne", getProductCategoryByName("leafOne").getAdaptee().getName());
    }

    @Test
    public void testAddChildCategory() {
        int childNumber = aggregateOne.getAdaptee().getChildren().size();
        aggregateOne.addChildCategory(productCategoryParams);
        ProductCategory aggregateOneUpdated = (ProductCategory)GuardedTransaction.runNamedQuery(ProductCategory.GET_CATEGORY_BY_NAME,
                query -> query.setParameter("name", aggregateOne.getAdaptee().getName())).get(0);
        assertEquals(childNumber + 1, aggregateOneUpdated.getChildren().size());
        assertEquals(1, aggregateOneUpdated.getChildren().stream()
                .filter(productCategory -> productCategory.getName().equals("leafSeven"))
                .count());
    }

    @Test(expected = IllegalProductCategoryStateException.class)
    public void testAddChildCategoryNameUsed() {
        productCategoryParams.setName("leafOne");
        aggregateOne.addChildCategory(productCategoryParams);
    }

    @Test(expected = IllegalProductCategoryStateException.class)
    public void testAddChildCategoryAlreadyHasLeafChild() {
        productCategoryParams.setType(ProductCategoryType.AGGREGATE);
        aggregateOne.addChildCategory(productCategoryParams);
    }

    @Test
    public void testUpdateProductCategory() {
        productCategoryParams.setName("leafTwenty");
        productCategoryParams.setOriginalName("leafOne");
        ProductCategoryAdapter.updateProductCategory(productCategoryParams);
        assertEquals("leafTwenty", getProductCategoryByName("leafTwenty").getAdaptee().getName());
        assertNull(getProductCategoryByName("leafOne"));
    }

    @Test(expected = IllegalProductCategoryStateException.class)
    public void testUpdateProductCategoryNameUsed() {
        productCategoryParams.setName("leafTwo");
        productCategoryParams.setOriginalName("leafOne");
        ProductCategoryAdapter.updateProductCategory(productCategoryParams);
    }

    @Test
    public void testDelete() {
        aggregateOne.deleteProductCategory();
        ProductCategory aggregateOneUpdated = getProductCategoryByName(aggregateOne.getAdaptee().getName()).getAdaptee();
        assertEquals(ProductStatus.DELETED, aggregateOneUpdated.getStatus());
    }
}
