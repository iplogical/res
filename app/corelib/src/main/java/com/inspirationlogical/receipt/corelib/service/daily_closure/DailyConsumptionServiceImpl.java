package com.inspirationlogical.receipt.corelib.service.daily_closure;

import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.enums.*;
import com.inspirationlogical.receipt.corelib.model.listeners.ReceiptPrinter;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptViewImpl;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRepository;
import com.inspirationlogical.receipt.corelib.repository.TableRepository;
import com.inspirationlogical.receipt.corelib.service.vat.VATService;
import com.inspirationlogical.receipt.corelib.utility.resources.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.inspirationlogical.receipt.corelib.service.receipt.ReceiptService.getDiscountMultiplier;
import static java.time.LocalDateTime.now;

@Service
public class DailyConsumptionServiceImpl implements DailyConsumptionService {

    @Autowired
    private DailyClosureService dailyClosureRepository;

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private VATService vatService;

    @Override
    public void printAggregatedConsumption(LocalDate startTime, LocalDate endTime) {
        List<LocalDateTime> closureTimes = dailyClosureRepository.getClosureTimes(startTime, endTime);
        Receipt aggregatedReceipt = createReceiptOfAggregatedConsumption(closureTimes.get(0), closureTimes.get(1));
        new ReceiptPrinter().onClose(aggregatedReceipt);
    }

    @Override
    public ReceiptView getAggregatedReceipt(LocalDate startTime, LocalDate endTime) {
        List<LocalDateTime> closureTimes = dailyClosureRepository.getClosureTimes(startTime, endTime);
        Receipt aggregatedReceipt = createReceiptOfAggregatedConsumption(closureTimes.get(0), closureTimes.get(1));
        return new ReceiptViewImpl(aggregatedReceipt);
    }

    Receipt createReceiptOfAggregatedConsumption(LocalDateTime startTime, LocalDateTime endTime) {
        List<Receipt> receipts = receiptRepository.getReceiptsByClosureTime(startTime, endTime);
        Receipt aggregatedReceipt = buildAggregatedReceipt(startTime);
        Map<PaymentMethod, Integer> incomesByPaymentMethod = initIncomes();
        Map<DiscountType, Integer> discountsByType = initDiscounts();
        receipts.forEach(receipt -> {
            updateIncomes(receipt, incomesByPaymentMethod);
            updateTableDiscount(receipt, discountsByType);
            receipt.getRecords().forEach(receiptRecord -> {
                updateProductDiscount(receiptRecord, discountsByType);
                receiptRecord.setDiscountPercent(receipt.getDiscountPercent() + receiptRecord.getDiscountPercent());
                receiptRecord.setSalePrice((int)(receiptRecord.getSalePrice() * getDiscountMultiplier(receiptRecord.getDiscountPercent())));
                mergeReceiptRecords(aggregatedReceipt, receiptRecord);
            });
        });
        aggregatedReceipt.getRecords().sort(Comparator.comparing(ReceiptRecord::getSoldQuantity).reversed());
        addIncomesAsReceiptRecord(aggregatedReceipt, incomesByPaymentMethod);
        addDiscountsAsReceiptRecord(aggregatedReceipt, discountsByType);
        return aggregatedReceipt;
    }

    private void updateProductDiscount(ReceiptRecord receiptRecord, Map<DiscountType, Integer> discountsByType) {
        int discount = (int)((receiptRecord.getOriginalSalePrice() - receiptRecord.getSalePrice()) * receiptRecord.getSoldQuantity());
        discountsByType.put(DiscountType.PRODUCT, discount + discountsByType.get(DiscountType.PRODUCT));
    }

    private Receipt buildAggregatedReceipt(LocalDateTime startTime) {
        Receipt receipt = Receipt.builder()
                .records(new ArrayList<>())
                .openTime(startTime)
                .closureTime(now())
                .owner(tableRepository.findAllByType(TableType.ORPHANAGE).get(0))
                .paymentMethod(PaymentMethod.CASH)
                .status(ReceiptStatus.OPEN)
                .VATSerie(vatService.findValidVATSerie())
                .type(ReceiptType.SALE)
                .paymentMethod(PaymentMethod.CASH)
                .build();
        receipt.setId(-1L);
        return receipt;
    }

    private Map<PaymentMethod, Integer> initIncomes() {
        Map<PaymentMethod, Integer> salePrices = new HashMap<>();
        salePrices.put(PaymentMethod.CASH, 0);
        salePrices.put(PaymentMethod.CREDIT_CARD, 0);
        salePrices.put(PaymentMethod.COUPON, 0);
        return salePrices;
    }

    private Map<DiscountType,Integer> initDiscounts() {
        Map<DiscountType, Integer> discounts = new HashMap<>();
        discounts.put(DiscountType.PRODUCT, 0);
        discounts.put(DiscountType.TABLE, 0);
        return discounts;
    }

    private void updateIncomes(Receipt receipt, Map<PaymentMethod, Integer> incomes) {
        int salePrice = incomes.get(receipt.getPaymentMethod());
        incomes.put(receipt.getPaymentMethod(), salePrice + receipt.getSumSaleGrossPrice());
    }

