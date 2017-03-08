package com.inspirationlogical.receipt.testsuite;

import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import com.inspirationlogical.receipt.controller.RestaurantControllerTest;

@RunWith(Categories.class)
@IncludeCategory(ControllerTest.class)
@SuiteClasses({
    RestaurantControllerTest.class
})
public class ControllerTestSuite {

}
