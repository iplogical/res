package com.inspirationlogical.receipt.corelib.utility;

import static com.inspirationlogical.receipt.corelib.model.enums.Orientation.HORIZONTAL;
import static java.time.LocalDateTime.now;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.corelib.model.adapter.EntityManagerProvider;
import com.inspirationlogical.receipt.corelib.model.entity.*;
import com.inspirationlogical.receipt.corelib.model.enums.*;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

import lombok.Getter;

public class BuildSchema  {

    public static final int NUMBER_OF_PRODUCTS = 11;
    public static final int NUMBER_OF_PRODUCT_CATEGORIES = 24;
    public static final int NUMBER_OF_PRICE_MODIFIERS = 4;
    public static final int NUMBER_OF_RECIPES = 11;
    public static final int NUMBER_OF_STOCKS = 3;
    public static final int NUMBER_OF_RECEIPTS = 9;
    public static final int NUMBER_OF_RECEIPT_RECORDS = 7;
    public static final int NUMBER_OF_TABLES = 8;
    public static final int NUMBER_OF_DISPLAYABLE_TABLES = 3;
    public static final int NUMBER_OF_RESERVATIONS = 2;
    public static final int NUMBER_OF_RESTAURANT = 1;
    public static final int NUMBER_OF_VAT_SERIE = 1;
    public static final int NUMBER_OF_VAT_RECORDS = 5;
    public static final int NUMBER_OF_DAILY_CLOSURES = 2;

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

    private @Getter Recipe productOnePartOne;
    private @Getter Recipe productTwoPartOne;
    private @Getter Recipe productThreePartOne;
    private @Getter Recipe productFourPartOne;
    private @Getter Recipe productFourPartTwo;
    private @Getter Recipe productFourPartThree;
    private @Getter Recipe productFivePartOne;
    private @Getter Recipe productSixPartOne;
    private @Getter Recipe productRecipeElementOnePartOne;
    private @Getter Recipe productRecipeElementTwoPartOne;
    private @Getter Recipe productRecipeElementThreePartOne;

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

    private @Getter DailyClosure dailyClosureOne;
    private @Getter DailyClosure dailyClosureTwo;

    private TestType testType;

    public BuildSchema(){
        this.testType = TestType.DROP_AND_CREATE;
        entityManager = EntityManagerProvider.getEntityManager("TestPersistance");
    }
    public BuildSchema(String persistenceName) {
        this.testType = TestType.DROP_AND_CREATE;
        entityManager = EntityManagerProvider.getEntityManager(persistenceName);
    }
    public BuildSchema(TestType testType){
        this.testType = testType;
        entityManager = EntityManagerProvider.getEntityManager("TestPersistance");
    }

