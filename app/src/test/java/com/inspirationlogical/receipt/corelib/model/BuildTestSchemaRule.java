package com.inspirationlogical.receipt.corelib.model;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashSet;
import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.corelib.model.adapter.EntityManagerProvider;
import com.inspirationlogical.receipt.corelib.model.enums.*;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

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
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

import lombok.Getter;

public class BuildTestSchemaRule implements TestRule {

    private @Getter EntityManager entityManager;
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

    private @Getter PriceModifier priceModifierOne;
    private @Getter PriceModifier priceModifierTwo;
    private @Getter PriceModifier priceModifierThree;

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
    private @Getter
    ReceiptRecord receiptRecordSaleTwo;
    private @Getter ReceiptRecord receiptRecordSaleThree;
    private @Getter ReceiptRecord receiptRecordSaleFour;
    private @Getter ReceiptRecord receiptRecordOther;

    private @Getter Table tableNormal;
    private @Getter Table tableNormalClosed;
    private @Getter Table tableVirtual;
    private @Getter Table tablePurchase;
    private @Getter Table tableInventory;
    private @Getter Table tableDisposal;
    private @Getter
    Table tableOther;

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
        GuardedTransaction.Run(() -> {
            entityManager.persist(restaurant);});
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


    private void buildPriceModifiers() {
        buildPriceModifierOne();
        buildPriceModifierTwo();
        buildPriceModifierThree();
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

    private void buildReceiptRecords() {
        buildReceiptRecordSaleOne();
        buildReceiptRecordSaleTwo();
        buildReceiptRecordSaleThree();
        buildReceiptRecordSaleFour();
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


    private void buildPriceModifierOne() {
        priceModifierOne = PriceModifier.builder()
                .name("TestPriceModifier1")
                .type(PriceModifierType.FUTURE_PRICE_MODIFICATION)
                .status(PriceModifierStatus.PAST)
                .period(PriceModifierRepeatPeriod.NO_REPETITION)
                .startTime(new GregorianCalendar(2017, 1, 8, 16, 00))
                .endTime(new GregorianCalendar(2017, 1, 8, 20, 20))
                .limitType(PriceModifierLimitType.NONE)
                .build();
    }

    private void buildPriceModifierTwo() {
        priceModifierTwo = PriceModifier.builder()
                .name("TestPriceModifier2")
                .type(PriceModifierType.SIMPLE_DISCOUNT)
                .status(PriceModifierStatus.ACTUAL)
                .period(PriceModifierRepeatPeriod.DAILY)
                .startTime(new GregorianCalendar(2017, 2, 8, 16, 00))
                .endTime(new GregorianCalendar(2017, 3, 8, 20, 20))
                .limitType(PriceModifierLimitType.EXACT)
                .build();
    }

    private void buildPriceModifierThree() {
        priceModifierThree = PriceModifier.builder()
                .name("TestPriceModifier3")
                .type(PriceModifierType.QUANTITY_DISCOUNT)
                .status(PriceModifierStatus.FUTURE)
                .period(PriceModifierRepeatPeriod.WEEKLY)
                .startTime(new GregorianCalendar(2017, 3, 8, 16, 00))
                .endTime(new GregorianCalendar(2017, 5, 8, 20, 20))
                .limitType(PriceModifierLimitType.EXACT)
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
                .build();
    }

    private void buildReceiptRecordSaleThree() {
        receiptRecordSaleThree = ReceiptRecord.builder()
                .name("C")
                .soldQuantity(1D)
                .type(ReceiptRecordType.HERE)
                .build();
    }

    private void buildReceiptRecordSaleFour() {
        receiptRecordSaleFour = ReceiptRecord.builder()
                .name("D")
                .soldQuantity(0.5)
                .type(ReceiptRecordType.HERE)
                .build();
    }


    private void buildReceiptRecordOther() {
        receiptRecordOther = ReceiptRecord.builder()
                .name("E")
                .soldQuantity(1)
                .type(ReceiptRecordType.HERE)
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

    private void buildTableOther() {
        tableOther = Table.builder()
                .number(1003)
                .type(TableType.OTHER)
                .build();
       }

    private void buildReservationOne() {
        reservationOne = Reservation.builder()
                .tableNumber(2)
                .startTime(new GregorianCalendar(2017, 2, 8, 16, 00))
                .endTime(new GregorianCalendar(2017, 2, 8, 20, 20))
                .name("TestName1")
                .note("TestNote1")
                .build();
    }

    private void buildReservationTwo() {
        reservationTwo = Reservation.builder()
                .tableNumber(2)
                .startTime(new GregorianCalendar(2017, 2, 12, 16, 00))
                .endTime(new GregorianCalendar(2017, 2, 12, 20, 20))
                .name("TestName2")
                .note("TestNote2")
                .build();
    }

    private void productCategories() {
        rootAndAggregates();
        aggregatesAndLeafs();
        leafsAndPseudos();
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

    private void categoriesAndPriceModifiers() {
        pseudoOne.setPriceModifier(new HashSet<PriceModifier>(
                Arrays.asList(priceModifierOne, priceModifierTwo)));
        leafTwo.setPriceModifier(new HashSet<PriceModifier>(
                Arrays.asList(priceModifierThree)));
        priceModifierOne.setOwner(pseudoOne);
        priceModifierTwo.setOwner(pseudoOne);
        priceModifierThree.setOwner(leafTwo);
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

    private void tablesAndReservations() {
        tableNormal.setReservation(new HashSet<Reservation>(
                Arrays.asList(reservationOne, reservationTwo)));
        reservationOne.setOwner(tableNormal);
        reservationTwo.setOwner(tableNormal);
    }

    private void receiptsAndReceiptRecords() {
        receiptSaleOne.setRecords(new HashSet<ReceiptRecord>(
                Arrays.asList(receiptRecordSaleOne, receiptRecordSaleTwo)));
        receiptSaleTwo.setRecords(new HashSet<ReceiptRecord>(
                Arrays.asList(receiptRecordSaleThree, receiptRecordSaleFour)));
        receiptOther.setRecords(new HashSet<ReceiptRecord>(
                Arrays.asList(receiptRecordOther)));
        receiptRecordSaleOne.setOwner(receiptSaleOne);
        receiptRecordSaleTwo.setOwner(receiptSaleOne);
        receiptRecordSaleThree.setOwner(receiptSaleTwo);
        receiptRecordSaleFour.setOwner(receiptSaleTwo);
        receiptRecordOther.setOwner(receiptOther);
        
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
                Arrays.asList(tableNormal, tableNormalClosed, tableVirtual, tablePurchase,
                        tableInventory, tableDisposal, tableOther)));
        tableNormal.setOwner(restaurant);
        tableNormalClosed.setOwner(restaurant);
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
