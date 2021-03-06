package com.inspirationlogical.receipt.corelib.service.receipt;

import com.inspirationlogical.receipt.corelib.model.entity.*;
import com.inspirationlogical.receipt.corelib.model.enums.*;
import com.inspirationlogical.receipt.corelib.model.view.DailyConsumptionModel;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.params.*;
import com.inspirationlogical.receipt.corelib.printing.ReceiptPrinter;
import com.inspirationlogical.receipt.corelib.repository.*;
import com.inspirationlogical.receipt.corelib.service.daily_closure.DailyClosureService;
import com.inspirationlogical.receipt.corelib.service.stock.StockService;
import com.inspirationlogical.receipt.corelib.service.vat.VATService;
import com.inspirationlogical.receipt.corelib.utility.Round;
import com.inspirationlogical.receipt.corelib.utility.RoundingLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import static com.inspirationlogical.receipt.corelib.service.receipt.ReceiptService.getDiscountMultiplier;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.*;

@Service
public class ReceiptServicePay {

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private ReceiptRecordRepository receiptRecordRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private VATService vatService;

    @Autowired
    private VATSerieRepository vatSerieRepository;

    @Autowired
    private VATRepository vatRepository;

    @Autowired
    private DailyClosureService dailyClosureService;

    @Autowired
    private StockService stockService;

    @Autowired
    private ReceiptPrinter receiptPrinter;

    public void close(Receipt receipt, PaymentParams paymentParams) {
        if (receipt.getRecords().isEmpty()) {
            deleteReceipt(receipt);
            return;
        }
        receipt.setStatus(ReceiptStatus.CLOSED);
        receipt.setClosureTime(now());
        if (paymentParams.isServiceFee()) {
            addServiceFee(receipt);
        }
        setSumValues(receipt);
        receipt.setDiscountPercent(calculateDiscount(receipt, paymentParams));
        receipt.setUserCode(paymentParams.getUserCode());
        receipt.setPaymentMethod(paymentParams.getPaymentMethod());
        applyDiscountOnSalePrices(receipt);
        dailyClosureService.update(receipt);
        stockService.increaseStock(receipt, receipt.getType());
        printReceipt(receipt, paymentParams.isDoublePrint());
        receiptRepository.save(receipt);
    }

    private void deleteReceipt(Receipt receipt) {
        receipt.getOwner().getReceipts().remove(receipt);
        receipt.setOwner(null);
        receiptRepository.delete(receipt);
    }

    private double calculateDiscount(Receipt receipt, PaymentParams paymentParams) {
        if (paymentParams.getDiscountPercent() != 0) {
            return paymentParams.getDiscountPercent();
        } else if (paymentParams.getDiscountAbsolute() != 0) {
            double discountAbs = paymentParams.getDiscountAbsolute();
            double sumSale = receipt.getSumSaleGrossPrice();
            return discountAbs / sumSale * 100;
        } else return 0;
    }

    private void applyDiscountOnSalePrices(Receipt receipt) {
        receipt.setSumSaleGrossOriginalPrice(receipt.getSumSaleGrossPrice());
        receipt.setSumSaleNetOriginalPrice(receipt.getSumSaleNetPrice());
        receipt.setSumSaleGrossPrice((int) (receipt.getSumSaleGrossPrice() * getDiscountMultiplier(receipt.getDiscountPercent())));
        receipt.setSumSaleNetPrice((int) (receipt.getSumSaleNetPrice() * getDiscountMultiplier(receipt.getDiscountPercent())));
    }

    private void addServiceFee(Receipt receipt) {
        int serviceFeeTotal = calculateTotalServiceFee(receipt.getRecords());
        ReceiptRecord serviceFeeRecord = buildServiceFeeRecord(serviceFeeTotal);
        serviceFeeRecord.getCreatedList().add(ReceiptRecordCreated.builder().created(now()).owner(serviceFeeRecord).build());
        serviceFeeRecord.setOwner(receipt);
        receipt.getRecords().add(serviceFeeRecord);
    }

