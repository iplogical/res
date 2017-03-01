package com.inspirationlogical.receipt.model;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.inspirationlogical.receipt.model.EtalonQuantity;
import com.inspirationlogical.receipt.model.Product;
import com.inspirationlogical.receipt.model.ProductCategory;
import com.inspirationlogical.receipt.model.ProductCategoryType;
import com.inspirationlogical.receipt.model.ProductType;
import com.inspirationlogical.receipt.model.QunatityUnit;

import lombok.Getter;

public class BuildTestSchemaRule implements TestRule {

    private @Getter Product productOne;
    private @Getter Product productTwo;
    private @Getter Product productThree;
    private @Getter Product productFour;
    
    private @Getter ProductCategory root;
    private @Getter ProductCategory aggregate;
    private @Getter ProductCategory leafOne;
    private @Getter ProductCategory leafTwo;
    private @Getter ProductCategory pseudoOne;
    private @Getter ProductCategory pseudoTwo;
    private @Getter ProductCategory pseudoThree;
    private @Getter ProductCategory pseudoFour;

    private @Getter Recipe elementOne;
    private @Getter Recipe elementTwo;
    private @Getter Recipe elementThree;

    private @Getter Stock stockOne;
    private @Getter Stock stockTwo;
    private @Getter Stock stockThree;

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

    private void buildTestSchema() {
        buildObjects();
        setUpObjectRelationShips();
    }

    private void buildObjects() {
        buildProducts();
        buildProductCategories();
        buildRecipes();
        buildStocks();
    }

    private void setUpObjectRelationShips() {
        rootAndAggregates();
        aggregatesAndLeafs();
        leafsAndPseudos();
        pseudosAndProducts();
        productFourAndRecipes();
        recipesAndProducts();
        ProductFourAndStocks();
}

    private void buildProducts() {
        buildProduct();
        buildProductTwo();
        buildProductThree();
        buildProductFour();
    }

    private void buildProductCategories() {
        buildRoot();
        buildAggregate();
        buildLeafOne();
        buildLeafTwo();
        buildPseudoOne();
        buildPseudoTwo();
        buildPseudoThree();
        buildPseudoFour();
    }

    private void buildRecipes() {
        buildElementOne();
        buildElementTwo();
        buildElementThree();
    }

    private void buildStocks() {
        buildStockOne();
        buildStockTwo();
        BuildStockThree();
    }

    private void buildProduct() {
        productOne = Product.builder()
                .LongName("product")
                .shortName("product")
                .salePrice(1000)
                .quantityUnit(QunatityUnit.LITER)
                .etalonQuantity(EtalonQuantity.LITER)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildProductTwo() {
        productTwo = Product.builder()
                .LongName("productTwo")
                .shortName("productTwo")
                .salePrice(200)
                .quantityUnit(QunatityUnit.BOTTLE)
                .etalonQuantity(EtalonQuantity.LITER)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildProductThree() {
        productThree = Product.builder()
                .LongName("productThree")
                .shortName("productThree")
                .salePrice(790)
                .quantityUnit(QunatityUnit.PIECE)
                .etalonQuantity(EtalonQuantity.KILOGRAM)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildProductFour() {
        productFour = Product.builder()
                .LongName("productFour")
                .shortName("productFour")
                .salePrice(990)
                .quantityUnit(QunatityUnit.PIECE)
                .etalonQuantity(EtalonQuantity.KILOGRAM)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildRoot() {
        root = ProductCategory.builder()
                .name("root")
                .type(ProductCategoryType.ROOT)
                .build();
    }

    private void buildAggregate() {
        aggregate = ProductCategory.builder()
                .name("aggregate")
                .type(ProductCategoryType.AGGREGATE)
                .build();
    }

    private void buildLeafOne() {
        leafOne = ProductCategory.builder()
                .name("leafOne")
                .type(ProductCategoryType.LEAF)
                .build();
    }

    private void buildLeafTwo() {
        leafTwo = ProductCategory.builder()
                .name("leafTwo")
                .type(ProductCategoryType.LEAF)
                .build();
    }

    private void buildPseudoOne() {
        pseudoOne = ProductCategory.builder()
                .name("pseudoOne")
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoTwo() {
        pseudoTwo = ProductCategory.builder()
                .name("pseudoTwo")
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoThree() {
        pseudoThree = ProductCategory.builder()
                .name("pseudoThree")
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoFour() {
        pseudoFour = ProductCategory.builder()
                .name("pseudoFour")
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildElementOne() {
        elementOne = Recipe.builder()
                .quantityMultiplier(0.2)
                .quantityUnit(QunatityUnit.LITER)
                .build();
    }

    private void buildElementTwo() {
        elementTwo = Recipe.builder()
                .quantityMultiplier(0.05)
                .quantityUnit(QunatityUnit.LITER)
                .build();
    }

    private void buildElementThree() {
        elementThree = Recipe.builder()
                .quantityMultiplier(0.1)
                .quantityUnit(QunatityUnit.LITER)
                .build();
    }

    private void buildStockOne() {
        stockOne = Stock.builder()
                .startingStock(50)
                .soldQuantity(20)
                .build();
    }

    private void buildStockTwo() {
        stockTwo = Stock.builder()
                .startingStock(60)
                .soldQuantity(30)
                .build();
    }

    private void BuildStockThree() {
        stockThree = Stock.builder()
                .startingStock(70)
                .soldQuantity(40)
                .build();
    }

    private void rootAndAggregates() {
        aggregate.setParent(root);
        root.setChildren(new HashSet<ProductCategory>(
                Arrays.asList(aggregate)));
    }

    private void aggregatesAndLeafs() {
        leafOne.setParent(aggregate);
        leafTwo.setParent(aggregate);
        aggregate.setChildren(new HashSet<ProductCategory>(
                Arrays.asList(leafOne, leafTwo)));
    }
    
    private void leafsAndPseudos() {
        leafOne.setChildren(new HashSet<ProductCategory>(
                Arrays.asList(pseudoOne, pseudoTwo)));
        leafTwo.setChildren(new HashSet<ProductCategory>(
                Arrays.asList(pseudoThree, pseudoFour)));
        pseudoOne.setParent(leafOne);
        pseudoTwo.setParent(leafOne);
        pseudoThree.setParent(leafTwo);
        pseudoFour.setParent(leafTwo);
    }

    private void pseudosAndProducts() {
        pseudoOne.setProduct(productOne);
        productOne.setCategory(pseudoOne);

        pseudoTwo.setProduct(productTwo);
        productTwo.setCategory(pseudoTwo);

        pseudoThree.setProduct(productThree);
        productThree.setCategory(pseudoThree);

        pseudoFour.setProduct(productFour);
        productFour.setCategory(pseudoFour);
    }

    private void productFourAndRecipes() {
        productFour.setRecipe(new HashSet<Recipe>(
                Arrays.asList(elementOne, elementTwo, elementThree)));
        elementOne.setOwner(productFour);
        elementTwo.setOwner(productFour);
        elementThree.setOwner(productFour);
    }

    private void recipesAndProducts() {
        elementOne.setElement(productOne);
        elementTwo.setElement(productTwo);
        elementThree.setElement(productThree);
    }

    private void ProductFourAndStocks() {
        productFour.setStock(new HashSet<Stock>(
                Arrays.asList(stockOne, stockTwo, stockThree)));
        stockOne.setOwner(productFour);
        stockTwo.setOwner(productFour);
        stockThree.setOwner(productFour);
    }
}
