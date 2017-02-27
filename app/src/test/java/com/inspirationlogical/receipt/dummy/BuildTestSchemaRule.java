package com.inspirationlogical.receipt.dummy;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class BuildTestSchemaRule implements TestRule {

    private Product product;
    private Product productTwo;
    private Product productThree;
    private Product productFour;
    
    private ProductCategory leafOne;
    private ProductCategory leafTwo;
    private ProductCategory aggregate;

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            
            @Override
            public void evaluate() throws Throwable {
                buildTestSchema();
                base.evaluate();
            }
        };
    }

    public Product getProduct() {
        return product;
    }

    public ProductCategory getCategory() {
        return leafOne;
    }

    private void buildTestSchema() {
        buildObjects();
        setUpObjectRelationShips();
    }

    private void buildObjects() {
        buildProduct();
        buildProductCategory();
    }

    private void setUpObjectRelationShips() {
        leafOne.setProduct(product);
        product.setCategory(leafOne);
    }
 
    private void buildProduct() {
        product = Product.builder()
                .LongName("Jack and Coke")
                .shortName("Jack and Coke")
                .salePrice(1000)
                .quantityUnit(QunatityUnit.LITER)
                .etalonQuantity(EtalonQuantity.LITER)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildProductCategory() {
        aggregate = ProductCategory.builder()
                .name("Beverage")
                .type(ProductCategoryType.AGGREGATE)
                .build();
        leafOne = ProductCategory.builder()
                .name("Whisky")
                .type(ProductCategoryType.LEAF)
                .build();
    }
}