    private ReceiptRecord buildServiceFeeRecord(int serviceFeeTotal) {
        Product serviceFeeProduct = productRepository.findFirstByType(ProductType.SERVICE_FEE_PRODUCT);
        return ReceiptRecord.builder()
                .product(serviceFeeProduct)
                .type(ReceiptRecordType.HERE)
                .name(serviceFeeProduct.getLongName())
                .soldQuantity(1)
                .purchasePrice(0)
                .salePrice(serviceFeeTotal)
                .originalSalePrice(serviceFeeTotal)
                .VAT(serviceFeeProduct.getVATLocal())
                .createdList(new ArrayList<>())
                .build();
    }

    private int calculateTotalServiceFee(List<ReceiptRecord> receiptRecords) {
        double serviceFeeBase = receiptRecords.stream()
                .filter(receiptRecord -> !receiptRecord.getProduct().getType().equals(ProductType.GAME_FEE_PRODUCT))
                .filter(receiptRecord -> !receiptRecord.getProduct().getType().equals(ProductType.AD_HOC_PRODUCT))
                .mapToInt(this::getReceiptRecordTotalPrice)
                .sum();
        double serviceFeePercent = restaurantRepository.findAll().get(0).getServiceFeePercent();
        return (int) Math.round(serviceFeeBase * (serviceFeePercent / 100D));
    }

    private void printReceipt(Receipt receipt, boolean doublePrint) {
        if (receipt.getType() == ReceiptType.SALE) {
            ReceiptPrintModel receiptPrintModel = buildReceiptPrintModel(receipt);
            receiptPrinter.printReceipt(receiptPrintModel);
            if (doublePrint) {
                receiptPrinter.printReceipt(receiptPrintModel);
            }
        }
    }

    private ReceiptPrintModel buildReceiptPrintModel(Receipt receipt) {
        Restaurant restaurant = receipt.getOwner().getOwner();
        ReceiptPrintModel receiptPrintModel = buildReceiptPrintModel(receipt, restaurant);
        List<ReceiptRecordPrintModel> receiptRecordPrintModels = buildReceiptRecordPrintModels(receipt);
        receiptPrintModel.getReceiptRecordsPrintModels().addAll(receiptRecordPrintModels);
        return receiptPrintModel;
    }

    private ReceiptPrintModel buildReceiptPrintModel(Receipt receipt, Restaurant restaurant) {
        int serviceFee = calculateServiceFee(receipt);
        return ReceiptPrintModel.builder()
                .restaurantName(restaurant.getRestaurantName())
                .restaurantAddress(getRestaurantAddress(restaurant.getRestaurantAddress()))
                .restaurantSocialMediaInfo(restaurant.getSocialMediaInfo())
                .restaurantWebsite(restaurant.getWebSite())
                .restaurantPhoneNumber(restaurant.getPhoneNumber())
                .receiptRecordsPrintModels(new ArrayList<>())
                .totalPriceNoServiceFee(receipt.getSumSaleGrossOriginalPrice() - serviceFee)
                .serviceFee(serviceFee)
                .serviceFeePercent(restaurant.getServiceFeePercent())
                .totalPriceWithServiceFee(receipt.getSumSaleGrossOriginalPrice())
                .totalDiscount(receipt.getSumSaleGrossOriginalPrice() - receipt.getSumSaleGrossPrice())
                .discountedTotalPrice(receipt.getSumSaleGrossPrice())
                .roundedTotalPrice(calculateRoundedTotalPrice(receipt))
                .paymentMethod(receipt.getPaymentMethod().toPrintString())
                .receiptDisclaimer(restaurant.getReceiptDisclaimer())
                .receiptNote(restaurant.getReceiptNote())
                .receiptGreet(restaurant.getReceiptGreet())
                .closureTime(receipt.getClosureTime())
                .receiptId(receipt.getId())
                .build();
    }