    private void updateTableDiscount(Receipt receipt, Map<DiscountType, Integer> discountsByType) {
        int discount = receipt.getSumSaleGrossOriginalPrice() - receipt.getSumSaleGrossPrice();
        discountsByType.put(DiscountType.TABLE, discount + discountsByType.get(DiscountType.TABLE));
    }

    private void mergeReceiptRecords(Receipt aggregatedReceipt, ReceiptRecord receiptRecord) {
        List<ReceiptRecord> aggregatedRecords = aggregatedReceipt.getRecords().stream()
                .filter(sameNameAndDiscount(receiptRecord))
                .peek(aggregatedRecord -> aggregatedRecord.setSoldQuantity(aggregatedRecord.getSoldQuantity() + receiptRecord.getSoldQuantity()))
                .collect(Collectors.toList());
        if (aggregatedRecords.isEmpty()) {
            aggregatedReceipt.getRecords().add(buildReceiptRecord(receiptRecord));
        }
    }

    private Predicate<ReceiptRecord> sameNameAndDiscount(ReceiptRecord receiptRecord) {
        return aggregatedRecord -> aggregatedRecord.getName().equals(receiptRecord.getName()) &&
                aggregatedRecord.getDiscountPercent() == receiptRecord.getDiscountPercent();
    }

    private ReceiptRecord buildReceiptRecord(ReceiptRecord receiptRecord) {
        ReceiptRecord cloneRecord =  ReceiptRecord.builder()
                .owner(receiptRecord.getOwner())
                .createdList(new ArrayList<>(receiptRecord.getCreatedList()))
                .product(receiptRecord.getProduct())
                .type(receiptRecord.getType())
                .name(receiptRecord.getName())
                .soldQuantity(receiptRecord.getSoldQuantity())
                .purchasePrice(receiptRecord.getPurchasePrice())
                .salePrice(receiptRecord.getSalePrice())
                .VAT(receiptRecord.getVAT())
                .discountPercent(receiptRecord.getDiscountPercent())
                .build();
        cloneRecord.setId(receiptRecord.getId());
        cloneRecord.setVersion(receiptRecord.getVersion());
        return cloneRecord;
    }

    private void addIncomesAsReceiptRecord(Receipt aggregatedReceipt, Map<PaymentMethod, Integer> salePrices) {
        aggregatedReceipt.getRecords().add(buildIncomeReceiptRecord(PaymentMethod.CASH, salePrices.get(PaymentMethod.CASH)));
        aggregatedReceipt.getRecords().add(buildIncomeReceiptRecord(PaymentMethod.CREDIT_CARD, salePrices.get(PaymentMethod.CREDIT_CARD)));
        aggregatedReceipt.getRecords().add(buildIncomeReceiptRecord(PaymentMethod.COUPON, salePrices.get(PaymentMethod.COUPON)));
    }

    private ReceiptRecord buildIncomeReceiptRecord(PaymentMethod paymentMethod, int salePrice) {
        return ReceiptRecord.builder()
                .product(null)
                .createdList(new ArrayList<>())
                .type(ReceiptRecordType.HERE)
                .name(paymentMethod.toI18nString())
                .soldQuantity(0)
                .purchasePrice(0)
                .salePrice(salePrice)
                .VAT(0)
                .discountPercent(0)
                .build();
    }

    private void addDiscountsAsReceiptRecord(Receipt aggregatedReceipt, Map<DiscountType, Integer> totalDiscounts) {
        aggregatedReceipt.getRecords().add(buildDiscountReceiptRecord(DiscountType.PRODUCT, totalDiscounts.get(DiscountType.PRODUCT)));
        aggregatedReceipt.getRecords().add(buildDiscountReceiptRecord(DiscountType.TABLE, totalDiscounts.get(DiscountType.TABLE)));
        aggregatedReceipt.getRecords().add(buildDiscountReceiptRecord(DiscountType.TOTAL,
                totalDiscounts.get(DiscountType.PRODUCT) + totalDiscounts.get(DiscountType.TABLE)));
    }

    private ReceiptRecord buildDiscountReceiptRecord(DiscountType type, Integer totalDiscount) {
        return ReceiptRecord.builder()
                .product(null)
                .createdList(new ArrayList<>())
                .type(ReceiptRecordType.HERE)
                .name(type.toI18nString())
                .soldQuantity(0)
                .purchasePrice(0)
                .salePrice(totalDiscount)
                .VAT(0)
                .discountPercent(0)
                .build();
    }

    public enum DiscountType {
        PRODUCT,
        TABLE,
        TOTAL;

        public String toI18nString() {
            if(this.equals(PRODUCT))
                return Resources.CONFIG.getString("DiscountType.Product");
            if(this.equals(TABLE))
                return Resources.CONFIG.getString("DiscountType.Table");
            if(this.equals(TOTAL))
                return Resources.CONFIG.getString("DiscountType.Total");
            return "";
        }
    }
}
