package com.inspirationlogical.receipt.corelib.model.utils;

import com.inspirationlogical.receipt.corelib.model.entity.*;
import com.inspirationlogical.receipt.corelib.model.enums.*;
import com.inspirationlogical.receipt.corelib.model.transaction.EntityManagerProvider;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;
import lombok.Getter;

import javax.persistence.EntityManager;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static java.time.LocalDateTime.now;

public class BuildTestSchema {

    public static final String PRODUCT_ONE_LONG_NAME = "productOne";
    public static final String PRODUCT_ONE_SHORT_NAME = "product1";

    public static final String AGGREGATE_TOP_ONE_NAME = "aggregateTopOne";
    public static final String AGGREGATE_ONE_NAME = "aggregateOne";
    public static final String LEAF_ONE_NAME = "leafOne";
    public static final String LEAF_FIVE_NAME = "leafFive";

    public static final String PRICE_MODIFIER1_NAME = "TestPriceModifier1";

    public static final String RESERVATION_TEST_TABLE = "20";
    public static final String RESTAURANT_TEST_TABLE = "21";

    public static final String SALE_TEST_TABLE = "25";
    public static final String PAYMENT_TEST_TABLE = "26";
    public static final String TABLE_TEST_TABLE = "27";
    public static final String TABLE_TEST_TABLE_NAME = "tableTableTest";

    public static final int RESERVATION_ONE_TABLE_NUMBER = 1;
    public static final String RESERVATION_ONE_NAME = "TestName1";
    public static final String RESERVATION_ONE_NOTE = "TestNote1";

    private @Getter EntityManager entityManager;

    private @Getter Product productOne;
    private @Getter Product productTwo;
    private @Getter Product productThree;
    private @Getter Product productFour;
    private @Getter Product productFive;
    private @Getter Product productSix;
    private @Getter Product productSeven;
    private @Getter Product productEight;
    private @Getter Product productAdHoc;
    private @Getter Product productGameFee;
    private @Getter Product productServiceFee;

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
    private @Getter ProductCategory leafFive;
    private @Getter ProductCategory leafSix;
    private @Getter ProductCategory leafRecipeElements;
    private @Getter ProductCategory pseudoOne;
    private @Getter ProductCategory pseudoTwo;
    private @Getter ProductCategory pseudoThree;
    private @Getter ProductCategory pseudoFour;
    private @Getter ProductCategory pseudoFive;
    private @Getter ProductCategory pseudoSix;
    private @Getter ProductCategory pseudoSeven;
    private @Getter ProductCategory pseudoEight;
    private @Getter ProductCategory pseudoAdHoc;
    private @Getter ProductCategory pseudoGameFee;
    private @Getter ProductCategory pseudoServiceFee;

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
    private @Getter Recipe productSevenPartOne;
    private @Getter Recipe productEightPartOne;
    private @Getter Recipe productRecipeElementOnePartOne;
    private @Getter Recipe productRecipeElementTwoPartOne;
    private @Getter Recipe productRecipeElementThreePartOne;

    private @Getter Stock stockOne;
    private @Getter Stock stockTwo;
    private @Getter Stock stockThree;

    private @Getter Receipt receiptTableSaleTest;
    private @Getter Receipt receiptPurchase;
    private @Getter Receipt receiptInventory;
    private @Getter Receipt receiptDisposal;

    private @Getter Receipt receiptOther;
    private @Getter ReceiptRecord receiptRecordOther;
    private @Getter ReceiptRecordCreated receiptRecordCreatedOther;

    private @Getter Table tableReservationTest;
    private @Getter Table tableRestaurantTest;
    private @Getter Table tableSaleTest;
    private @Getter Table tablePaymentTest;
    private @Getter Table tableTableTest;
    
    private @Getter Table tablePurchase;
    private @Getter Table tableInventory;
    private @Getter Table tableDisposal;
    private @Getter Table tableOther;
    private @Getter Table tableOrphanage;

    private @Getter Restaurant restaurant;

    private @Getter VATSerie vatSerie;

    private @Getter VAT vatNormal;
    private @Getter VAT vatReduced;
    private @Getter VAT vatGreatlyReduced;
    private @Getter VAT vatTaxTicket;
    private @Getter VAT vatTaxFree;

    private @Getter DailyClosure dailyClosureOne;
    private @Getter DailyClosure dailyClosureTwo;
    private @Getter DailyClosure openDailyClosure;