    private int calculateServiceFee(Receipt receipt) {
        Optional<ReceiptRecord> serviceFeeRecordOptional = receipt.getRecords().stream()
                .filter(receiptRecord -> receiptRecord.getProduct().getType().equals(ProductType.SERVICE_FEE_PRODUCT))
                .findFirst();
        return serviceFeeRecordOptional.map(ReceiptRecord::getSalePrice).orElse(0);
    }

    private int calculateRoundedTotalPrice(Receipt receipt) {
        return (int) RoundingLogic.create(receipt.getPaymentMethod()).round((double) receipt.getSumSaleGrossPrice());
    }

    private String getRestaurantAddress(Address address) {
        return address.getZIPCode() + " " + address.getCity() + ", " + address.getStreet();
    }

    private List<ReceiptRecordPrintModel> buildReceiptRecordPrintModels(Receipt receipt) {
        return receipt.getRecords().stream()
                .filter(receiptRecord -> !receiptRecord.getProduct().getType().equals(ProductType.SERVICE_FEE_PRODUCT))
                .map(this::buildReceiptRecordPrintModel).collect(Collectors.toList());
    }

    private ReceiptRecordPrintModel buildReceiptRecordPrintModel(ReceiptRecord record) {
        return ReceiptRecordPrintModel.builder()
                .productName(record.getName())
                .soldQuantity(record.getSoldQuantity())
                .productPrice(record.getSalePrice())
                .totalPrice((int) (record.getSalePrice() * record.getSoldQuantity()))
                .discount(record.getSalePrice() != record.getOriginalSalePrice())
                .build();
    }

    public void paySelective(Receipt receipt, List<ReceiptRecordView> records, PaymentParams paymentParams) {
        Receipt paidReceipt = buildReceipt(receipt);

        Map<Integer, ReceiptRecordView> recordsToPay = records.stream()
                .collect(Collectors.toMap(ReceiptRecordView::getId, Function.identity()));
        List<ReceiptRecord> notPaidRecords = receipt.getRecords().stream()
                .filter(record -> !recordsToPay.containsKey(record.getId()))
                .collect(toList());
        List<ReceiptRecord> paidRecords = receipt.getRecords().stream()
                .filter(record -> recordsToPay.containsKey(record.getId()))
                .collect(toList());
        notPaidRecords.forEach(record -> record.setOwner(receipt));
        receipt.setRecords(notPaidRecords);

        paidReceipt.setRecords(paidRecords);
        paidRecords.forEach(record -> record.setOwner(paidReceipt));
        paidReceipt.setStatus(ReceiptStatus.PENDING);
        paidReceipt.setOwner(receipt.getOwner());
        receipt.getOwner().getReceipts().add(paidReceipt);
        receiptRepository.save(receipt);
        close(paidReceipt, paymentParams);
    }

    private Receipt buildReceipt(Receipt originalReceipt) {
        return Receipt.builder()
                .type(ReceiptType.SALE)
                .status(ReceiptStatus.OPEN)
                .paymentMethod(PaymentMethod.CASH)
                .openTime(originalReceipt.getOpenTime())
                .VATSerie(vatService.findValidVATSerie())
                .records(new ArrayList<>())
                .build();
    }

    List<ReceiptRecordView> payPartial(Receipt receipt, double partialValue, PaymentParams paymentParams) {
        Receipt partialReceipt = buildReceipt(receipt);
        cloneReceiptRecords(receipt, partialReceipt, partialValue);
        receipt.getRecords().forEach(record -> record.setSoldQuantity(Round.roundToTwoDecimals(record.getSoldQuantity() * (1 - partialValue))));
        partialReceipt.setOwner(receipt.getOwner());
        receiptRepository.save(receipt);
        close(partialReceipt, paymentParams);
        ReceiptView paidReceiptView = new ReceiptView(partialReceipt);
        return paidReceiptView.getSoldProducts().stream()
                .filter(receiptRecordView -> receiptRecordView.getProductId() != productRepository.findFirstByType(ProductType.SERVICE_FEE_PRODUCT).getId())
                .collect(toList());
    }