    private void dropAll(){

        GuardedTransaction.run(() -> {
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.PriceModifier").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.Recipe").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.Stock").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.Product").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.Receipt").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.Reservation").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.Table").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.DailyClosure").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.Restaurant").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.VAT").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.VATSerie").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.ProductCategory").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM PRODUCT_CATEGORY_RELATIONS").executeUpdate();
            entityManager.createNativeQuery("UPDATE hibernate_sequence SET next_val=1").executeUpdate();
        });
        entityManager.clear();
    }

    public void buildTestSchema() {
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
        buildRestaurant();
        buildDailyClosures();
    }

    private void buildDailyClosures() {
        buildDailyClosureOne();
        buildDailyClosureTwo();
    }

    private void setUpObjectRelationShips() {
        productCategories();
        categoriesAndPriceModifiers();
        productsAndCategories();
        productsAndRecipes();
        recipesAndProducts();
        productFourAndStocks();
        tablesAndReceipts();
        tablesAndReservations();
        receiptsAndReceiptRecords();
        receiptsAndVatSerie();
        vatSerieAndVatValues();
        receiptRecordsAndProducts();
        restaurantAndTables();
        restaurantAndDailyClosures();
    }

    private void persistObjects() {
        GuardedTransaction.run(() -> entityManager.persist(restaurant));
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
        buildProductOnePartOne();
        buildProductTwoPartOne();
        buildProductThreePartOne();
        buildProductFourPartOne();
        buildProductFourPartTwo();
        buildProductFourPartThree();
        buildProductFivePartOne();
        buildProductSixPartOne();
        buildProductRecipeElementOnePartOne();
        buildProductRecipeElementOnePartTwo();
        buildProductRecipeElementOnePartThree();
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

    private void buildRestaurant() {
        restaurant = Restaurant.builder()
                .restaurantName("GameUp Pub")
                .companyName("Arcopen Kft.")
                .companyTaxPayerId("1-42-6518879")
                .companyAddress(buildDefaultAddress())
                .restaurantAddress(buildDefaultAddress())
                .receiptNote("Árvíztűrő tükörfúrógép")
                .socialMediaInfo("facebook.com/gameuppub")
                .webSite("http://www.gameup.hu")
                .phoneNumber("+36 30/287-87-66")
                .build();
     }

    private Address buildDefaultAddress() {
        return Address.builder()
                .ZIPCode("1066")
                .city("Budapest")
                .street("Zichy Jenő utca 4.")
                .build();
    }

    private void buildDailyClosureOne() {
        dailyClosureOne = DailyClosure.builder()
                .closureTime(now().minusDays(1))
                .sumPurchaseNetPriceCash(10000)
                .sumPurchaseNetPriceCreditCard(10000)
                .sumPurchaseNetPriceCoupon(10000)
                .sumPurchaseNetPriceTotal(30000)

                .sumPurchaseGrossPriceCash(12000)
                .sumPurchaseGrossPriceCreditCard(12000)
                .sumPurchaseGrossPriceCoupon(12000)
                .sumPurchaseGrossPriceTotal(36000)

                .sumSaleNetPriceCash(20000)
                .sumSaleNetPriceCreditCard(20000)
                .sumSaleNetPriceCoupon(20000)
                .sumSaleNetPriceTotal(60000)

                .sumSaleGrossPriceCash(25000)
                .sumSaleGrossPriceCreditCard(25000)
                .sumSaleGrossPriceCoupon(25000)
                .sumSaleGrossPriceTotal(75000)

                .profit(30000)
                .markup(50)
                .receiptAverage(3000)
                .numberOfReceipts(25)
                .discount(5)
                .build();
    }

    private void buildDailyClosureTwo() {
        dailyClosureTwo = DailyClosure.builder()
                .closureTime(null)
                .sumPurchaseNetPriceCash(0)
                .sumPurchaseNetPriceCreditCard(0)
                .sumPurchaseNetPriceCoupon(0)
                .sumPurchaseNetPriceTotal(0)

                .sumPurchaseGrossPriceCash(0)
                .sumPurchaseGrossPriceCreditCard(0)
                .sumPurchaseGrossPriceCoupon(0)
                .sumPurchaseGrossPriceTotal(0)

                .sumSaleNetPriceCash(0)
                .sumSaleNetPriceCreditCard(0)
                .sumSaleNetPriceCoupon(0)
                .sumSaleNetPriceTotal(0)

                .sumSaleGrossPriceCash(0)
                .sumSaleGrossPriceCreditCard(0)
                .sumSaleGrossPriceCoupon(0)
                .sumSaleGrossPriceTotal(0)

                .profit(0)
                .markup(0)
                .receiptAverage(0)
                .numberOfReceipts(0)
                .discount(0)
                .build();
    }

    private void buildProduct() {
        productOne = Product.builder()
                .longName("product")
                .shortName("product")
                .salePrice(1000)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildProductTwo() {
        productTwo = Product.builder()
                .longName("productTwo")
                .shortName("productTwo")
                .salePrice(200)
                .purchasePrice(100)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.LITER)
                .storageMultiplier(50)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildProductThree() {
        productThree = Product.builder()
                .longName("productThree")
                .shortName("productThree")
                .salePrice(790)
                .purchasePrice(400)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.GRAM)
                .storageMultiplier(1000)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildProductFour() {
        productFour = Product.builder()
                .longName("productFour")
                .shortName("productFour")
                .salePrice(990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.GRAM)
                .storageMultiplier(2000)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildProductFive() {
        productFive = Product.builder()
                .longName("productFive")
                .shortName("productFive")
                .salePrice(780)
                .purchasePrice(450)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.LITER)
                .storageMultiplier(2)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildProductSix() {
        productSix = Product.builder()
                .longName("productSix")
                .shortName("productSix")
                .salePrice(4990)
                .purchasePrice(2500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildProductAdHoc() {
        productAdHoc = Product.builder()
                .longName("productAdHoc")
                .shortName("productAdHoc")
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.AD_HOC_PRODUCT)
                .build();
    }

    private void buildProductGameFee() {
        productGameFee = Product.builder()
                .longName("Játékdíj")
                .shortName("Játékdíj")
                .salePrice(300)
                .purchasePrice(0)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.LITER)
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
                .quantityUnit(QuantityUnit.KILOGRAM)
                .storageMultiplier(10)
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
                .quantityUnit(QuantityUnit.GRAM)
                .storageMultiplier(5000)
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
                .quantityUnit(QuantityUnit.KILOGRAM)
                .storageMultiplier(2)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildRoot() {
        root = ProductCategory.builder()
                .name("root")
                .type(ProductCategoryType.ROOT)
                .status(ProductStatus.ACTIVE)
                .build();
    }


    private void buildAggregateTopOne() {
        aggregateTopOne = ProductCategory.builder()
                .name("aggregateTopOne")
                .type(ProductCategoryType.AGGREGATE)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildAggregateTopTwo() {
        aggregateTopTwo = ProductCategory.builder()
                .name("aggregateTopTwo")
                .type(ProductCategoryType.AGGREGATE)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildAggregateOne() {
        aggregateOne = ProductCategory.builder()
                .name("aggregateOne")
                .type(ProductCategoryType.AGGREGATE)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildAggregateTwo() {
        aggregateTwo = ProductCategory.builder()
                .name("aggregateTwo")
                .type(ProductCategoryType.AGGREGATE)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildAggregateThree() {
        aggregateThree = ProductCategory.builder()
                .name("aggregateThree")
                .type(ProductCategoryType.AGGREGATE)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildAggregateFour() {
        aggregateFour = ProductCategory.builder()
                .name("aggregateFour")
                .type(ProductCategoryType.AGGREGATE)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildAggregateRecipeElements() {
        aggregateRecipeElements = ProductCategory.builder()
                .name("aggregateRecipeElements")
                .type(ProductCategoryType.AGGREGATE)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildLeafOne() {
        leafOne = ProductCategory.builder()
                .name("leafOne")
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildLeafTwo() {
        leafTwo = ProductCategory.builder()
                .name("leafTwo")
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildLeafThree() {
        leafThree = ProductCategory.builder()
                .name("leafThree")
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildLeafFour() {
        leafFour = ProductCategory.builder()
                .name("leafFour")
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildLeafRecipeElements() {
        leafRecipeElements = ProductCategory.builder()
                .name("leafRecipeElements")
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildPseudoOne() {
        pseudoOne = ProductCategory.builder()
                .name("pseudoOne")
                .type(ProductCategoryType.PSEUDO)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildPseudoTwo() {
        pseudoTwo = ProductCategory.builder()
                .name("pseudoTwo")
                .type(ProductCategoryType.PSEUDO)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildPseudoThree() {
        pseudoThree = ProductCategory.builder()
                .name("pseudoThree")
                .type(ProductCategoryType.PSEUDO)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildPseudoFour() {
        pseudoFour = ProductCategory.builder()
                .name("pseudoFour")
                .type(ProductCategoryType.PSEUDO)
                .status(ProductStatus.ACTIVE)
                .build();
    }


    private void buildPseudoFive() {
        pseudoFive = ProductCategory.builder()
                .name("pseudoFive")
                .type(ProductCategoryType.PSEUDO)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildPseudoSix() {
        pseudoSix = ProductCategory.builder()
                .name("pseudoSix")
                .type(ProductCategoryType.PSEUDO)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildPseudoAdHoc() {
        pseudoAdHoc = ProductCategory.builder()
                .name("pseudoAdHoc")
                .type(ProductCategoryType.PSEUDO)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildPseudoGameFee() {
        pseudoGameFee = ProductCategory.builder()
                .name("pseudoGameFee")
                .type(ProductCategoryType.PSEUDO)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildPseudoRecipeElementOne() {
        pseudoRecipeElementOne = ProductCategory.builder()
                .name("pseudoRecipeElementOne")
                .type(ProductCategoryType.PSEUDO)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildPseudoRecipeElementTwo() {
        pseudoRecipeElementTwo = ProductCategory.builder()
                .name("pseudoRecipeElementTwo")
                .type(ProductCategoryType.PSEUDO)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildPseudoRecipeElementThree() {
        pseudoRecipeElementThree = ProductCategory.builder()
                .name("pseudoRecipeElementThree")
                .type(ProductCategoryType.PSEUDO)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildPriceModifierOne() {
        priceModifierOne = PriceModifier.builder()
                .name("TestPriceModifier1")
                .type(PriceModifierType.SIMPLE_DISCOUNT)
                .repeatPeriod(PriceModifierRepeatPeriod.NO_REPETITION)
                .startDate(LocalDateTime.of(2017, 1, 8, 16, 0))
                .endDate(LocalDateTime.of(2017, 1, 8, 20, 20))
                .discountPercent(33.333)
                .build();
    }

    private void buildPriceModifierTwo() {
        priceModifierTwo = PriceModifier.builder()
                .name("TestPriceModifier2")
                .type(PriceModifierType.SIMPLE_DISCOUNT)
                .repeatPeriod(PriceModifierRepeatPeriod.DAILY)
                .startTime(LocalTime.now().minusMinutes(5))
                .endTime(LocalTime.now().plusMinutes(5))
                .startDate(LocalDateTime.of(2017, 2, 8, 16, 0))
                .endDate(LocalDateTime.of(2020, 3, 8, 20, 20))
                .discountPercent(33.333)
                .build();
    }

    private void buildPriceModifierThree() {
        priceModifierThree = PriceModifier.builder()
                .name("TestPriceModifier3")
                .type(PriceModifierType.QUANTITY_DISCOUNT)
                .repeatPeriod(PriceModifierRepeatPeriod.WEEKLY)
                .startDate(LocalDateTime.of(2017, 2, 8, 16, 0))
                .endDate(LocalDateTime.of(2017, 5, 8, 20, 20))
                .discountPercent(33.333)
                .quantityLimit(3)
                .build();
    }


    private void buildPriceModifierFour() {
        priceModifierFour = PriceModifier.builder()
                .name("TestPriceModifier4")
                .type(PriceModifierType.QUANTITY_DISCOUNT)
                .repeatPeriod(PriceModifierRepeatPeriod.WEEKLY)
                .dayOfWeek(LocalDate.now().getDayOfWeek())
                .startDate(LocalDateTime.of(2017, 2, 8, 16, 0))
                .endDate(LocalDateTime.of(2020, 5, 8, 20, 20))
                .discountPercent(33.333)
                .quantityLimit(3)
                .build();
    }

    private void buildProductOnePartOne() {
        productOnePartOne = Recipe.builder()
                .quantityMultiplier(4)
                .build();
    }

    private void buildProductTwoPartOne() {
        productTwoPartOne = Recipe.builder()
                .quantityMultiplier(0.5)
                .build();
    }

    private void buildProductThreePartOne() {
        productThreePartOne = Recipe.builder()
                .quantityMultiplier(30)
                .build();
    }

    private void buildProductFourPartOne() {
        productFourPartOne = Recipe.builder()
                .quantityMultiplier(0.2)
                .build();
    }

    private void buildProductFourPartTwo() {
        productFourPartTwo = Recipe.builder()
                .quantityMultiplier(0.05)
                .build();
    }

    private void buildProductFourPartThree() {
        productFourPartThree = Recipe.builder()
                .quantityMultiplier(0.1)
                .build();
    }

    private void buildProductFivePartOne() {
        productFivePartOne = Recipe.builder()
                .quantityMultiplier(0.2)
                .build();
    }

    private void buildProductSixPartOne() {
        productSixPartOne = Recipe.builder()
                .quantityMultiplier(2)
                .build();
    }


    private void buildProductRecipeElementOnePartOne() {
        productRecipeElementOnePartOne = Recipe.builder()
                .quantityMultiplier(0.01)
                .build();
    }

    private void buildProductRecipeElementOnePartTwo() {
        productRecipeElementTwoPartOne = Recipe.builder()
                .quantityMultiplier(20)
                .build();
    }

    private void buildProductRecipeElementOnePartThree() {
        productRecipeElementThreePartOne = Recipe.builder()
                .quantityMultiplier(0.02)
                .build();
    }

    private void buildStockOne() {
        stockOne = Stock.builder()
                .initialQuantity(60)
                .soldQuantity(20)
                .date(LocalDateTime.of(2017, 2, 6, 20, 0))
                .build();
    }

    private void buildStockTwo() {
        stockTwo = Stock.builder()
                .initialQuantity(40)
                .soldQuantity(30)
                .date(LocalDateTime.of(2017, 2, 7, 20, 0))
                .build();
    }

    private void BuildStockThree() {
        stockThree = Stock.builder()
                .initialQuantity(10)
                .soldQuantity(2)
                .purchasedQuantity(2)
                .date(LocalDateTime.of(2017, 2, 8, 20, 0))
                .build();
    }

    private void buildReceiptSaleOne() {
        receiptSaleOne = Receipt.builder()
                .type(ReceiptType.SALE)
                .status(ReceiptStatus.OPEN)
                .paymentMethod(PaymentMethod.CASH)
                .openTime(LocalDateTime.now())
                .discountPercent(10)
                .sumPurchaseGrossPrice(6550)
                .sumPurchaseNetPrice(5157)
                .sumSaleGrossPrice(13100)
                .sumSaleNetPrice(10314)
                .client(buildDefaultClient())
                .build();
    }

    private void buildReceiptSaleTwo() {
        receiptSaleTwo = Receipt.builder()
                .type(ReceiptType.SALE)
                .status(ReceiptStatus.CLOSED)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .openTime(LocalDateTime.now())
                .closureTime(LocalDateTime.now())
                .client(buildDefaultClient())
                .build();
    }


    private void buildReceiptSaleThree() {
        receiptSaleThree = Receipt.builder()
                .type(ReceiptType.SALE)
                .status(ReceiptStatus.OPEN)
                .paymentMethod(PaymentMethod.CASH)
                .openTime(LocalDateTime.now())
                .client(buildDefaultClient())
                .build();
    }

    private void buildReceiptSaleFour() {
        receiptSaleFour = Receipt.builder()
                .type(ReceiptType.SALE)
                .status(ReceiptStatus.CLOSED)
                .paymentMethod(PaymentMethod.CASH)
                .openTime(LocalDateTime.now())
                .closureTime(LocalDateTime.now())
                .discountPercent(20)
                .client(buildDefaultClient())
                .build();
    }


    private void buildReceiptSaleClosedTable() {
        receiptSaleClosedTable = Receipt.builder()
                .type(ReceiptType.SALE)
                .status(ReceiptStatus.CLOSED)
                .paymentMethod(PaymentMethod.CASH)
                .openTime(LocalDateTime.now())
                .closureTime(LocalDateTime.now())
                .discountPercent(0)
                .client(buildDefaultClient())
                .build();
    }

    private void buildReceiptPurchase() {
        receiptPurchase = Receipt.builder()
                .type(ReceiptType.PURCHASE)
                .records(new ArrayList<>())
                .status(ReceiptStatus.CLOSED)
                .paymentMethod(PaymentMethod.CASH)
                .openTime(LocalDateTime.now())
                .closureTime(LocalDateTime.now())
                .client(buildDefaultClient())
                .build();
    }

    private void buildReceiptInventory() {
        receiptInventory = Receipt.builder()
                .type(ReceiptType.INVENTORY)
                .records(new ArrayList<>())
                .status(ReceiptStatus.CLOSED)
                .paymentMethod(PaymentMethod.CASH)
                .openTime(LocalDateTime.now())
                .closureTime(LocalDateTime.now())
                .client(buildDefaultClient())
                .build();
    }

    private void buildReceiptDisposal() {
        receiptDisposal = Receipt.builder()
                .type(ReceiptType.DISPOSAL)
                .records(new ArrayList<>())
                .status(ReceiptStatus.CLOSED)
                .paymentMethod(PaymentMethod.CASH)
                .openTime(LocalDateTime.now())
                .closureTime(LocalDateTime.now())
                .client(buildDefaultClient())
                .build();
    }

    private void buildReceiptOther() {
        receiptOther = Receipt.builder()
                .type(ReceiptType.OTHER)
                .records(new ArrayList<>())
                .status(ReceiptStatus.CLOSED)
                .paymentMethod(PaymentMethod.CASH)
                .openTime(LocalDateTime.now())
                .closureTime(LocalDateTime.now())
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
                .created(now())
                .build();
    }

    private void buildReceiptRecordSaleTwo() {
        receiptRecordSaleTwo = ReceiptRecord.builder()
                .name("Jim Beam")
                .type(ReceiptRecordType.HERE)
                .VAT(27)
                .soldQuantity(2D)
                .absoluteQuantity(2D)
                .salePrice(560)
                .purchasePrice(300)
                .created(now())
                .build();
    }

    private void buildReceiptRecordSaleThree() {
        receiptRecordSaleThree = ReceiptRecord.builder()
                .name("C")
                .soldQuantity(1D)
                .type(ReceiptRecordType.HERE)
                .created(now())
                .build();
    }

    private void buildReceiptRecordSaleFour() {
        receiptRecordSaleFour = ReceiptRecord.builder()
                .name("D")
                .soldQuantity(0.5)
                .type(ReceiptRecordType.HERE)
                .created(now())
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
                .created(now())
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
                .created(now())
                .build();
    }

    private void buildReceiptRecordOther() {
        receiptRecordOther = ReceiptRecord.builder()
                .name("E")
                .soldQuantity(1)
                .type(ReceiptRecordType.HERE)
                .created(now())
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
                .visible(true)
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
                .visible(true)
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
                .visible(true)
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
                .startTime(LocalDateTime.of(2017, 2, 8, 16, 0))
                .endTime(LocalDateTime.of(2017, 2, 8, 20, 20))
                .name("TestName1")
                .note("TestNote1")
                .build();
    }

    private void buildReservationTwo() {
        reservationTwo = Reservation.builder()
                .tableNumber(2)
                .startTime(LocalDateTime.of(2017, 2, 12, 16, 0))
                .endTime(LocalDateTime.of(2017, 2, 12, 20, 20))
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
        pseudoOne.setPriceModifiers(new HashSet<>(
                Arrays.asList(priceModifierOne, priceModifierTwo)));
        leafTwo.setPriceModifiers(new HashSet<>(
                Collections.singletonList(priceModifierThree)));
        pseudoTwo.setPriceModifiers(new HashSet<>(
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

    private void productsAndRecipes() {
        productOne.setRecipes(new HashSet<>(
                Collections.singletonList(productOnePartOne)));
        productTwo.setRecipes(new HashSet<>(
                Collections.singletonList(productTwoPartOne)));
        productThree.setRecipes(new HashSet<>(
                Collections.singletonList(productThreePartOne)));
        productFour.setRecipes(new HashSet<>(
                Arrays.asList(productFourPartOne, productFourPartTwo, productFourPartThree)));
        productFive.setRecipes(new HashSet<>(
                Collections.singletonList(productFivePartOne)));
        productSix.setRecipes(new HashSet<>(
                Collections.singletonList(productSixPartOne)));
        productRecipeElementOne.setRecipes(new HashSet<>(
                Collections.singletonList(productRecipeElementOnePartOne)));
        productRecipeElementTwo.setRecipes(new HashSet<>(
                Collections.singletonList(productRecipeElementTwoPartOne)));
        productRecipeElementThree.setRecipes(new HashSet<>(
                Collections.singletonList(productRecipeElementThreePartOne)));
        productOnePartOne.setOwner(productOne);
        productTwoPartOne.setOwner(productTwo);
        productThreePartOne.setOwner(productThree);
        productFourPartOne.setOwner(productFour);
        productFourPartTwo.setOwner(productFour);
        productFourPartThree.setOwner(productFour);
        productFivePartOne.setOwner(productFive);
        productSixPartOne.setOwner(productSix);
        productRecipeElementOnePartOne.setOwner(productRecipeElementOne);
        productRecipeElementTwoPartOne.setOwner(productRecipeElementTwo);
        productRecipeElementThreePartOne.setOwner(productRecipeElementThree);
    }

    private void recipesAndProducts() {
        productOnePartOne.setComponent(productOne);
        productTwoPartOne.setComponent(productTwo);
        productThreePartOne.setComponent(productThree);
        productFourPartOne.setComponent(productRecipeElementOne);
        productFourPartTwo.setComponent(productRecipeElementTwo);
        productFourPartThree.setComponent(productRecipeElementThree);
        productFivePartOne.setComponent(productFive);
        productSixPartOne.setComponent(productSix);
        productRecipeElementOnePartOne.setComponent(productRecipeElementOne);
        productRecipeElementTwoPartOne.setComponent(productRecipeElementTwo);
        productRecipeElementThreePartOne.setComponent(productRecipeElementThree);
    }

    private void productFourAndStocks() {
        productRecipeElementOne.setStocks(new HashSet<>(
                Arrays.asList(stockOne, stockTwo, stockThree)));
        stockOne.setOwner(productRecipeElementOne);
        stockTwo.setOwner(productRecipeElementOne);
        stockThree.setOwner(productRecipeElementOne);
    }

    private void tablesAndReceipts() {
        receiptsToTables();
        tablesToReceipts();
     }

    private void tablesAndReservations() {
        tableNormal.setReservations(new HashSet<>(
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
        restaurant.setTables(new HashSet<>(
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

    private void restaurantAndDailyClosures() {
        restaurant.setDailyClosures(new ArrayList<>(
                Arrays.asList(dailyClosureOne, dailyClosureTwo)));
        dailyClosureOne.setOwner(restaurant);
        dailyClosureTwo.setOwner(restaurant);
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
        tableNormal.setReceipts(new HashSet<>(
                Arrays.asList(receiptSaleOne, receiptSaleTwo)));
        tableNormalClosed.setReceipts(new HashSet<>(
                Collections.singletonList(receiptSaleClosedTable)));
        tableVirtual.setReceipts(new HashSet<>(
                Arrays.asList(receiptSaleThree, receiptSaleFour)));
        tablePurchase.setReceipts(new HashSet<>(
                Collections.singletonList(receiptPurchase)));
        tableInventory.setReceipts(new HashSet<>(
                Collections.singletonList(receiptInventory)));
        tableDisposal.setReceipts(new HashSet<>(
                Collections.singletonList(receiptDisposal)));
        tableOther.setReceipts(new HashSet<>(
                Collections.singletonList(receiptOther)));
    }
}

enum TestType {
    DROP_AND_CREATE,
    CREATE,
    VALIDATE;
}