package com.inspirationlogical.receipt.model;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.inspirationlogical.receipt.model.Product;
import com.inspirationlogical.receipt.model.ProductCategory;
import com.inspirationlogical.receipt.model.enums.EtalonQuantity;
import com.inspirationlogical.receipt.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.model.enums.ProductStatus;
import com.inspirationlogical.receipt.model.enums.ProductType;
import com.inspirationlogical.receipt.model.enums.QunatityUnit;
import com.inspirationlogical.receipt.model.enums.ReceiptRecordType;
import com.inspirationlogical.receipt.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.model.enums.ReceiptType;
import com.inspirationlogical.receipt.model.enums.TableType;
import com.inspirationlogical.receipt.model.enums.VATName;

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

    private @Getter Receipt receiptSaleOne;
    private @Getter Receipt receiptSaleTwo;
    private @Getter Receipt receiptSaleThree;
    private @Getter Receipt receiptSaleFour;
    private @Getter Receipt receiptPurchase;
    private @Getter Receipt receiptInventory;
    private @Getter Receipt receiptDisposal;
    private @Getter Receipt receiptOther;

    private @Getter ReceiptRecord receiptRecordSaleOne;
    private @Getter ReceiptRecord receiptRecordSaleTwo;
    private @Getter ReceiptRecord receiptRecordSaleThree;
    private @Getter ReceiptRecord receiptRecordSaleFour;

    private @Getter Table tableNormal;
    private @Getter Table tableVirtual;
    private @Getter Table tablePurchase;
    private @Getter Table tableInventory;
    private @Getter Table tableDisposal;
    private @Getter Table tableOther;

    private @Getter Restaurant restaurant;

    private @Getter VATSerie vatSerie;

    private @Getter VAT vatOne;
    private @Getter VAT vatTwo;
    private @Getter VAT vatThree;
    private @Getter VAT vatFour;
    private @Getter VAT vatFive;

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
        buildReceipts();
        buildReceptRecords();
        BuildVatSerie();
        BuildVATs();
        buildTables();
        BuildRestaurant();
    }

