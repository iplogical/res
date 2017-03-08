package com.inspirationlogical.receipt.testsuite;

import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import com.inspirationlogical.receipt.model.*;

@RunWith(Categories.class)
@IncludeCategory(ModelTest.class)
@SuiteClasses({
    PriceModifierTest.class,
    ProductCategoryTest.class,
    ProductTest.class,
    ReceiptRecordTest.class,
    ReceiptTest.class,
    RecipeTest.class,
    ReservationTest.class,
    RestaurantTest.class,
    StockTest.class,
    TableTest.class,
    VATSerieTest.class,
    VATTest.class})
public class ModelTestSuite {

}
