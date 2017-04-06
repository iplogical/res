package com.inspirationlogical.receipt.corelib.model;

import static com.inspirationlogical.receipt.corelib.model.enums.Orientation.HORIZONTAL;
import static java.time.LocalDateTime.now;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import javax.persistence.EntityManager;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.inspirationlogical.receipt.corelib.model.adapter.EntityManagerProvider;
import com.inspirationlogical.receipt.corelib.model.entity.Address;
import com.inspirationlogical.receipt.corelib.model.entity.Client;
import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.entity.Recipe;
import com.inspirationlogical.receipt.corelib.model.entity.Reservation;
import com.inspirationlogical.receipt.corelib.model.entity.Restaurant;
import com.inspirationlogical.receipt.corelib.model.entity.Stock;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.entity.VAT;
import com.inspirationlogical.receipt.corelib.model.entity.VATSerie;
import com.inspirationlogical.receipt.corelib.model.enums.EtalonQuantity;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierRepeatPeriod;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.enums.QuantityUnit;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptRecordType;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.enums.VATName;
import com.inspirationlogical.receipt.corelib.model.enums.VATStatus;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

import lombok.Getter;

public class BuildTestSchemaRule implements TestRule {

    public static final int NUMBER_OF_PRODUCTS = 11;
    public static final int NUMBER_OF_PRODUCT_CATEGORIES = 24;
    public static final int NUMBER_OF_PRICE_MODIFIERS = 4;
    public static final int NUMBER_OF_RECIPES = 3;
    public static final int NUMBER_OF_STOCKS = 3;
    public static final int NUMBER_OF_RECEIPTS = 9;
    public static final int NUMBER_OF_RECEIPT_RECORDS = 7;
    public static final int NUMBER_OF_TABLES = 8;
    public static final int NUMBER_OF_DISPLAYABLE_TABLES = 3;
    public static final int NUMBER_OF_RESERVATIONS = 2;
    public static final int NUMBER_OF_RESTAURANT = 1;
    public static final int NUMBER_OF_VAT_SERIE = 1;
    public static final int NUMBER_OF_VAT_RECORDS = 5;

    private @Getter EntityManager entityManager;

    private @Getter Product productOne;
    private @Getter Product productTwo;
    private @Getter Product productThree;
    private @Getter Product productFour;
    private @Getter Product productFive;
    private @Getter Product productSix;
    private @Getter Product productAdHoc;
    private @Getter Product productGameFee;

    private @Getter Product productRecipeElementOne;
    private @Getter Product productRecipeElementTwo;
    private @Getter Product productRecipeElementThree;

    private @Getter ProductCategory root;
    private @Getter ProductCategory aggregateTopOne;
    private @Getter ProductCategory aggregateTopTwo;
    private @Getter ProductCategory aggregateOne;
    private @Getter ProductCategory aggregateTwo;
    private @Getter ProductCategory aggregateThree;
    private @Getter ProductCategory aggregateFour;
    private @Getter ProductCategory aggregateRecipeElements;
    private @Getter ProductCategory leafOne;
    private @Getter ProductCategory leafTwo;
    private @Getter ProductCategory leafThree;
    private @Getter ProductCategory leafFour;
    private @Getter ProductCategory leafRecipeElements;
    private @Getter ProductCategory pseudoOne;
    private @Getter ProductCategory pseudoTwo;
    private @Getter ProductCategory pseudoThree;
    private @Getter ProductCategory pseudoFour;
    private @Getter ProductCategory pseudoFive;
    private @Getter ProductCategory pseudoSix;
    private @Getter ProductCategory pseudoAdHoc;
    private @Getter ProductCategory pseudoGameFee;

    private @Getter ProductCategory pseudoRecipeElementOne;
    private @Getter ProductCategory pseudoRecipeElementTwo;
    private @Getter ProductCategory pseudoRecipeElementThree;

    private @Getter PriceModifier priceModifierOne;
    private @Getter PriceModifier priceModifierTwo;
    private @Getter PriceModifier priceModifierThree;
    private @Getter PriceModifier priceModifierFour;

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
    private @Getter Receipt receiptSaleClosedTable;
    private @Getter Receipt receiptPurchase;
    private @Getter Receipt receiptInventory;
    private @Getter Receipt receiptDisposal;
    private @Getter Receipt receiptOther;

    private @Getter ReceiptRecord receiptRecordSaleOne;
    private @Getter ReceiptRecord receiptRecordSaleTwo;
    private @Getter ReceiptRecord receiptRecordSaleThree;
    private @Getter ReceiptRecord receiptRecordSaleFour;
    private @Getter ReceiptRecord receiptRecordSaleFive;
    private @Getter ReceiptRecord receiptRecordSaleSix;
    private @Getter ReceiptRecord receiptRecordOther;

