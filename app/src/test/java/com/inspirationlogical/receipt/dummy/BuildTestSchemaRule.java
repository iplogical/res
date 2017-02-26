package com.inspirationlogical.receipt.dummy;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class BuildTestSchemaRule implements TestRule {

    private Product product;
    private ProductCategory category;

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
        return category;
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
        category.setProduct(product);
        product.setCategory(category);
    }
 
    private void buildProduct() {
        product = new Product();
        product.setLongName("Jack and Coke");
        product.setShortName("Jack and Coke");
        product.setSalePrice(1000);
        product.setQuantityUnit(QunatityUnit.LITER);
        product.setEtalonQuantity(EtalonQuantity.LITER);
        product.setType(ProductType.SELLABLE);
    }

    private void buildProductCategory() {
        category = new ProductCategory();
        category.setName("Beverage");
    }
}
