package com.inspirationlogical.receipt.corelib.service.daily_closure;

import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecordCreated;
import com.inspirationlogical.receipt.corelib.model.enums.*;
import com.inspirationlogical.receipt.corelib.model.view.DailyConsumptionModel;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRowModel;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.repository.DailyClosureRepository;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRepository;
import com.inspirationlogical.receipt.corelib.repository.TableRepository;
import com.inspirationlogical.receipt.corelib.service.receipt.ReceiptService;
import com.inspirationlogical.receipt.corelib.service.vat.VATService;
import com.inspirationlogical.receipt.corelib.utility.resources.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus.CLOSED;
import static com.inspirationlogical.receipt.corelib.service.receipt.ReceiptService.getDiscountMultiplier;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class DailyConsumptionServiceImpl implements DailyConsumptionService {

    final private static Logger logger = LoggerFactory.getLogger(DailyConsumptionServiceImpl.class);

    @Autowired
    private DailyClosureService dailyClosureService;

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private ReceiptService receiptService;

    private int getTotalSalePrice(ReceiptRecord receiptRecord) {
        return (int) (receiptRecord.getSalePrice() * receiptRecord.getSoldQuantity());
    }

    @Override
    public void printAggregatedConsumption(DailyConsumptionModel dailyConsumptionModel) {
        logger.info("The aggregated consumption was printed between: " + dailyConsumptionModel.getStartTime() +
                " - " + dailyConsumptionModel.getEndTime());
        receiptService.printAggregatedReceipt(dailyConsumptionModel);
    }


    @Override
    @Transactional(propagation = Propagation.NEVER)
    public DailyConsumptionModel getDailyConsumptionModel(LocalDate startDate, LocalDate endDate) {
        DailyConsumptionModel dailyConsumptionModel = new DailyConsumptionModel();
        List<LocalDateTime> closureTimes = dailyClosureService.getClosureTimes(startDate, endDate);
        dailyConsumptionModel.setStartTime(closureTimes.get(0));
        dailyConsumptionModel.setEndTime(closureTimes.get(1));

        List<Receipt> receipts = receiptRepository.getReceiptsByClosureTime(closureTimes.get(0), closureTimes.get(1));
        setConsumptionFields(dailyConsumptionModel, receipts);
        setDiscountFields(dailyConsumptionModel, receipts);
        List<ReceiptRecordView> reducedSoldProducts = getReducedSoldProducts(receipts);
        dailyConsumptionModel.setSoldProducts(reducedSoldProducts);
        ReceiptRecordView serviceFeeRecord = getServiceFeeRecord(receipts);
        if(serviceFeeRecord != null) {
            dailyConsumptionModel.getSoldProducts().add(serviceFeeRecord);
        }
        dailyConsumptionModel.getSoldProducts().sort(Comparator.comparing(ReceiptRecordView::getSoldQuantity).reversed());
        return dailyConsumptionModel;
    }

    private ReceiptRecordView mergeEquivalentRecords(ReceiptRecordView recordA, ReceiptRecordView recordB) {
        double sumSoldQuantity = recordA.getSoldQuantity() + recordB.getSoldQuantity();
        List<LocalDateTime> created = new ArrayList<>();
        created.addAll(recordA.getCreated());
        created.addAll(recordB.getCreated());
        return ReceiptRecordView.builder()
                .id(recordA.getId())
                .name(recordA.getName())
                .type(recordA.getType())
                .soldQuantity(sumSoldQuantity)
                .purchasePrice(recordA.getPurchasePrice())
                .salePrice(recordA.getSalePrice())
                .totalPrice((int)Math.round(recordA.getSalePrice() * sumSoldQuantity))
                .discountPercent(recordA.getDiscountPercent())
                .vat(recordA.getVat())
                .created(created)
                .isPartiallyPayable(recordA.isPartiallyPayable())
                .ownerStatus(recordA.getOwnerStatus())
                .family(recordA.getFamily())
                .build();

    }

    private void setConsumptionFields(DailyConsumptionModel dailyConsumptionModel, List<Receipt> receipts) {
        dailyConsumptionModel.setOpenConsumption(getOpenConsumption());
        dailyConsumptionModel.setConsumptionCash(getConsumptionOfTheDay(receipts, PaymentMethod.CASH));
        dailyConsumptionModel.setConsumptionCreditCard(getConsumptionOfTheDay(receipts, PaymentMethod.CREDIT_CARD));
        dailyConsumptionModel.setConsumptionCoupon(getConsumptionOfTheDay(receipts, PaymentMethod.COUPON));
        dailyConsumptionModel.setTotalConsumption(dailyConsumptionModel.getOpenConsumption() +
                        dailyConsumptionModel.getConsumptionCash() +
                        dailyConsumptionModel.getConsumptionCreditCard() +
                        dailyConsumptionModel.getConsumptionCoupon());
    }

    public int getOpenConsumption() {
        List<Receipt> openReceipts = receiptRepository.getAllOpenReceipts();
        return openReceipts.stream().map(Receipt::getRecords).flatMap(Collection::stream).mapToInt(this::getTotalSalePrice).sum();
    }

    private int getConsumptionOfTheDay(List<Receipt> receipts, PaymentMethod paymentMethod) {
        int totalConsumption = (int) receipts.stream().filter(receipt -> receipt.getPaymentMethod().equals(paymentMethod))
                .map(Receipt::getRecords).flatMap(Collection::stream)
                .mapToDouble(receiptRecord -> receiptRecord.getSalePrice() * receiptRecord.getSoldQuantity())
                .sum();
        int tableDiscount = getTableDiscount(receipts, paymentMethod);
        return totalConsumption - tableDiscount;
    }

    private int getTableDiscount(List<Receipt> receipts, PaymentMethod paymentMethod) {
        return receipts.stream()
                .filter(receipt -> receipt.getPaymentMethod().equals(paymentMethod))
                .mapToInt(receipt -> receipt.getSumSaleGrossOriginalPrice() - receipt.getSumSaleGrossPrice()).sum();
    }
    
    private void setDiscountFields(DailyConsumptionModel dailyConsumptionModel, List<Receipt> receipts) {
        dailyConsumptionModel.setProductDiscount(getProductDiscount(receipts));
        dailyConsumptionModel.setTableDiscount(getTableDiscount(receipts));
        dailyConsumptionModel.setTotalDiscount(dailyConsumptionModel.getProductDiscount() + dailyConsumptionModel.getTableDiscount());
    }

    private int getProductDiscount(List<Receipt> receipts) {
        return (int) Math.round(receipts.stream().map(Receipt::getRecords).flatMap(Collection::stream)
                .mapToDouble(receiptRecord ->
                        (receiptRecord.getOriginalSalePrice() - receiptRecord.getSalePrice()) * receiptRecord.getSoldQuantity())
                .sum());
    }


    private int getTableDiscount(List<Receipt> receipts) {
        return receipts.stream()
                .mapToInt(receipt -> receipt.getSumSaleGrossOriginalPrice() - receipt.getSumSaleGrossPrice()).sum();
    }

    private List<ReceiptRecordView> getReducedSoldProducts(List<Receipt> receipts) {
        List<ReceiptRecordView> soldProducts = getSoldProducts(receipts);

        Map<String, Map<Double, Optional<ReceiptRecordView>>> reducedSoldProductsMap =
                soldProducts.stream().collect(Collectors.groupingBy(ReceiptRecordView::getName,
                        Collectors.groupingBy(ReceiptRecordView::getDiscountPercent, Collectors.reducing(this::mergeEquivalentRecords))));

        List<Optional<ReceiptRecordView>> reducedOptionalSoldPrducts =
                reducedSoldProductsMap.values().stream().map(Map::entrySet).flatMap(Collection::stream).map(Map.Entry::getValue).collect(Collectors.toList());

        return reducedOptionalSoldPrducts.stream().map(Optional::get).collect(Collectors.toList());
    }

    private List<ReceiptRecordView> getSoldProducts(List<Receipt> receipts) {
        return receipts.stream()
                .map(Receipt::getRecords)
                .flatMap(Collection::stream)
                .filter(receiptRecord -> !receiptRecord.getProduct().getType().equals(ProductType.SERVICE_FEE_PRODUCT))
                .map(ReceiptRecordView::new)
                .collect(toList());
    }

    private ReceiptRecordView getServiceFeeRecord(List<Receipt> receipts) {
        List<ReceiptRecord> serviceFees = receipts.stream()
                .map(Receipt::getRecords)
                .flatMap(Collection::stream)
                .filter(receiptRecord -> receiptRecord.getProduct().getType().equals(ProductType.SERVICE_FEE_PRODUCT))
                .collect(toList());
        if(serviceFees.isEmpty()) {
            return null;
        }
        ReceiptRecord serviceFeeRecord = serviceFees.get(0);
        int totalServiceFee = serviceFees.stream().mapToInt(ReceiptRecord::getSalePrice).sum();
        double avgServiceFee = serviceFees.stream().mapToInt(ReceiptRecord::getSalePrice).average().orElse(0D);
        return ReceiptRecordView.builder()
                .id(serviceFeeRecord.getId())
                .name(serviceFeeRecord.getName())
                .type(serviceFeeRecord.getType())
                .soldQuantity(serviceFees.size())
                .purchasePrice(serviceFeeRecord.getPurchasePrice())
                .salePrice((int) Math.round(avgServiceFee))
                .totalPrice(totalServiceFee)
                .discountPercent(serviceFeeRecord.getDiscountPercent())
                .vat(serviceFeeRecord.getVAT())
                .created(serviceFeeRecord.getCreatedList().stream().map(ReceiptRecordCreated::getCreated).collect(toList()))
                .isPartiallyPayable(false)
                .ownerStatus(CLOSED)
                .family(ProductCategoryFamily.FOOD)
                .build();
    }

    @Override
    public List<ReceiptRowModel> getReceipts(LocalDate startDate, LocalDate endDate) {
        List<LocalDateTime> closureTimes = dailyClosureService.getClosureTimes(startDate, endDate);
        List<Receipt> receipts = receiptRepository.getReceiptsByClosureTime(closureTimes.get(0), closureTimes.get(1));
        return receipts.stream()
                .map(this::buildReceiptRowModel)
                .sorted(Comparator.comparing(ReceiptRowModel::getReceiptClosureTime))
                .collect(toList());
    }

    private ReceiptRowModel buildReceiptRowModel(Receipt receipt) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        return ReceiptRowModel.builder()
                .receiptId(String.valueOf(receipt.getId()))
                .receiptTotalPrice(String.valueOf(receipt.getSumSaleGrossPrice()))
                .receiptPaymentMethod(receipt.getPaymentMethod().toI18nString())
                .receiptOpenTime(receipt.getOpenTime().format(dtf))
                .receiptClosureTime(receipt.getClosureTime().format(dtf))
                .build();
    }

    @Override
    public void updatePaymentMethod(int receiptId, PaymentMethod paymentMethod) {
        Receipt receipt = receiptRepository.findById(receiptId);
        receipt.setPaymentMethod(paymentMethod);
        receiptRepository.save(receipt);
    }

    public enum DiscountType {
        PRODUCT,
        TABLE,
        TOTAL;

        public String toI18nString() {
            if (this.equals(PRODUCT))
                return Resources.CONFIG.getString("DiscountType.Product");
            if (this.equals(TABLE))
                return Resources.CONFIG.getString("DiscountType.Table");
            if (this.equals(TOTAL))
                return Resources.CONFIG.getString("DiscountType.Total");
            return "";
        }
    }
}
