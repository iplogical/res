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
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.NUMBER_OF_PRODUCT_CATEGORIES;
import static com.inspirationlogical.receipt.corelib.model.adapter.ProductCategoryAdapter.getProductCategoryByName;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ProductCategoryAdapterTest extends TestBase {

    ReceiptRecordAdapter receiptRecordAdapter;
    ProductCategoryAdapter root, aggregateOne, leafOne, pseudoTwo, pseudoFour;
    Product.ProductBuilder builder;

    @Before
    public void setUp() {
        receiptRecordAdapter = new ReceiptRecordAdapter(schema.getReceiptSaleOneRecordTwo());
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
                .rapidCode(110)
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
        aggregateOne.addChildCategory("leafSeven", ProductCategoryType.LEAF);
        ProductCategory aggregateOneUpdated = (ProductCategory)GuardedTransaction.runNamedQuery(ProductCategory.GET_CATEGORY_BY_NAME,
                query -> query.setParameter("name", aggregateOne.getAdaptee().getName())).get(0);
        assertEquals(childNumber + 1, aggregateOneUpdated.getChildren().size());
        assertEquals(1, aggregateOneUpdated.getChildren().stream()
                .filter(productCategory -> productCategory.getName().equals("leafSeven"))
                .count());

    }

    @Test(expected = IllegalProductCategoryStateException.class)
    public void testAddChildCategoryNameUsed() {
        aggregateOne.addChildCategory("leafOne", ProductCategoryType.LEAF);
    }

    @Test(expected = IllegalProductCategoryStateException.class)
    public void testAddChildCategoryAlreadyHasLeafChild() {
        aggregateOne.addChildCategory("leafSeven", ProductCategoryType.AGGREGATE);
    }

    @Test
    public void testUpdateProductCategory() {
        ProductCategoryAdapter.updateProductCategory("leafTwenty", "leafOne", null);
        assertEquals("leafTwenty", getProductCategoryByName("leafTwenty").getAdaptee().getName());
        assertNull(getProductCategoryByName("leafOne"));
    }

    @Test(expected = IllegalProductCategoryStateException.class)
    public void testUpdateProductCategoryNameUsed() {
        ProductCategoryAdapter.updateProductCategory("leafTwo", "leafOne", null);
    }

    @Test
    public void testDelete() {
        aggregateOne.deleteProductCategory();
        ProductCategory aggregateOneUpdated = getProductCategoryByName(aggregateOne.getAdaptee().getName()).getAdaptee();
        assertEquals(ProductStatus.DELETED, aggregateOneUpdated.getStatus());
    }
}