    private @Getter Table tableNormal;
    private @Getter Table tableNormalClosed;
    private @Getter Table tableVirtual;
    private @Getter Table tablePurchase;
    private @Getter Table tableInventory;
    private @Getter Table tableDisposal;
    private @Getter Table tableOther;
    private @Getter Table tableOrphanage;

    private @Getter Reservation reservationOne;
    private @Getter Reservation reservationTwo;

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
    private TestType testType;

    public BuildTestSchemaRule(){
        this.testType = TestType.DROP_AND_CREATE;
        entityManager = EntityManagerProvider.getEntityManager("TestPersistance");
    }
    public BuildTestSchemaRule(String persistenceName) {
        this.testType = TestType.DROP_AND_CREATE;
        entityManager = EntityManagerProvider.getEntityManager(persistenceName);
    }
    public BuildTestSchemaRule(TestType testType){
        this.testType = testType;
        entityManager = EntityManagerProvider.getEntityManager("TestPersistance");
    }

    private void dropAll(){

        GuardedTransaction.Run(() -> {
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.PriceModifier").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.Recipe").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.Stock").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.Product").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.Receipt").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.Reservation").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.Table").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.Restaurant").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.VAT").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.VATSerie").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.ProductCategory").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM PRODUCT_CATEGORY_RELATIONS").executeUpdate();
            entityManager.createNativeQuery("UPDATE hibernate_sequence SET next_val=1").executeUpdate();
        });
        entityManager.clear();
    }

    private void buildTestSchema() {
        dropAll();
        buildObjects();
        setUpObjectRelationShips();
        persistObjects();
    }

    private void buildObjects() {
        buildProducts();
        buildProductCategories();
        buildPriceModifiers();
        buildRecipes();
        buildStocks();
        buildReceipts();
        buildReceiptRecords();
        buildVatSeries();
        BuildVATs();
        buildTables();
        buildReservations();
        BuildRestaurant();
    }

    private void setUpObjectRelationShips() {
        productCategories();
        categoriesAndPriceModifiers();
        productsAndCategories();
        productFourAndRecipes();
        recipesAndProducts();
        productFourAndStocks();
        tablesAndReceipts();
        tablesAndReservations();
        receiptsAndReceiptRecords();
        receiptsAndVatSerie();
        vatSerieAndVatValues();
        receiptRecordsAndProducts();
        restaurantAndTables();
    }

    private void persistObjects() {
        GuardedTransaction.Run(() -> entityManager.persist(restaurant));
    }

    private void buildProducts() {
        buildProduct();
        buildProductTwo();
        buildProductThree();
        buildProductFour();
        buildProductFive();
        buildProductSix();
        buildProductAdHoc();
        buildProductGameFee();
        buildProductRecipeElementOne();
        buildProductRecipeElementTwo();
        buildProductRecipeElementThree();
    }

    private void buildProductCategories() {
        buildRoot();
        buildAggregateTopOne();
        buildAggregateTopTwo();
        buildAggregateOne();
        buildAggregateTwo();
        buildAggregateThree();
        buildAggregateFour();
        buildAggregateRecipeElements();
        buildLeafOne();
        buildLeafTwo();
        buildLeafThree();
        buildLeafFour();
        buildLeafRecipeElements();
        buildPseudoOne();
        buildPseudoTwo();
        buildPseudoThree();
        buildPseudoFour();
        buildPseudoFive();
        buildPseudoSix();
        buildPseudoAdHoc();
        buildPseudoGameFee();
        buildPseudoRecipeElementOne();
        buildPseudoRecipeElementTwo();
        buildPseudoRecipeElementThree();
    }

    private void buildPriceModifiers() {
        buildPriceModifierOne();
        buildPriceModifierTwo();
        buildPriceModifierThree();
        buildPriceModifierFour();

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
        buildReceiptSaleClosedTable();
        buildReceiptPurchase();
        buildReceiptInventory();
        buildReceiptDisposal();
        buildReceiptOther();
    }

    private void buildReceiptRecords() {
        buildReceiptRecordSaleOne();
        buildReceiptRecordSaleTwo();
        buildReceiptRecordSaleThree();
        buildReceiptRecordSaleFour();
        buildReceiptRecordSaleFive();
        buildReceiptRecordSaleSix();
        buildReceiptRecordOther();
    }

    private void buildVatSeries() {
        buildVatSerieOne();
    }

    private void BuildVATs() {
        buildVatOne();
        buildVatTwo();
        buildVatThree();
        buildVatFour();
        buildVatFive();
    }

    private void buildTables() {
        buildTableNormal();
        buildTableNormalClosed();
        buildTableVirtual();
        buildTablePurchase();
        buildTableInventory();
        buildTableDisposal();
        buildTableOrphanage();
        buildTableOther();
    }

    private void buildReservations() {
        buildReservationOne();
        buildReservationTwo();
        
    }

    private void BuildRestaurant() {
        restaurant = Restaurant.builder()
                .restaurantName("GameUp Pub")
                .companyName("Arcopen Kft.")
                .companyTaxPayerId("1-42-6518879")
                .companyAddress(buildDefaultAddress())
                .restaurantAddress(buildDefaultAddress())
                .build();
     }

    private Address buildDefaultAddress() {
        return Address.builder()
                .ZIPCode("1066")
                .city("Budapest")
                .street("Zichy Jenő utca 4.")
                .build();
    }

    private void buildProduct() {
        productOne = Product.builder()
                .longName("product")
                .shortName("product")
                .salePrice(1000)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.LITER)
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
                .quantityUnit(QuantityUnit.BOTTLE)
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
                .quantityUnit(QuantityUnit.PIECE)
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
                .quantityUnit(QuantityUnit.PIECE)
                .etalonQuantity(EtalonQuantity.KILOGRAM)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildProductFive() {
        productFive = Product.builder()
                .longName("productFive")
                .shortName("productFive")
                .salePrice(780)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.PIECE)
                .etalonQuantity(EtalonQuantity.KILOGRAM)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildProductSix() {
        productSix = Product.builder()
                .longName("productSix")
                .shortName("productSix")
                .salePrice(4990)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.PIECE)
                .etalonQuantity(EtalonQuantity.KILOGRAM)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildProductAdHoc() {
        productAdHoc = Product.builder()
                .longName("productAdHoc")
                .shortName("productAdHoc")
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.PIECE)
                .etalonQuantity(EtalonQuantity.KILOGRAM)
                .type(ProductType.AD_HOC_PRODUCT)
                .build();
    }

    private void buildProductGameFee() {
        productGameFee = Product.builder()
                .longName("Játékdíj")
                .shortName("Játékdíj")
                .salePrice(300)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.PIECE)
                .etalonQuantity(EtalonQuantity.KILOGRAM)
                .type(ProductType.GAME_FEE_PRODUCT)
                .build();
    }

    private void buildProductRecipeElementOne() {
        productRecipeElementOne = Product.builder()
                .longName("productRecipeElementOne")
                .shortName("recipeElementOne")
                .salePrice(0)
                .purchasePrice(100)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.PIECE)
                .etalonQuantity(EtalonQuantity.KILOGRAM)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildProductRecipeElementTwo() {
        productRecipeElementTwo = Product.builder()
                .longName("productRecipeElementTwo")
                .shortName("recipeElementTwo")
                .salePrice(0)
                .purchasePrice(100)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.PIECE)
                .etalonQuantity(EtalonQuantity.KILOGRAM)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildProductRecipeElementThree() {
        productRecipeElementThree = Product.builder()
                .longName("productRecipeElementThree")
                .shortName("recipeElementThree")
                .salePrice(0)
                .purchasePrice(100)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.PIECE)
                .etalonQuantity(EtalonQuantity.KILOGRAM)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildRoot() {
        root = ProductCategory.builder()
                .name("root")
                .type(ProductCategoryType.ROOT)
                .build();
    }


    private void buildAggregateTopOne() {
        aggregateTopOne = ProductCategory.builder()
                .name("aggregateTopOne")
                .type(ProductCategoryType.AGGREGATE)
                .build();
    }

    private void buildAggregateTopTwo() {
        aggregateTopTwo = ProductCategory.builder()
                .name("aggregateTopTwo")
                .type(ProductCategoryType.AGGREGATE)
                .build();
    }

    private void buildAggregateOne() {
        aggregateOne = ProductCategory.builder()
                .name("aggregateOne")
                .type(ProductCategoryType.AGGREGATE)
                .build();
    }

    private void buildAggregateTwo() {
        aggregateTwo = ProductCategory.builder()
                .name("aggregateTwo")
                .type(ProductCategoryType.AGGREGATE)
                .build();
    }

    private void buildAggregateThree() {
        aggregateThree = ProductCategory.builder()
                .name("aggregateThree")
                .type(ProductCategoryType.AGGREGATE)
                .build();
    }

    private void buildAggregateFour() {
        aggregateFour = ProductCategory.builder()
                .name("aggregateFour")
                .type(ProductCategoryType.AGGREGATE)
                .build();
    }

    private void buildAggregateRecipeElements() {
        aggregateRecipeElements = ProductCategory.builder()
                .name("aggregateRecipeElements")
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

    private void buildLeafThree() {
        leafThree = ProductCategory.builder()
                .name("leafThree")
                .type(ProductCategoryType.LEAF)
                .build();
    }

    private void buildLeafFour() {
        leafFour = ProductCategory.builder()
                .name("leafFour")
                .type(ProductCategoryType.LEAF)
                .build();
    }

    private void buildLeafRecipeElements() {
        leafRecipeElements = ProductCategory.builder()
                .name("leafRecipeElements")
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


    private void buildPseudoFive() {
        pseudoFive = ProductCategory.builder()
                .name("pseudoFive")
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoSix() {
        pseudoSix = ProductCategory.builder()
                .name("pseudoSix")
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoAdHoc() {
        pseudoAdHoc = ProductCategory.builder()
                .name("pseudoAdHoc")
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoGameFee() {
        pseudoGameFee = ProductCategory.builder()
                .name("pseudoGameFee")
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoRecipeElementOne() {
        pseudoRecipeElementOne = ProductCategory.builder()
                .name("pseudoRecipeElementOne")
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoRecipeElementTwo() {
        pseudoRecipeElementTwo = ProductCategory.builder()
                .name("pseudoRecipeElementTwo")
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoRecipeElementThree() {
        pseudoRecipeElementThree = ProductCategory.builder()
                .name("pseudoRecipeElementThree")
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPriceModifierOne() {
        priceModifierOne = PriceModifier.builder()
                .name("TestPriceModifier1")
                .type(PriceModifierType.FUTURE_PRICE_MODIFICATION)
                .period(PriceModifierRepeatPeriod.NO_REPETITION)
                .startTime(new GregorianCalendar(2017, 1, 8, 16, 0))
                .endTime(new GregorianCalendar(2017, 1, 8, 20, 20))
                .build();
    }

    private void buildPriceModifierTwo() {
        priceModifierTwo = PriceModifier.builder()
                .name("TestPriceModifier2")
                .type(PriceModifierType.SIMPLE_DISCOUNT)
                .period(PriceModifierRepeatPeriod.DAILY)
                .startTime(new GregorianCalendar(2017, 2, 8, 16, 0))
                .endTime(new GregorianCalendar(2020, 3, 8, 20, 20))
                .discountPercent(33.333)
                .quantityLimit(3)
                .build();
    }

    private void buildPriceModifierThree() {
        priceModifierThree = PriceModifier.builder()
                .name("TestPriceModifier3")
                .type(PriceModifierType.QUANTITY_DISCOUNT)
                .period(PriceModifierRepeatPeriod.WEEKLY)
                .startTime(new GregorianCalendar(2017, 2, 8, 16, 0))
                .endTime(new GregorianCalendar(2017, 5, 8, 20, 20))
                .build();
    }


    private void buildPriceModifierFour() {
        priceModifierFour = PriceModifier.builder()
                .name("TestPriceModifier4")
                .type(PriceModifierType.QUANTITY_DISCOUNT)
                .period(PriceModifierRepeatPeriod.WEEKLY)
                .startTime(new GregorianCalendar(2017, 2, 8, 16, 0))
                .endTime(new GregorianCalendar(2020, 5, 8, 20, 20))
                .discountPercent(33.333)
                .quantityLimit(3)
                .build();
    }

    private void buildElementOne() {
        elementOne = Recipe.builder()
                .quantityMultiplier(0.2)
                .quantityUnit(QuantityUnit.LITER)
                .build();
    }

    private void buildElementTwo() {
        elementTwo = Recipe.builder()
                .quantityMultiplier(0.05)
                .quantityUnit(QuantityUnit.LITER)
                .build();
    }

    private void buildElementThree() {
        elementThree = Recipe.builder()
                .quantityMultiplier(0.1)
                .quantityUnit(QuantityUnit.LITER)
                .build();
    }

    private void buildStockOne() {
        stockOne = Stock.builder()
                .initialQuantity(50)
                .soldQuantity(20)
                .date(now())
                .build();
    }

    private void buildStockTwo() {
        stockTwo = Stock.builder()
                .initialQuantity(60)
                .soldQuantity(30)
                .date(now())
                .build();
    }

    private void BuildStockThree() {
        stockThree = Stock.builder()
                .initialQuantity(70)
                .soldQuantity(40)
                .date(now())
                .build();
    }

    private void buildReceiptSaleOne() {
        receiptSaleOne = Receipt.builder()
                .type(ReceiptType.SALE)
                .status(ReceiptStatus.OPEN)
                .paymentMethod(PaymentMethod.CASH)
                .openTime(new GregorianCalendar())
                .discountPercent(10)
                .client(buildDefaultClient())
                .build();
    }

    private void buildReceiptSaleTwo() {
        receiptSaleTwo = Receipt.builder()
                .type(ReceiptType.SALE)
                .status(ReceiptStatus.CLOSED)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .openTime(new GregorianCalendar())
                .closureTime(new GregorianCalendar())
                .client(buildDefaultClient())
                .build();
    }


    private void buildReceiptSaleThree() {
        receiptSaleThree = Receipt.builder()
                .type(ReceiptType.SALE)
                .status(ReceiptStatus.OPEN)
                .paymentMethod(PaymentMethod.CASH)
                .openTime(new GregorianCalendar())
                .client(buildDefaultClient())
                .build();
    }

    private void buildReceiptSaleFour() {
        receiptSaleFour = Receipt.builder()
                .type(ReceiptType.SALE)
                .status(ReceiptStatus.CLOSED)
                .paymentMethod(PaymentMethod.CASH)
                .openTime(new GregorianCalendar())
                .closureTime(new GregorianCalendar())
                .discountPercent(20)
                .client(buildDefaultClient())
                .build();
    }


    private void buildReceiptSaleClosedTable() {
        receiptSaleClosedTable = Receipt.builder()
                .type(ReceiptType.SALE)
                .status(ReceiptStatus.CLOSED)
                .paymentMethod(PaymentMethod.CASH)
                .openTime(new GregorianCalendar())
                .closureTime(new GregorianCalendar())
                .discountPercent(0)
                .client(buildDefaultClient())
                .build();
    }

    private void buildReceiptPurchase() {
        receiptPurchase = Receipt.builder()
                .type(ReceiptType.PURCHASE)
                .status(ReceiptStatus.OPEN)
                .paymentMethod(PaymentMethod.CASH)
                .openTime(new GregorianCalendar())
                .client(buildDefaultClient())
                .build();
    }

    private void buildReceiptInventory() {
        receiptInventory = Receipt.builder()
                .type(ReceiptType.INVENTORY)
                .status(ReceiptStatus.CLOSED)
                .openTime(new GregorianCalendar())
                .closureTime(new GregorianCalendar())
                .client(buildDefaultClient())
                .build();
    }

    private void buildReceiptDisposal() {
        receiptDisposal = Receipt.builder()
                .type(ReceiptType.DISPOSAL)
                .status(ReceiptStatus.CLOSED)
                .openTime(new GregorianCalendar())
                .closureTime(new GregorianCalendar())
                .client(buildDefaultClient())
                .build();
    }

    private void buildReceiptOther() {
        receiptOther = Receipt.builder()
                .type(ReceiptType.OTHER)
                .status(ReceiptStatus.CLOSED)
                .paymentMethod(PaymentMethod.CASH)
                .openTime(new GregorianCalendar())
                .closureTime(new GregorianCalendar())
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
                .name("Soproni 0,5L")
                .type(ReceiptRecordType.HERE)
                .VAT(27)
                .salePrice(440)
                .purchasePrice(250)
                .soldQuantity(1D)
                .created(Calendar.getInstance())
                .build();
    }

    private void buildReceiptRecordSaleTwo() {
        receiptRecordSaleTwo = ReceiptRecord.builder()
                .name("Jim Beam")
                .type(ReceiptRecordType.HERE)
                .VAT(27)
                .soldQuantity(2D)
                .salePrice(560)
                .purchasePrice(300)
                .created(Calendar.getInstance())
                .build();
    }

    private void buildReceiptRecordSaleThree() {
        receiptRecordSaleThree = ReceiptRecord.builder()
                .name("C")
                .soldQuantity(1D)
                .type(ReceiptRecordType.HERE)
                .created(Calendar.getInstance())
                .build();
    }

    private void buildReceiptRecordSaleFour() {
        receiptRecordSaleFour = ReceiptRecord.builder()
                .name("D")
                .soldQuantity(0.5)
                .type(ReceiptRecordType.HERE)
                .created(Calendar.getInstance())
                .build();
    }


    private void buildReceiptRecordSaleFive() {
        receiptRecordSaleFive = ReceiptRecord.builder()
                .name("Edelweiss 0,5L")
                .type(ReceiptRecordType.HERE)
                .VAT(27)
                .salePrice(780)
                .purchasePrice(350)
                .soldQuantity(2D)
                .created(Calendar.getInstance())
                .build();
    }

    private void buildReceiptRecordSaleSix() {
        receiptRecordSaleSix = ReceiptRecord.builder()
                .name("Game Up Menu")
                .type(ReceiptRecordType.HERE)
                .VAT(27)
                .salePrice(4990)
                .purchasePrice(2500)
                .soldQuantity(2D)
                .created(Calendar.getInstance())
                .build();
    }

    private void buildReceiptRecordOther() {
        receiptRecordOther = ReceiptRecord.builder()
                .name("E")
                .soldQuantity(1)
                .type(ReceiptRecordType.HERE)
                .created(Calendar.getInstance())
                .build();
    }

    private void buildVatSerieOne() {
        vatSerie = VATSerie.builder()
                .status(VATStatus.VALID)
                .build();
    }

    private void buildVatOne() {
        vatOne = VAT.builder()
                .name(VATName.NORMAL)
                .status(VATStatus.VALID)
                .VAT(27)
                .build();
    }

    private void buildVatTwo() {
        vatTwo = VAT.builder()
                .name(VATName.REDUCED)
                .status(VATStatus.VALID)
                .VAT(18)
                .build();
    }

    private void buildVatThree() {
        vatThree = VAT.builder()
                .name(VATName.GREATLY_REDUCED)
                .status(VATStatus.VALID)
                .VAT(5)
                .build();
    }

    private void buildVatFour() {
        vatFour = VAT.builder()
                .name(VATName.TAX_TICKET)
                .status(VATStatus.VALID)
                .VAT(0)
                .build();
    }

    private void buildVatFive() {
        vatFive = VAT.builder()
                .name(VATName.TAX_FREE)
                .status(VATStatus.VALID)
                .VAT(0)
                .build();
    }

    private void buildTableNormal() {
        tableNormal = Table.builder()
                .number(1)
                .name("Spicces Feri")
                .type(TableType.NORMAL)
                .visibility(true)
                .capacity(6)
                .guestNumber(4)
                .coordinateX(100)
                .coordinateY(50)
                .orientation(HORIZONTAL)
                .build();
    }

    private void buildTableNormalClosed() {
        tableNormalClosed = Table.builder()
                .number(3)
                .name("Ittas Juci")
                .type(TableType.NORMAL)
                .visibility(true)
                .capacity(6)
                .guestNumber(6)
                .coordinateX(250)
                .coordinateY(50)
                .build();
    }

    private void buildTableVirtual() {
        tableVirtual = Table.builder()
                .number(2)
                .name("Bódult Karcsi")
                .type(TableType.VIRTUAL)
                .visibility(true)
                .capacity(1)
                .guestNumber(1)
                .coordinateX(100)
                .coordinateY(100)
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

    private void buildTableOrphanage() {
        tableOrphanage = Table.builder()
                .number(1004)
                .type(TableType.ORPHANAGE)
                .build();
    }

    private void buildTableOther() {
        tableOther = Table.builder()
                .number(1003)
                .type(TableType.OTHER)
                .build();
       }

    private void buildReservationOne() {
        reservationOne = Reservation.builder()
                .tableNumber(2)
                .startTime(new GregorianCalendar(2017, 2, 8, 16, 0))
                .endTime(new GregorianCalendar(2017, 2, 8, 20, 20))
                .name("TestName1")
                .note("TestNote1")
                .build();
    }

    private void buildReservationTwo() {
        reservationTwo = Reservation.builder()
                .tableNumber(2)
                .startTime(new GregorianCalendar(2017, 2, 12, 16, 0))
                .endTime(new GregorianCalendar(2017, 2, 12, 20, 20))
                .name("TestName2")
                .note("TestNote2")
                .build();
    }

    private void productCategories() {
        rootAndAggregates();
        aggregatesAndAggregates();
        aggregatesAndLeafs();
        leafsAndPseudos();
    }

    private void rootAndAggregates() {
        root.setChildren(new HashSet<>(
                Arrays.asList(aggregateTopOne, aggregateTopTwo, aggregateRecipeElements)));
        aggregateTopOne.setParent(root);
        aggregateTopTwo.setParent(root);
        aggregateRecipeElements.setParent(root);
    }

    private void aggregatesAndAggregates() {
        aggregateTopOne.setChildren(new HashSet<>(
                Arrays.asList(aggregateOne, aggregateTwo)));
        aggregateTopTwo.setChildren(new HashSet<>(
                Arrays.asList(aggregateThree, aggregateFour)));
        aggregateOne.setParent(aggregateTopOne);
        aggregateTwo.setParent(aggregateTopOne);
        aggregateThree.setParent(aggregateTopTwo);
        aggregateFour.setParent(aggregateTopTwo);
    }

    private void aggregatesAndLeafs() {
        leafOne.setParent(aggregateOne);
        leafThree.setParent(aggregateOne);
        leafTwo.setParent(aggregateTwo);
        leafFour.setParent(aggregateTwo);
        leafRecipeElements.setParent(aggregateRecipeElements);
        aggregateOne.setChildren(new HashSet<>(
                Arrays.asList(leafOne, leafThree)));
        aggregateTwo.setChildren(new HashSet<>(
                Arrays.asList(leafTwo, leafFour)));
        aggregateRecipeElements.setChildren(new HashSet<>(
                Arrays.asList(leafRecipeElements)));

    }
    
    private void leafsAndPseudos() {
        leafOne.setChildren(new HashSet<>(
                Arrays.asList(pseudoOne, pseudoTwo, pseudoFive, pseudoSix, pseudoAdHoc, pseudoGameFee)));
        leafTwo.setChildren(new HashSet<>(
                Arrays.asList(pseudoThree, pseudoFour)));
        leafRecipeElements.setChildren(new HashSet<>(
                Arrays.asList(pseudoRecipeElementOne, pseudoRecipeElementTwo, pseudoRecipeElementThree)));
        pseudoOne.setParent(leafOne);
        pseudoTwo.setParent(leafOne);
        pseudoFive.setParent(leafOne);
        pseudoSix.setParent(leafOne);
        pseudoAdHoc.setParent(leafOne);
        pseudoGameFee.setParent(leafOne);
        pseudoThree.setParent(leafTwo);
        pseudoFour.setParent(leafTwo);
        pseudoRecipeElementOne.setParent(leafRecipeElements);
        pseudoRecipeElementTwo.setParent(leafRecipeElements);
        pseudoRecipeElementThree.setParent(leafRecipeElements);
    }

    private void categoriesAndPriceModifiers() {
        pseudoOne.setPriceModifier(new HashSet<>(
                Arrays.asList(priceModifierOne, priceModifierTwo)));
        leafTwo.setPriceModifier(new HashSet<>(
                Collections.singletonList(priceModifierThree)));
        pseudoTwo.setPriceModifier(new HashSet<>(
                Collections.singletonList(priceModifierFour)));
        priceModifierOne.setOwner(pseudoOne);
        priceModifierTwo.setOwner(pseudoOne);
        priceModifierThree.setOwner(leafTwo);
        priceModifierFour.setOwner(pseudoTwo);
    }

    private void productsAndCategories() {
        pseudoOne.setProduct(productOne);
        productOne.setCategory(pseudoOne);

        pseudoTwo.setProduct(productTwo);
        productTwo.setCategory(pseudoTwo);

        pseudoThree.setProduct(productThree);
        productThree.setCategory(pseudoThree);

        pseudoFour.setProduct(productFour);
        productFour.setCategory(pseudoFour);

        pseudoFive.setProduct(productFive);
        productFive.setCategory(pseudoFive);

        pseudoSix.setProduct(productSix);
        productSix.setCategory(pseudoSix);

        pseudoAdHoc.setProduct(productAdHoc);
        productAdHoc.setCategory(pseudoAdHoc);

        pseudoGameFee.setProduct(productGameFee);
        productGameFee.setCategory(pseudoGameFee);

        pseudoRecipeElementOne.setProduct(productRecipeElementOne);
        productRecipeElementOne.setCategory(pseudoRecipeElementOne);

        pseudoRecipeElementTwo.setProduct(productRecipeElementTwo);
        productRecipeElementTwo.setCategory(pseudoRecipeElementTwo);

        pseudoRecipeElementThree.setProduct(productRecipeElementThree);
        productRecipeElementThree.setCategory(pseudoRecipeElementThree);

    }

    private void productFourAndRecipes() {
        productFour.setRecipe(new HashSet<>(
                Arrays.asList(elementOne, elementTwo, elementThree)));
        elementOne.setOwner(productFour);
        elementTwo.setOwner(productFour);
        elementThree.setOwner(productFour);
    }

    private void recipesAndProducts() {
        elementOne.setElement(productRecipeElementOne);
        elementTwo.setElement(productRecipeElementTwo);
        elementThree.setElement(productRecipeElementThree);
    }

    private void productFourAndStocks() {
        productFour.setStock(new HashSet<>(
                Arrays.asList(stockOne, stockTwo, stockThree)));
        stockOne.setOwner(productFour);
        stockTwo.setOwner(productFour);
        stockThree.setOwner(productFour);
    }

    private void tablesAndReceipts() {
        receiptsToTables();
        tablesToReceipts();
     }

    private void tablesAndReservations() {
        tableNormal.setReservation(new HashSet<>(
                Arrays.asList(reservationOne, reservationTwo)));
        reservationOne.setOwner(tableNormal);
        reservationTwo.setOwner(tableNormal);
    }

    private void receiptsAndReceiptRecords() {
        receiptSaleOne.setRecords(new HashSet<>(
                Arrays.asList(receiptRecordSaleOne, receiptRecordSaleTwo,
                        receiptRecordSaleFive, receiptRecordSaleSix)));
        receiptSaleTwo.setRecords(new HashSet<>(
                Arrays.asList(receiptRecordSaleThree, receiptRecordSaleFour)));
        receiptOther.setRecords(new HashSet<>(
                Collections.singletonList(receiptRecordOther)));
        receiptRecordSaleOne.setOwner(receiptSaleOne);
        receiptRecordSaleTwo.setOwner(receiptSaleOne);
        receiptRecordSaleFive.setOwner(receiptSaleOne);
        receiptRecordSaleSix.setOwner(receiptSaleOne);
        receiptRecordSaleThree.setOwner(receiptSaleTwo);
        receiptRecordSaleFour.setOwner(receiptSaleTwo);
        receiptRecordOther.setOwner(receiptOther);
        
    }

    private void receiptRecordsAndProducts() {
        receiptRecordSaleOne.setProduct(productOne);
        receiptRecordSaleTwo.setProduct(productTwo);
        receiptRecordSaleThree.setProduct(productThree);
        receiptRecordSaleFour.setProduct(productFour);
        receiptRecordSaleFive.setProduct(productFive);
        receiptRecordSaleSix.setProduct(productSix);
    }

    private void receiptsAndVatSerie() {
        receiptSaleOne.setVATSerie(vatSerie);
        receiptSaleTwo.setVATSerie(vatSerie);
        receiptSaleThree.setVATSerie(vatSerie);
        receiptSaleFour.setVATSerie(vatSerie);
        receiptSaleClosedTable.setVATSerie(vatSerie);
        receiptPurchase.setVATSerie(vatSerie);
        receiptInventory.setVATSerie(vatSerie);
        receiptDisposal.setVATSerie(vatSerie);
        receiptOther.setVATSerie(vatSerie);
    }

    private void vatSerieAndVatValues() {
        vatSerie.setVat(new HashSet<>(
                Arrays.asList(vatOne, vatTwo, vatThree, vatFour, vatFive)));
        vatOne.setSerie(vatSerie);
        vatTwo.setSerie(vatSerie);
        vatThree.setSerie(vatSerie);
        vatFour.setSerie(vatSerie);
        vatFive.setSerie(vatSerie);
    }

    private void restaurantAndTables() {
        //FIXME: Add service for building special tables in production
        restaurant.setTable(new HashSet<>(
                Arrays.asList(tableNormal, tableNormalClosed, tableVirtual, tablePurchase,
                        tableInventory, tableDisposal, tableOther, tableOrphanage)));
        tableNormal.setOwner(restaurant);
        tableNormalClosed.setOwner(restaurant);
        tableVirtual.setOwner(restaurant);
        tablePurchase.setOwner(restaurant);
        tableInventory.setOwner(restaurant);
        tableDisposal.setOwner(restaurant);
        tableOther.setOwner(restaurant);
        tableOrphanage.setOwner(restaurant);

   }

    private void tablesToReceipts() {
        receiptSaleOne.setOwner(tableNormal);
        receiptSaleTwo.setOwner(tableNormal);
        receiptSaleThree.setOwner(tableVirtual);
        receiptSaleFour.setOwner(tableVirtual);
        receiptSaleClosedTable.setOwner(tableNormalClosed);
        receiptPurchase.setOwner(tablePurchase);
        receiptInventory.setOwner(tableInventory);
        receiptDisposal.setOwner(tableDisposal);
        receiptOther.setOwner(tableOther);
    }

    private void receiptsToTables() {
        tableNormal.setReceipt(new HashSet<>(
                Arrays.asList(receiptSaleOne, receiptSaleTwo)));
        tableNormalClosed.setReceipt(new HashSet<>(
                Collections.singletonList(receiptSaleClosedTable)));
        tableVirtual.setReceipt(new HashSet<>(
                Arrays.asList(receiptSaleThree, receiptSaleFour)));
        tablePurchase.setReceipt(new HashSet<>(
                Collections.singletonList(receiptPurchase)));
        tableInventory.setReceipt(new HashSet<>(
                Collections.singletonList(receiptInventory)));
        tableDisposal.setReceipt(new HashSet<>(
                Collections.singletonList(receiptDisposal)));
        tableOther.setReceipt(new HashSet<>(
                Collections.singletonList(receiptOther)));
    }
}