    public BuildTestSchema(){
        entityManager = EntityManagerProvider.getTestEntityManager();
    }

    public void buildTestSchema() {
        dropAll();
        buildObjects();
        setUpObjectRelationShips();
        persistObjects();
    }

    private void buildObjects() {
        buildVatSeries();
        BuildVATs();
        buildProducts();
        buildProductCategories();
        buildPriceModifiers();
        buildRecipes();
        buildStocks();
        buildReceipts();
        buildReceiptRecords();
        buildReceiptRecordCreateds();
        buildTables();
        buildRestaurant();
        buildDailyClosures();
    }

    private void buildDailyClosures() {
        buildOpenDailyClosure();
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
        receiptsAndReceiptRecords();
        receiptRecordsAndCreated();
        receiptsAndVatSerie();
        vatSerieAndVatValues();
        restaurantAndTables();
        restaurantAndDailyClosures();
    }

    private void buildProducts() {
        buildProductOne();
        buildProductTwo();
        buildProductThree();
        buildProductFour();
        buildProductFive();
        buildProductSix();
        buildProductSeven();
        buildProductEight();
        buildProductAdHoc();
        buildProductGameFee();
        buildServiceFeeProduct();
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
        buildLeafFive();
        buildLeafSix();
        buildLeafRecipeElements();
        buildPseudoOne();
        buildPseudoTwo();
        buildPseudoThree();
        buildPseudoFour();
        buildPseudoFive();
        buildPseudoSix();
        buildPseudoSeven();
        buildPseudoEight();
        buildPseudoAdHoc();
        buildPseudoGameFee();
        buildPseudoServiceFee();
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
        buildProductSevenPartOne();
        buildProductEightPartOne();
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
        buildReceiptTableSaleTest();
        buildReceiptPurchase();
        buildReceiptInventory();
        buildReceiptDisposal();
        buildReceiptOther();
    }

    private void buildReceiptRecords() {
        buildReceiptRecordOther();
    }

    private void buildReceiptRecordCreateds() {
        buildReceiptRecordCreatedOther();
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
        buildTableReservationTest();
        buildTableRestaurantTest();
        buildTableSaleTest();
        buildTablePaymentTest();
        buildTableTableTest();
        
        buildTablePurchase();
        buildTableInventory();
        buildTableDisposal();
        buildTableOrphanage();
        buildTableOther();
    }

