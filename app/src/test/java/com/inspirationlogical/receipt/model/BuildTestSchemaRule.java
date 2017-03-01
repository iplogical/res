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

    private @Getter Product product;
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
    }

    private void setUpObjectRelationShips() {
        rootAndAggregates();
        aggregatesAndLeafs();
        leafsAndPseudos();
        pseudosAndProducts();
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

    private void buildProductTwo() {
        productTwo = Product.builder()
                .LongName("Wine of the House: Nagy and Nagy Rizling")
                .shortName("Wine: Nagy Rizling")
                .salePrice(200)
                .quantityUnit(QunatityUnit.BOTTLE)
                .etalonQuantity(EtalonQuantity.LITER)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildProductThree() {
        productThree = Product.builder()
                .LongName("Hot Sandwich")
                .shortName("Hot Sandwich")
                .salePrice(790)
                .quantityUnit(QunatityUnit.PIECE)
                .etalonQuantity(EtalonQuantity.KILOGRAM)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildProductFour() {
        productFour = Product.builder()
                .LongName("Cheese Plate")
                .shortName("Cheese Plate")
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
        pseudoOne.setProduct(product);
        product.setCategory(pseudoOne);

        pseudoTwo.setProduct(productTwo);
        productTwo.setCategory(pseudoTwo);

        pseudoThree.setProduct(productThree);
        productThree.setCategory(pseudoThree);

        pseudoFour.setProduct(productFour);
        productFour.setCategory(pseudoFour);
    }
}