    private void cloneReceiptRecords(Receipt receipt, Receipt partialReceipt, double partialValue) {
        receipt.getRecords().forEach(record -> {
            ReceiptRecord newRecord = ReceiptRecord.cloneReceiptRecord(record);
            newRecord.setSoldQuantity(Round.roundToTwoDecimals(newRecord.getSoldQuantity() * partialValue));
            newRecord.setOwner(partialReceipt);
            newRecord.getCreatedList().add(ReceiptRecordCreated.builder().created(now()).owner(newRecord).build());
            partialReceipt.getRecords().add(newRecord);
            newRecord.setProduct(record.getProduct());
        });
    }

    void setSumValues(ReceiptView receiptView) {
        Receipt receipt = receiptRepository.getOne(receiptView.getId());
        setSumValues(receipt);
        receiptRepository.save(receipt);
    }

    private void setSumValues(Receipt receipt) {
        receipt.setSumPurchaseGrossPrice(getSumValue(receipt, this::calculatePurchaseGrossPrice));
        receipt.setSumPurchaseNetPrice(getSumValue(receipt, this::calculatePurchaseNetPrice));
        receipt.setSumSaleGrossPrice(getSumValue(receipt, this::calculateSaleGrossPrice));
        receipt.setSumSaleNetPrice(getSumValue(receipt, this::calculateSaleNetPrice));
    }

    private int getSumValue(Receipt receipt, ToDoubleFunction<ReceiptRecord> calculator) {
        return (int) receipt.getRecords().stream().mapToDouble(calculator).sum();
    }

    private double calculatePurchaseGrossPrice(ReceiptRecord record) {
        return record.getPurchasePrice() * record.getSoldQuantity();
    }

    private double calculatePurchaseNetPrice(ReceiptRecord record) {
        return calculatePurchaseGrossPrice(record) / calculateVATDivider(record);
    }

    private double calculateSaleGrossPrice(ReceiptRecord record) {
        return record.getSalePrice() * record.getSoldQuantity();
    }

    private double calculateSaleNetPrice(ReceiptRecord record) {
        return calculateSaleGrossPrice(record) / calculateVATDivider(record);
    }

    private double calculateVATDivider(ReceiptRecord record) {
        return (100 + record.getVAT().getVAT()) / 100;
    }

    void printReceiptFromSale(int number) {
        Receipt openReceipt = receiptRepository.getOpenReceipt(number);
        ReceiptPrintModel receiptPrintModel = buildReceiptPrintModel(openReceipt);
        receiptPrintModel.setClosureTime(now());
        receiptPrintModel.setPaymentMethod("Nincs Fizetve");
        receiptPrintModel.setTotalPriceNoServiceFee(getSumValue(openReceipt, this::calculateSaleGrossPrice));
        receiptPrintModel.setServiceFee(calculateTotalServiceFee(openReceipt.getRecords()));
        receiptPrintModel.setTotalPriceWithServiceFee(receiptPrintModel.getTotalPriceNoServiceFee() + receiptPrintModel.getServiceFee());
        receiptPrintModel.setReceiptNote("");
        receiptPrinter.printReceipt(receiptPrintModel);
    }

    void printAggregatedReceipt(DailyConsumptionModel dailyConsumptionModel) {
        Restaurant restaurant = restaurantRepository.findAll().get(0);
        ReceiptPrintModel receiptPrintModel = buildReceiptPrintModelForDailyConsumption(dailyConsumptionModel, restaurant);
        List<ReceiptRecordPrintModel> receiptRecordPrintModels = dailyConsumptionModel.getSoldProducts().stream()
                .map(this::buildReceiptRecordPrintModel)
                .sorted(Comparator.comparing(ReceiptRecordPrintModel::getSoldQuantity).reversed())
                .collect(Collectors.toList());
        receiptPrintModel.getReceiptRecordsPrintModels().addAll(receiptRecordPrintModels);

        receiptPrintModel.setClosureTime(now());
        receiptPrintModel.setPaymentMethod("Napi Összesítő");
        receiptPrintModel.setReceiptNote("");
        receiptPrinter.printAggregatedReceipt(receiptPrintModel);
    }