    private void dropAll(){
        GuardedTransaction.run(() -> {
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.PriceModifier").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.Recipe").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.Stock").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.Product").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecordCreated").executeUpdate();
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
        });
        entityManager.clear();
    }

    private void persistObjects() {
        GuardedTransaction.persist(restaurant);
        persistProducts();
        persistReceipts();
        persistRecipes();
        persistStocks();
    }

    private void persistProducts() {
        GuardedTransaction.persist(productOne);
        GuardedTransaction.persist(productTwo);
        GuardedTransaction.persist(productThree);
        GuardedTransaction.persist(productFour);
        GuardedTransaction.persist(productFive);
        GuardedTransaction.persist(productSix);
        GuardedTransaction.persist(productSeven);
        GuardedTransaction.persist(productEight);
        GuardedTransaction.persist(productAdHoc);
        GuardedTransaction.persist(productGameFee);
        GuardedTransaction.persist(productServiceFee);
    }


    private void persistReceipts() {
        GuardedTransaction.persist(receiptTableSaleTest);
        GuardedTransaction.persist(receiptPurchase);
        GuardedTransaction.persist(receiptInventory);
        GuardedTransaction.persist(receiptDisposal);
        GuardedTransaction.persist(receiptOther);
    }

    private void persistRecipes() {
        GuardedTransaction.persist(productOnePartOne);
        GuardedTransaction.persist(productTwoPartOne);
        GuardedTransaction.persist(productThreePartOne);
        GuardedTransaction.persist(productFourPartOne);
        GuardedTransaction.persist(productFourPartTwo);
        GuardedTransaction.persist(productFourPartThree);
        GuardedTransaction.persist(productFivePartOne);
        GuardedTransaction.persist(productSixPartOne);
        GuardedTransaction.persist(productSevenPartOne);
        GuardedTransaction.persist(productEightPartOne);
        GuardedTransaction.persist(productRecipeElementOnePartOne);
        GuardedTransaction.persist(productRecipeElementTwoPartOne);
        GuardedTransaction.persist(productRecipeElementThreePartOne);
    }

    private void persistStocks() {
        GuardedTransaction.persist(stockOne);
        GuardedTransaction.persist(stockTwo);
        GuardedTransaction.persist(stockThree);
    }

    private void buildRestaurant() {
        restaurant = Restaurant.builder()
                .restaurantName("GameUp Pub")
                .companyName("Arcopen Kft.")
                .companyTaxPayerId("1-42-6518879")
                .companyAddress(buildDefaultAddress())
                .restaurantAddress(buildDefaultAddress())
                .receiptNote("A végösszeg 8 % szervízdíjat tartalmaz.")
                .receiptGreet("Köszönjük, hogy a vendégünk volt!")
                .socialMediaInfo("facebook.com/gameuppub")
                .webSite("http://www.gameup.hu")
                .receiptDisclaimer("Nem adóügyi bizonylat.\n Készpénz átvételére nem jogosít.")
                .phoneNumber("+36 30/287-87-66")
                .serviceFeePercent(8)
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
                .closureTime(now().minusDays(2))
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

    private void buildOpenDailyClosure() {
        openDailyClosure = DailyClosure.builder()
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

                .sumSaleGrossPriceCash(5600)
                .sumSaleGrossPriceCreditCard(2000)
                .sumSaleGrossPriceCoupon(0)
                .sumSaleGrossPriceTotal(7600)
                .profit(0)
                .markup(0)
                .receiptAverage(0)
                .numberOfReceipts(0)
                .discount(0)
                .build();
    }

    private void buildProductOne() {
        this.productOne = Product.builder()
                .longName(PRODUCT_ONE_LONG_NAME)
                .shortName(PRODUCT_ONE_SHORT_NAME)
                .orderNumber(1)
                .salePrice(440)
                .purchasePrice(500)
                .rapidCode(11)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .VATLocal(vatNormal)
                .VATTakeAway(vatNormal)
                .build();
    }

    private void buildProductTwo() {
        productTwo = Product.builder()
                .longName("productTwo")
                .shortName("product2")
                .orderNumber(2)
                .salePrice(200)
                .purchasePrice(100)
                .rapidCode(12)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.LITER)
                .storageMultiplier(50)
                .type(ProductType.SELLABLE)
                .VATLocal(vatNormal)
                .VATTakeAway(vatNormal)
                .build();
    }

    private void buildProductThree() {
        productThree = Product.builder()
                .longName("productThree")
                .shortName("product3")
                .orderNumber(3)
                .salePrice(2900)
                .purchasePrice(400)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.GRAM)
                .storageMultiplier(1000)
                .type(ProductType.PARTIALLY_PAYABLE)
                .VATLocal(vatGreatlyReduced)
                .VATTakeAway(vatGreatlyReduced)
                .build();
    }

    private void buildProductFour() {
        productFour = Product.builder()
                .longName("productFour")
                .shortName("product4")
                .orderNumber(4)
                .salePrice(990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.GRAM)
                .storageMultiplier(2000)
                .type(ProductType.SELLABLE)
                .VATLocal(vatGreatlyReduced)
                .VATTakeAway(vatGreatlyReduced)
                .build();
    }

    private void buildProductFive() {
        productFive = Product.builder()
                .longName("productFive")
                .shortName("product5")
                .orderNumber(5)
                .salePrice(440)
                .purchasePrice(450)
                .rapidCode(13)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.LITER)
                .storageMultiplier(2)
                .type(ProductType.SELLABLE)
                .VATLocal(vatNormal)
                .VATTakeAway(vatNormal)
                .build();
    }

    private void buildProductSix() {
        productSix = Product.builder()
                .longName("productSix")
                .shortName("product6")
                .orderNumber(6)
                .salePrice(480)
                .purchasePrice(200)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.PARTIALLY_PAYABLE)
                .VATLocal(vatNormal)
                .VATTakeAway(vatNormal)
                .build();
    }


    private void buildProductSeven() {
        productSeven = Product.builder()
                .longName("productSeven")
                .shortName("product7")
                .orderNumber(7)
                .salePrice(780)
                .purchasePrice(400)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .VATLocal(vatNormal)
                .VATTakeAway(vatNormal)
                .build();
    }

    private void buildProductEight() {
        productEight = Product.builder()
                .longName("productEight")
                .shortName("product8")
                .orderNumber(8)
                .salePrice(500)
                .purchasePrice(200)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .VATLocal(vatNormal)
                .VATTakeAway(vatNormal)
                .build();
    }
    
    private void buildProductAdHoc() {
        productAdHoc = Product.builder()
                .longName("productAdHoc")
                .shortName("productAdHoc")
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.AD_HOC_PRODUCT)
                .VATLocal(vatNormal)
                .VATTakeAway(vatNormal)
                .recipes(new ArrayList<>())
                .build();
    }

    private void buildProductGameFee() {
        productGameFee = Product.builder()
                .longName("Játékdíj / Game Fee")
                .shortName("Játékdíj / Game Fee")
                .salePrice(300)
                .purchasePrice(0)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.LITER)
                .type(ProductType.GAME_FEE_PRODUCT)
                .VATLocal(vatNormal)
                .VATTakeAway(vatNormal)
                .recipes(new ArrayList<>())
                .build();
    }

    private void buildServiceFeeProduct() {
        productServiceFee = Product.builder()
                .longName("Szervízdíj / Service fee")
                .shortName("Szervízdíj / Service fee")
                .salePrice(0)
                .purchasePrice(0)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.LITER)
                .type(ProductType.SERVICE_FEE_PRODUCT)
                .VATLocal(vatNormal)
                .VATTakeAway(vatNormal)
                .recipes(new ArrayList<>())
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
                .VATLocal(vatNormal)
                .VATTakeAway(vatNormal)
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
                .VATLocal(vatNormal)
                .VATTakeAway(vatNormal)
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
                .VATLocal(vatNormal)
                .VATTakeAway(vatNormal)
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
        this.aggregateTopOne = ProductCategory.builder()
                .name(AGGREGATE_TOP_ONE_NAME)
                .orderNumber(1)
                .type(ProductCategoryType.AGGREGATE)
                .status(ProductStatus.ACTIVE)
                .family(ProductCategoryFamily.FOOD)
                .orderNumber(1)
                .build();
    }

    private void buildAggregateTopTwo() {
        aggregateTopTwo = ProductCategory.builder()
                .name("aggregateTopTwo")
                .orderNumber(2)
                .type(ProductCategoryType.AGGREGATE)
                .status(ProductStatus.ACTIVE)
                .family(ProductCategoryFamily.DRINK)
                .orderNumber(2)
                .build();
    }

    private void buildAggregateOne() {
        aggregateOne = ProductCategory.builder()
                .name(AGGREGATE_ONE_NAME)
                .orderNumber(3)
                .type(ProductCategoryType.AGGREGATE)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildAggregateTwo() {
        aggregateTwo = ProductCategory.builder()
                .name("aggregateTwo")
                .orderNumber(4)
                .type(ProductCategoryType.AGGREGATE)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildAggregateThree() {
        aggregateThree = ProductCategory.builder()
                .name("aggregateThree")
                .orderNumber(5)
                .type(ProductCategoryType.AGGREGATE)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildAggregateFour() {
        aggregateFour = ProductCategory.builder()
                .name("aggregateFour")
                .orderNumber(6)
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
        this.leafOne = ProductCategory.builder()
                .name(LEAF_ONE_NAME)
                .orderNumber(7)
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .priceModifiers(new ArrayList<>())
                .build();
    }

    private void buildLeafTwo() {
        leafTwo = ProductCategory.builder()
                .name("leafTwo")
                .orderNumber(8)
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildLeafThree() {
        leafThree = ProductCategory.builder()
                .name("leafThree")
                .orderNumber(9)
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildLeafFour() {
        leafFour = ProductCategory.builder()
                .name("leafFour")
                .orderNumber(10)
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildLeafFive() {
        leafFive = ProductCategory.builder()
                .name("leafFive")
                .orderNumber(11)
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildLeafSix() {
        leafSix = ProductCategory.builder()
                .name("leafSix")
                .orderNumber(12)
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


    private void buildPseudoSeven() {
        pseudoSeven = ProductCategory.builder()
                .name("pseudoSeven")
                .type(ProductCategoryType.PSEUDO)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildPseudoEight() {
        pseudoEight = ProductCategory.builder()
                .name("pseudoEight")
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

    private void buildPseudoServiceFee() {
        pseudoServiceFee = ProductCategory.builder()
                .name("pseudoServiceFee")
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
                .name(PRICE_MODIFIER1_NAME)
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
                .startTime(LocalTime.of(17, 0))
                .endTime(LocalTime.of(17, 59))
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
                .dayOfWeek(getDayOfWeek())
                .startDate(LocalDateTime.of(2017, 2, 8, 16, 0))
                .endDate(LocalDateTime.of(2017, 5, 8, 20, 20))
                .discountPercent(33.333)
                .quantityLimit(3)
                .build();
    }

    private DayOfWeek getDayOfWeek() {
        return LocalTime.now().isBefore(LocalTime.of(4, 0)) ?
                LocalDate.now().minusDays(1).getDayOfWeek() :
                LocalDate.now().getDayOfWeek();
    }


    private void buildPriceModifierFour() {
        priceModifierFour = PriceModifier.builder()
                .name("TestPriceModifier4")
                .type(PriceModifierType.QUANTITY_DISCOUNT)
                .repeatPeriod(PriceModifierRepeatPeriod.WEEKLY)
                .dayOfWeek(getDayOfWeek())
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

    private void buildProductSevenPartOne() {
        productSevenPartOne = Recipe.builder()
                .quantityMultiplier(2)
                .build();
    }

    private void buildProductEightPartOne() {
        productEightPartOne = Recipe.builder()
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

    private void buildReceiptTableSaleTest() {
        receiptTableSaleTest = Receipt.builder()
                .type(ReceiptType.SALE)
                .status(ReceiptStatus.OPEN)
                .paymentMethod(PaymentMethod.CASH)
                .openTime(LocalDateTime.now())
                .sumPurchaseGrossPrice(0)
                .sumPurchaseNetPrice(0)
                .sumSaleGrossPrice(0)
                .sumSaleNetPrice(0)
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

    private void buildReceiptRecordOther() {
        receiptRecordOther = ReceiptRecord.builder()
                .name("E")
                .soldQuantity(1)
                .type(ReceiptRecordType.HERE)
                .createdList(new ArrayList<>())
                .build();
    }

    private void buildReceiptRecordCreatedOther() {
        receiptRecordCreatedOther = ReceiptRecordCreated.builder()
                .created(now())
                .build();
    }

    private void buildVatSerieOne() {
        vatSerie = VATSerie.builder()
                .status(VATStatus.VALID)
                .build();
    }

    private void buildVatOne() {
        vatNormal = VAT.builder()
                .name(VATName.NORMAL)
                .status(VATStatus.VALID)
                .VAT(27)
                .cashierNumber(4)
                .serviceFeeCashierNumber(1)
                .build();
    }

    private void buildVatTwo() {
        vatReduced = VAT.builder()
                .name(VATName.REDUCED)
                .status(VATStatus.VALID)
                .VAT(18)
                .build();
    }

    private void buildVatThree() {
        vatGreatlyReduced = VAT.builder()
                .name(VATName.GREATLY_REDUCED)
                .status(VATStatus.VALID)
                .VAT(5)
                .cashierNumber(3)
                .serviceFeeCashierNumber(2)
                .build();
    }

    private void buildVatFour() {
        vatTaxTicket = VAT.builder()
                .name(VATName.TAX_TICKET)
                .status(VATStatus.VALID)
                .VAT(0)
                .build();
    }

    private void buildVatFive() {
        vatTaxFree = VAT.builder()
                .name(VATName.TAX_FREE)
                .status(VATStatus.VALID)
                .VAT(0)
                .build();
    }

    private void buildTableReservationTest() {
        tableReservationTest = Table.builder()
                .number(Integer.valueOf(RESERVATION_TEST_TABLE))
                .name("tableReservationTest")
                .type(TableType.NORMAL)
                .visible(true)
                .capacity(6)
                .guestCount(0)
                .coordinateX(500)
                .coordinateY(200)
                .reservations(new ArrayList<>())
                .build();
    }

    private void buildTableRestaurantTest() {
        tableRestaurantTest = Table.builder()
                .number(Integer.valueOf(RESTAURANT_TEST_TABLE))
                .name("tableRestaurantTest")
                .type(TableType.NORMAL)
                .visible(true)
                .capacity(6)
                .guestCount(0)
                .coordinateX(600)
                .coordinateY(200)
                .reservations(new ArrayList<>())
                .build();
    }

    private void buildTableSaleTest() {
        tableSaleTest = Table.builder()
                .number(Integer.valueOf(SALE_TEST_TABLE))
                .name("tableSaleTest")
                .type(TableType.NORMAL)
                .visible(true)
                .capacity(6)
                .guestCount(0)
                .coordinateX(600)
                .coordinateY(400)
                .reservations(new ArrayList<>())
                .build();
    }

    private void buildTablePaymentTest() {
        tablePaymentTest = Table.builder()
                .number(Integer.valueOf(PAYMENT_TEST_TABLE))
                .name("tablePaymentTest")
                .type(TableType.NORMAL)
                .visible(true)
                .capacity(6)
                .guestCount(0)
                .coordinateX(700)
                .coordinateY(400)
                .reservations(new ArrayList<>())
                .build();
    }

    private void buildTableTableTest() {
        tableTableTest = Table.builder()
                .number(Integer.valueOf(TABLE_TEST_TABLE))
                .name(TABLE_TEST_TABLE_NAME)
                .type(TableType.NORMAL)
                .visible(true)
                .capacity(6)
                .guestCount(0)
                .coordinateX(800)
                .coordinateY(400)
                .reservations(new ArrayList<>())
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

    private void productCategories() {
        rootAndAggregates();
        aggregatesAndAggregates();
        aggregatesAndLeafs();
        leafsAndPseudos();
    }

    private void rootAndAggregates() {
        root.setChildren(new ArrayList<>(
                Arrays.asList(aggregateTopOne, aggregateTopTwo, aggregateRecipeElements)));
        aggregateTopOne.setParent(root);
        aggregateTopTwo.setParent(root);
        aggregateRecipeElements.setParent(root);
    }

    private void aggregatesAndAggregates() {
        aggregateTopOne.setChildren(new ArrayList<>(
                Arrays.asList(aggregateOne, aggregateTwo, leafFive)));
        aggregateTopTwo.setChildren(new ArrayList<>(
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
        leafFive.setParent(aggregateTopOne);
        leafSix.setParent(aggregateFour);
        leafRecipeElements.setParent(aggregateRecipeElements);
        aggregateOne.setChildren(new ArrayList<>(
                Arrays.asList(leafOne, leafThree)));
        aggregateTwo.setChildren(new ArrayList<>(
                Arrays.asList(leafTwo, leafFour)));
        aggregateFour.setChildren(new ArrayList<>(
                Collections.singletonList(leafSix)));
        aggregateRecipeElements.setChildren(new ArrayList<>(
                Collections.singletonList(leafRecipeElements)));

    }
    
    private void leafsAndPseudos() {
        leafOne.setChildren(new ArrayList<>(
                Arrays.asList(pseudoOne, pseudoFive, pseudoSix, pseudoAdHoc, pseudoGameFee, pseudoServiceFee)));
        leafTwo.setChildren(new ArrayList<>(
                Arrays.asList(pseudoThree, pseudoFour)));
        leafThree.setChildren(new ArrayList<>(
                Collections.singletonList(pseudoTwo)));
        leafFive.setChildren(new ArrayList<>(
                Collections.singletonList(pseudoSeven)));
        leafSix.setChildren(new ArrayList<>(
                Collections.singletonList(pseudoEight)));

        leafRecipeElements.setChildren(new ArrayList<>(
                Arrays.asList(pseudoRecipeElementOne, pseudoRecipeElementTwo, pseudoRecipeElementThree)));
        pseudoOne.setParent(leafOne);
        pseudoTwo.setParent(leafThree);
        pseudoFive.setParent(leafOne);
        pseudoSix.setParent(leafOne);
        pseudoAdHoc.setParent(leafOne);
        pseudoGameFee.setParent(leafOne);
        pseudoServiceFee.setParent(leafOne);
        pseudoThree.setParent(leafTwo);
        pseudoFour.setParent(leafTwo);
        pseudoSeven.setParent(leafFive);
        pseudoEight.setParent(leafSix);
        pseudoRecipeElementOne.setParent(leafRecipeElements);
        pseudoRecipeElementTwo.setParent(leafRecipeElements);
        pseudoRecipeElementThree.setParent(leafRecipeElements);
    }

    private void categoriesAndPriceModifiers() {
        pseudoOne.setPriceModifiers(new ArrayList<>(
                Arrays.asList(priceModifierOne, priceModifierTwo)));
        leafTwo.setPriceModifiers(new ArrayList<>(
                Collections.singletonList(priceModifierThree)));
        pseudoTwo.setPriceModifiers(new ArrayList<>(
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

        pseudoSeven.setProduct(productSeven);
        productSeven.setCategory(pseudoSeven);

        pseudoEight.setProduct(productEight);
        productEight.setCategory(pseudoEight);

        pseudoAdHoc.setProduct(productAdHoc);
        productAdHoc.setCategory(pseudoAdHoc);

        pseudoGameFee.setProduct(productGameFee);
        productGameFee.setCategory(pseudoGameFee);

        pseudoServiceFee.setProduct(productServiceFee);
        productServiceFee.setCategory(pseudoServiceFee);

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
        productSeven.setRecipes(new HashSet<>(
                Collections.singletonList(productSevenPartOne)));
        productEight.setRecipes(new HashSet<>(
                Collections.singletonList(productEightPartOne)));
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
        productSevenPartOne.setOwner(productSeven);
        productEightPartOne.setOwner(productEight);
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
        productSevenPartOne.setComponent(productSeven);
        productEightPartOne.setComponent(productEight);
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

    private void receiptsAndReceiptRecords() {
        receiptOther.setRecords(new ArrayList<>(
                Collections.singletonList(receiptRecordOther)));
        receiptRecordOther.setOwner(receiptOther);
    }

    private void receiptRecordsAndCreated() {
        receiptRecordOther.getCreatedList().add(receiptRecordCreatedOther);
        receiptRecordCreatedOther.setOwner(receiptRecordOther);
    }
    
    private void receiptsAndVatSerie() {
        receiptTableSaleTest.setVATSerie(vatSerie);
        receiptPurchase.setVATSerie(vatSerie);
        receiptInventory.setVATSerie(vatSerie);
        receiptDisposal.setVATSerie(vatSerie);
        receiptOther.setVATSerie(vatSerie);
    }

    private void vatSerieAndVatValues() {
        vatSerie.setVat(new ArrayList<>(
                Arrays.asList(vatNormal, vatReduced, vatGreatlyReduced, vatTaxTicket, vatTaxFree)));
        vatNormal.setSerie(vatSerie);
        vatReduced.setSerie(vatSerie);
        vatGreatlyReduced.setSerie(vatSerie);
        vatTaxTicket.setSerie(vatSerie);
        vatTaxFree.setSerie(vatSerie);
    }

    private void restaurantAndTables() {
        //FIXME: Add service for building special tables in production
        restaurant.setTables(new HashSet<>(
                Arrays.asList(
                        tableReservationTest, tableRestaurantTest, tableSaleTest, tablePaymentTest, tableTableTest,
                        tablePurchase, tableInventory, tableDisposal, tableOther, tableOrphanage)));

        tableReservationTest.setOwner(restaurant);
        tableRestaurantTest.setOwner(restaurant);
        tableSaleTest.setOwner(restaurant);
        tablePaymentTest.setOwner(restaurant);
        tableTableTest.setOwner(restaurant);
        
        tablePurchase.setOwner(restaurant);
        tableInventory.setOwner(restaurant);
        tableDisposal.setOwner(restaurant);
        tableOther.setOwner(restaurant);
        tableOrphanage.setOwner(restaurant);
   }

    private void restaurantAndDailyClosures() {
        restaurant.setDailyClosures(new ArrayList<>(
                Arrays.asList(openDailyClosure, dailyClosureOne, dailyClosureTwo)));
        openDailyClosure.setOwner(restaurant);
        dailyClosureOne.setOwner(restaurant);
        dailyClosureTwo.setOwner(restaurant);
    }

    private void tablesToReceipts() {
        receiptTableSaleTest.setOwner(tableSaleTest);
        receiptPurchase.setOwner(tablePurchase);
        receiptInventory.setOwner(tableInventory);
        receiptDisposal.setOwner(tableDisposal);
        receiptOther.setOwner(tableOther);
    }

    private void receiptsToTables() {
        tableSaleTest.setReceipts(new HashSet<>(
                Collections.singletonList(receiptTableSaleTest)));
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