private void setUpObjectRelationShips() {
        rootAndAggregates();
        aggregatesAndLeafs();
        leafsAndPseudos();
        pseudosAndProducts();
        productFourAndRecipes();
        recipesAndProducts();
        productFourAndStocks();
        tablesAndReceipts();
        receiptsAndReceiptRecords();
        receiptsAndVatSerie();
        vatSerieAndVatValues();
        receiptRecordsAndProducts();
        restaurantAndTables();
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


    private void buildReceipts() {
        buildReceiptSaleOne();
        buildReceiptSaleTwo();
        buildReceiptSaleThree();
        buildReceiptSaleFour();
        buildReceiptPurchase();
        buildReceiptInventory();
        buildReceiptDisposal();
        buildReceiptOther();
    }

    private void buildReceptRecords() {
        buildReceiptRecordSaleOne();
        buildReceiptRecordSaleTwo();
        buildReceiptRecordSaleThree();
        buildReceiptRecordSaleFour();
    }

    private void BuildVatSerie() {
        buildVatSerie();
    }

    private void BuildVATs() {
        buildVAT1();
        buildVAT2();
        buildVAT3();
        buildVAT4();
        buildVAT5();
    }

    private void buildTables() {
        buildTableOne();
        buildTableTwo();
        buildTablePurchase();
        buildTableInventory();
        buildTableDisposal();
        buildTableOther();
    }

    private void BuildRestaurant() {
        restaurant = Restaurant.builder()
                .name("TestRestaurant")
                .companyName("TestCompany")
                .address("TestAddress")
                .build();
     }

private void buildProduct() {
        productOne = Product.builder()
                .longName("product")
                .shortName("product")
                .salePrice(1000)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QunatityUnit.LITER)
                .etalonQuantity(EtalonQuantity.LITER)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildProductTwo() {
        productTwo = Product.builder()
                .longName("productTwo")
                .shortName("productTwo")
                .salePrice(200)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QunatityUnit.BOTTLE)
                .etalonQuantity(EtalonQuantity.LITER)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildProductThree() {
        productThree = Product.builder()
                .longName("productThree")
                .shortName("productThree")
                .salePrice(790)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QunatityUnit.PIECE)
                .etalonQuantity(EtalonQuantity.KILOGRAM)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildProductFour() {
        productFour = Product.builder()
                .longName("productFour")
                .shortName("productFour")
                .salePrice(990)
                .status(ProductStatus.ACTIVE)
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

    private void buildReceiptSaleOne() {
        receiptSaleOne = Receipt.builder()
                .type(ReceiptType.SALE)
                .status(ReceiptStatus.OPEN)
                .discountAbsolute(1000)
                .client(buildDefaultClient())
                .build();
    }

    private void buildReceiptSaleTwo() {
        receiptSaleTwo = Receipt.builder()
                .type(ReceiptType.SALE)
                .status(ReceiptStatus.CLOSED)
                .discountAbsolute(2000)
                .client(buildDefaultClient())
                .build();
    }


    private void buildReceiptSaleThree() {
        receiptSaleThree = Receipt.builder()
                .type(ReceiptType.SALE)
                .status(ReceiptStatus.OPEN)
                .discountAbsolute(3000)
                .client(buildDefaultClient())
                .build();
    }

    private void buildReceiptSaleFour() {
        receiptSaleFour = Receipt.builder()
                .type(ReceiptType.SALE)
                .status(ReceiptStatus.CLOSED)
                .discountAbsolute(4000)
                .client(buildDefaultClient())
                .build();
    }

    private void buildReceiptPurchase() {
        receiptPurchase = Receipt.builder()
                .type(ReceiptType.PURCHASE)
                .status(ReceiptStatus.OPEN)
                .client(buildDefaultClient())
                .build();
    }

    private void buildReceiptInventory() {
        receiptInventory = Receipt.builder()
                .type(ReceiptType.INVENTORY)
                .status(ReceiptStatus.CLOSED)
                .client(buildDefaultClient())
                .build();
    }

    private void buildReceiptDisposal() {
        receiptDisposal = Receipt.builder()
                .type(ReceiptType.DISPOSAL)
                .status(ReceiptStatus.CLOSED)
                .client(buildDefaultClient())
                .build();
    }

    private void buildReceiptOther() {
        receiptOther = Receipt.builder()
                .type(ReceiptType.OTHER)
                .status(ReceiptStatus.CLOSED)
                .client(buildDefaultClient())
                .build();
    }

    private Client buildDefaultClient() {
        return Client.builder()
                .name("TestClient")
                .address("TestAddress")
                .TAXNumber("TestTaxNumber")
                .build();
    }

    private void buildReceiptRecordSaleOne() {
        receiptRecordSaleOne = ReceiptRecord.builder()
                .name("A")
                .type(ReceiptRecordType.HERE)
                .quantity(1D)
                .build();
    }

    private void buildReceiptRecordSaleTwo() {
        receiptRecordSaleTwo = ReceiptRecord.builder()
                .name("B")
                .quantity(2D)
                .type(ReceiptRecordType.TAKE_AWAY)
                .build();
    }

    private void buildReceiptRecordSaleThree() {
        receiptRecordSaleThree = ReceiptRecord.builder()
                .name("C")
                .quantity(1D)
                .type(ReceiptRecordType.HERE)
                .build();
    }

    private void buildReceiptRecordSaleFour() {
        receiptRecordSaleFour = ReceiptRecord.builder()
                .name("D")
                .quantity(0.5)
                .type(ReceiptRecordType.HERE)
                .build();
    }

    private void buildVatSerie() {
        vatSerie = VATSerie.builder()
                .build();
    }

    private void buildVAT1() {
        vatOne = VAT.builder()
                .name(VATName.NORMAL)
                .VAT(27)
                .build();
    }

    private void buildVAT2() {
        vatTwo = VAT.builder()
                .name(VATName.REDUCED)
                .VAT(18)
                .build();
    }

    private void buildVAT3() {
        vatThree = VAT.builder()
                .name(VATName.GREATLY_REDUCED)
                .VAT(5)
                .build();
    }

    private void buildVAT4() {
        vatFour = VAT.builder()
                .name(VATName.TAX_TICKET)
                .VAT(0)
                .build();
    }

    private void buildVAT5() {
        vatFive = VAT.builder()
                .name(VATName.TAX_FREE)
                .VAT(0)
                .build();
    }

    private void buildTableOne() {
        tableNormal = Table.builder()
                .number(1)
                .type(TableType.NORMAL)
                .build();
    }

    private void buildTableTwo() {
        tableVirtual = Table.builder()
                .number(2)
                .type(TableType.VIRTUAL)
                .build();
    }


    private void buildTablePurchase() {
        tablePurchase = Table.builder()
                .number(1000)
                .type(TableType.PURCHASE)
                .build();
    }

    private void buildTableInventory() {
        tableInventory = Table.builder()
                .number(1001)
                .type(TableType.INVENTORY)
                .build();
    }

    private void buildTableDisposal() {
        tableDisposal = Table.builder()
                .number(1002)
                .type(TableType.DISPOSAL)
                .build();
    }

    private void buildTableOther() {
        tableOther = Table.builder()
                .number(1003)
                .type(TableType.OTHER)
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

    private void productFourAndStocks() {
        productFour.setStock(new HashSet<Stock>(
                Arrays.asList(stockOne, stockTwo, stockThree)));
        stockOne.setOwner(productFour);
        stockTwo.setOwner(productFour);
        stockThree.setOwner(productFour);
    }

    private void tablesAndReceipts() {
        receiptsToTables();
        tablesToReceipts();
     }

    private void receiptsAndReceiptRecords() {
        receiptSaleOne.setRecords(new HashSet<ReceiptRecord>(
                Arrays.asList(receiptRecordSaleOne, receiptRecordSaleTwo)));
        receiptSaleTwo.setRecords(new HashSet<ReceiptRecord>(
                Arrays.asList(receiptRecordSaleThree, receiptRecordSaleFour)));
        receiptRecordSaleOne.setOwner(receiptSaleOne);
        receiptRecordSaleTwo.setOwner(receiptSaleOne);
        receiptRecordSaleThree.setOwner(receiptSaleTwo);
        receiptRecordSaleFour.setOwner(receiptSaleTwo);
        
    }

    private void receiptRecordsAndProducts() {
        receiptRecordSaleOne.setProduct(productOne);
        receiptRecordSaleTwo.setProduct(productTwo);
        receiptRecordSaleThree.setProduct(productThree);
        receiptRecordSaleFour.setProduct(productFour);
    }

    private void receiptsAndVatSerie() {
        receiptSaleOne.setVATSerie(vatSerie);
        receiptSaleTwo.setVATSerie(vatSerie);
        receiptSaleThree.setVATSerie(vatSerie);
        receiptSaleFour.setVATSerie(vatSerie);
        receiptPurchase.setVATSerie(vatSerie);
        receiptInventory.setVATSerie(vatSerie);
        receiptDisposal.setVATSerie(vatSerie);
        receiptOther.setVATSerie(vatSerie);
    }

    private void vatSerieAndVatValues() {
        vatSerie.setVat(new HashSet<VAT>(
                Arrays.asList(vatOne, vatTwo, vatThree, vatFour, vatFive)));
        vatOne.setSerie(vatSerie);
        vatTwo.setSerie(vatSerie);
        vatThree.setSerie(vatSerie);
        vatFour.setSerie(vatSerie);
        vatFive.setSerie(vatSerie);
    }

    private void restaurantAndTables() {
        restaurant.setTable(new HashSet<Table>(
                Arrays.asList(tableNormal, tableVirtual, tablePurchase,
                        tableInventory, tableDisposal, tableOther)));
        tableNormal.setOwner(restaurant);
        tableVirtual.setOwner(restaurant);
        tablePurchase.setOwner(restaurant);
        tableInventory.setOwner(restaurant);
        tableDisposal.setOwner(restaurant);
        tableOther.setOwner(restaurant);
   }

    private void tablesToReceipts() {
        receiptSaleOne.setOwner(tableNormal);
        receiptSaleTwo.setOwner(tableNormal);
        receiptSaleThree.setOwner(tableVirtual);
        receiptSaleFour.setOwner(tableVirtual);
        receiptPurchase.setOwner(tablePurchase);
        receiptInventory.setOwner(tableInventory);
        receiptDisposal.setOwner(tableDisposal);
        receiptOther.setOwner(tableOther);
    }

    private void receiptsToTables() {
        tableNormal.setReceipt(new HashSet<Receipt>(
                Arrays.asList(receiptSaleOne, receiptSaleTwo)));
        tableVirtual.setReceipt(new HashSet<Receipt>(
                Arrays.asList(receiptSaleThree, receiptSaleFour)));
        tablePurchase.setReceipt(new HashSet<Receipt>(
                Arrays.asList(receiptPurchase)));
        tableInventory.setReceipt(new HashSet<Receipt>(
                Arrays.asList(receiptInventory)));
        tableDisposal.setReceipt(new HashSet<Receipt>(
                Arrays.asList(receiptDisposal)));
        tableOther.setReceipt(new HashSet<Receipt>(
                Arrays.asList(receiptOther)));
    }
}