    private ReceiptPrintModel buildReceiptPrintModelForDailyConsumption(DailyConsumptionModel dailyConsumptionModel, Restaurant restaurant) {
        return ReceiptPrintModel.builder()
                .restaurantName(restaurant.getRestaurantName())
                .restaurantAddress(getRestaurantAddress(restaurant.getRestaurantAddress()))
                .restaurantSocialMediaInfo(restaurant.getSocialMediaInfo())
                .restaurantWebsite(restaurant.getWebSite())
                .restaurantPhoneNumber(restaurant.getPhoneNumber())
                .receiptRecordsPrintModels(new ArrayList<>())
                .totalPriceNoServiceFee(dailyConsumptionModel.getTotalConsumption())
                .serviceFeePercent(0)

                .consumptionCash(dailyConsumptionModel.getConsumptionCash())
                .serviceFeeCash(dailyConsumptionModel.getServiceFeeCash())
                .consumptionCreditCard(dailyConsumptionModel.getConsumptionCreditCard())
                .serviceFeeCreditCard(dailyConsumptionModel.getServiceFeeCreditCard())
                .consumptionCoupon(dailyConsumptionModel.getConsumptionCoupon())
                .serviceFeeCoupon(dailyConsumptionModel.getServiceFeeCoupon())
                .netServiceFee(dailyConsumptionModel.getServiceFeeNetTotal())
                .openConsumption(dailyConsumptionModel.getOpenConsumption())
                .serviceFee(dailyConsumptionModel.getServiceFeeTotal())
                .totalConsumption(dailyConsumptionModel.getTotalConsumption() - dailyConsumptionModel.getOpenConsumption())

                .serviceFeeOver(dailyConsumptionModel.getServiceFeeOver())
                .creditCardOver(dailyConsumptionModel.getCreditCardOver())
                .envelope(getEnvelope(dailyConsumptionModel))

                .productDiscount(dailyConsumptionModel.getProductDiscount())
                .tableDiscount(dailyConsumptionModel.getTableDiscount())
                .totalDiscount(dailyConsumptionModel.getTotalDiscount())
                .receiptDisclaimer(restaurant.getReceiptDisclaimer())
                .receiptNote(restaurant.getReceiptNote())
                .receiptGreet(restaurant.getReceiptGreet())
                .closureTime(dailyConsumptionModel.getEndTime())
                .receiptId(-1)
                .build();
    }

    private int getEnvelope(DailyConsumptionModel dailyConsumptionModel) {
        return dailyConsumptionModel.getConsumptionCash() + dailyConsumptionModel.getConsumptionCoupon() +
                dailyConsumptionModel.getServiceFeeCash() + dailyConsumptionModel.getServiceFeeCoupon() +
                dailyConsumptionModel.getServiceFeeOver();
    }

    private ReceiptRecordPrintModel buildReceiptRecordPrintModel(ReceiptRecordView record) {
        return ReceiptRecordPrintModel.builder()
                .productName(record.getName())
                .soldQuantity(record.getSoldQuantity())
                .productPrice(record.getSalePrice())
                .totalPrice(record.getTotalPrice())
                .discount(record.getDiscountPercent() != 0)
                .build();
    }

    int getTotalServiceFee(int tableNumber) {
        Receipt openReceipt = receiptRepository.getOpenReceipt(tableNumber);
        if (openReceipt == null) {
            return 0;
        }
        return calculateTotalServiceFee(openReceipt.getRecords());
    }

    public int getTotalPrice(int tableNumber) {
        Receipt openReceipt = receiptRepository.getOpenReceipt(tableNumber);
        if (openReceipt == null) {
            return 0;
        }
        return openReceipt.getRecords().stream()
                .mapToInt(this::getReceiptRecordTotalPrice).sum();
    }

    private int getReceiptRecordTotalPrice(ReceiptRecord receiptRecord) {
        return (int) Math.round(receiptRecord.getSalePrice() * receiptRecord.getSoldQuantity());
    }

    List<Integer> getTotalPriceAndServiceFee(List<ReceiptRecordView> recordViewList, PaymentParams paymentParams) {
        int totalPrice = getTotalPrice(recordViewList);
        int totalServiceFee = getTotalServiceFee(recordViewList);
        if (paymentParams != null) {
            int discountAbsolute = paymentParams.getDiscountAbsolute();
            double discountPercent = paymentParams.getDiscountPercent();
            if (discountPercent != 0) {
                return applyDiscountPercentOnTotalPrices(totalPrice, totalServiceFee, discountPercent, paymentParams.isServiceFee());
            } else if (discountAbsolute != 0) {
                return applyDiscountAbsoluteOnTotalPrices(totalPrice, totalServiceFee, discountAbsolute, paymentParams.isServiceFee());
            }
            return Arrays.asList(totalPrice, paymentParams.isServiceFee() ? totalServiceFee : 0);
        }
        return Arrays.asList(totalPrice, totalServiceFee);
    }

    private int getTotalPrice(List<ReceiptRecordView> recordViewList) {
        List<Integer> receiptRecordIds = recordViewList.stream().map(ReceiptRecordView::getId).collect(toList());
        List<ReceiptRecord> receiptRecordList = receiptRecordRepository.findAllById(receiptRecordIds);
        return receiptRecordList.stream().mapToInt(this::getReceiptRecordTotalPrice).sum();
    }

    private int getTotalServiceFee(List<ReceiptRecordView> recordViewList) {
        List<Integer> receiptRecordIds = recordViewList.stream().map(ReceiptRecordView::getId).collect(toList());
        List<ReceiptRecord> receiptRecordList = receiptRecordRepository.findAllById(receiptRecordIds);
        return calculateTotalServiceFee(receiptRecordList);
    }

    private List<Integer> applyDiscountAbsoluteOnTotalPrices(int totalPrice, int totalServiceFee, int discountAbsolute, boolean serviceFee) {
        if (serviceFee) {
            double serviceFeeTotalPriceRatio = (double) totalServiceFee / ((double) totalPrice + (double) totalServiceFee);
            int serviceFeeDiscount = multiply(discountAbsolute, serviceFeeTotalPriceRatio);
            totalServiceFee = totalServiceFee - serviceFeeDiscount;
            totalPrice = totalPrice - discountAbsolute + serviceFeeDiscount;
            return Arrays.asList(totalPrice, totalServiceFee);
        } else {
            totalPrice = totalPrice - discountAbsolute;
            return Arrays.asList(totalPrice, 0);
        }
    }

    private List<Integer> applyDiscountPercentOnTotalPrices(int totalPrice, int totalServiceFee, double discountPercent, boolean serviceFee) {
        double discountMultiplier = getDiscountMultiplier(discountPercent);
        totalPrice = multiply(totalPrice, discountMultiplier);
        totalServiceFee = multiply(totalServiceFee, discountMultiplier);
        if (serviceFee) {
            return Arrays.asList(totalPrice, totalServiceFee);
        } else {
            return Arrays.asList(totalPrice, 0);
        }
    }

    private int multiply(double price, double discountMultiplier) {
        return (int) Math.round(price * discountMultiplier);
    }

    Map<VATName, VatPriceModel> getVatPriceModelMap(List<ReceiptRecordView> recordViewList, PaymentParams paymentParams) {
        List<Integer> receiptRecordIds = recordViewList.stream().map(ReceiptRecordView::getId).collect(toList());
        List<ReceiptRecord> receiptRecordList = receiptRecordRepository.findAllById(receiptRecordIds);
        Map<VATName, List<ReceiptRecord>> vatNameReceiptRecordListMap = receiptRecordList.stream()
                .collect(groupingBy(receiptRecord -> receiptRecord.getVAT().getName()));
        Map<VATName, VatPriceModel> vatPriceModelMap = vatNameReceiptRecordListMap.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, this::calculateVatPriceModel));
        vatPriceModelMap.computeIfAbsent(VATName.NORMAL, this::buildEmptyVatPriceModel);
        vatPriceModelMap.computeIfAbsent(VATName.GREATLY_REDUCED, this::buildEmptyVatPriceModel);
        if (paymentParams != null) {
            int discountAbsolute = paymentParams.getDiscountAbsolute();
            double discountPercent = paymentParams.getDiscountPercent();
            if (discountPercent != 0) {
                return applyDiscountPercentOnVatPriceModelMap(vatPriceModelMap, discountPercent, paymentParams.isServiceFee());
            } else if (discountAbsolute != 0) {
                return applyDiscountAbsoluteOnVatPriceModelMap(vatPriceModelMap, discountAbsolute, paymentParams.isServiceFee());
            } else if (!paymentParams.isServiceFee()) {
                clearServiceFee(vatPriceModelMap);
            }
        }
        return vatPriceModelMap;
    }

    private VatPriceModel calculateVatPriceModel(Map.Entry<VATName, List<ReceiptRecord>> entry) {
        VATName vatName = entry.getKey();
        List<ReceiptRecord> receiptRecordList = entry.getValue();
        int price = receiptRecordList.stream().mapToInt(this::getReceiptRecordTotalPrice).sum();
        int serviceFee = calculateTotalServiceFee(receiptRecordList);
        return VatPriceModel.builder()
                .vatPercent(vatRepository.getVatByName(vatName).getVAT())
                .price(price)
                .serviceFee(serviceFee)
                .totalPrice(price + serviceFee)
                .build();
    }

    private VatPriceModel buildEmptyVatPriceModel(VATName vatName) {
        return VatPriceModel.builder()
                .vatPercent(vatRepository.getVatByName(vatName).getVAT())
                .price(0)
                .serviceFee(0)
                .totalPrice(0)
                .build();
    }

    private Map<VATName, VatPriceModel> applyDiscountPercentOnVatPriceModelMap(Map<VATName, VatPriceModel> vatPriceModelMap, double discountPercent, boolean serviceFee) {
        VatPriceModel normalVatPriceModel = vatPriceModelMap.get(VATName.NORMAL);
        VatPriceModel greatlyReducesVatPriceModel = vatPriceModelMap.get(VATName.GREATLY_REDUCED);
        double discountMultiplier = getDiscountMultiplier(discountPercent);
        applyDiscountPercentOnVatPriceModel(serviceFee, normalVatPriceModel, discountMultiplier);
        applyDiscountPercentOnVatPriceModel(serviceFee, greatlyReducesVatPriceModel, discountMultiplier);
        return vatPriceModelMap;
    }

    private void applyDiscountPercentOnVatPriceModel(boolean serviceFee, VatPriceModel vatPriceModel, double discountMultiplier) {
        vatPriceModel.setPrice(multiply(vatPriceModel.getPrice(), discountMultiplier));
        vatPriceModel.setServiceFee(serviceFee ? multiply(vatPriceModel.getServiceFee(), discountMultiplier) : 0);
        vatPriceModel.setTotalPrice(vatPriceModel.getPrice() + vatPriceModel.getServiceFee());
    }

    private Map<VATName, VatPriceModel> applyDiscountAbsoluteOnVatPriceModelMap(Map<VATName, VatPriceModel> vatPriceModelMap, int discountAbsolute, boolean serviceFee) {
        VatPriceModel normalVatPriceModel = vatPriceModelMap.get(VATName.NORMAL);
        VatPriceModel greatlyReducesVatPriceModel = vatPriceModelMap.get(VATName.GREATLY_REDUCED);
        if (!serviceFee) {
            clearServiceFee(vatPriceModelMap);
        }
        if (normalVatPriceModel.getTotalPrice() == 0) {
            applyDiscountAbsoluteOnVatPriceModel(discountAbsolute, greatlyReducesVatPriceModel);
        } else if (greatlyReducesVatPriceModel.getTotalPrice() == 0) {
            applyDiscountAbsoluteOnVatPriceModel(discountAbsolute, normalVatPriceModel);
        } else {
            double normalGreatlyReducesVatRatio = (double) normalVatPriceModel.getTotalPrice() /
                    ((double) normalVatPriceModel.getTotalPrice() + (double) greatlyReducesVatPriceModel.getTotalPrice());
            int normalDiscountAbsolute = multiply(discountAbsolute, normalGreatlyReducesVatRatio);
            int greatlyReducedDiscountAbsolute = discountAbsolute - normalDiscountAbsolute;
            applyDiscountAbsoluteOnVatPriceModel(greatlyReducedDiscountAbsolute, greatlyReducesVatPriceModel);
            applyDiscountAbsoluteOnVatPriceModel(normalDiscountAbsolute, normalVatPriceModel);
        }
        return vatPriceModelMap;
    }

    private void applyDiscountAbsoluteOnVatPriceModel(int discountAbsolute, VatPriceModel vatPriceModel) {
        double serviceFeePriceRatio = (double) vatPriceModel.getServiceFee() / (double) vatPriceModel.getTotalPrice();
        int serviceFeeDiscount = multiply(discountAbsolute, serviceFeePriceRatio);
        vatPriceModel.setServiceFee(vatPriceModel.getServiceFee() - serviceFeeDiscount);
        vatPriceModel.setPrice(vatPriceModel.getPrice() - discountAbsolute + serviceFeeDiscount);
        vatPriceModel.setTotalPrice(vatPriceModel.getPrice() + vatPriceModel.getServiceFee());
    }

    private void clearServiceFee(Map<VATName, VatPriceModel> vatPriceModelMap) {
        VatPriceModel normalVatPriceModel = vatPriceModelMap.get(VATName.NORMAL);
        VatPriceModel greatlyReducesVatPriceModel = vatPriceModelMap.get(VATName.GREATLY_REDUCED);
        normalVatPriceModel.setServiceFee(0);
        normalVatPriceModel.setTotalPrice(normalVatPriceModel.getPrice());
        greatlyReducesVatPriceModel.setServiceFee(0);
        greatlyReducesVatPriceModel.setTotalPrice(greatlyReducesVatPriceModel.getPrice());
    }

    @Transactional
    VatCashierNumberModel getVatCashierNumberModel() {
        VATSerie validVatSerie = vatSerieRepository.findFirstByStatus(VATStatus.VALID);
        VAT drinkVat = getValidVat(validVatSerie, VATName.NORMAL);
        VAT foodVat = getValidVat(validVatSerie, VATName.GREATLY_REDUCED);
        return VatCashierNumberModel.builder()
                .vatDrinkCashierNumber(drinkVat.getCashierNumber())
                .vatDrinkServiceFeeCashierNumber(drinkVat.getServiceFeeCashierNumber())
                .vatFoodCashierNumber(foodVat.getCashierNumber())
                .vatFoodServiceFeeCashierNumber(foodVat.getServiceFeeCashierNumber())
                .build();
    }

    private VAT getValidVat(VATSerie validVatSerie, VATName vatName) {
        return validVatSerie.getVat().stream()
                .filter(vat -> vat.getName() == vatName)
                .findFirst().orElseThrow(() -> new RuntimeException("Missing VAT"));
    }
}
