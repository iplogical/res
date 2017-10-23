package com.inspirationlogical.receipt.corelib.model.adapter.restaurant;

import com.inspirationlogical.receipt.corelib.model.adapter.DailyClosureAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.TableAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.VATSerieAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.enums.*;
import com.inspirationlogical.receipt.corelib.model.listeners.ReceiptPrinter;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptViewImpl;
import com.inspirationlogical.receipt.corelib.utility.resources.Resources;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase.getDiscountMultiplier;
import static com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase.getReceiptsByClosureTime;
import static java.time.LocalDateTime.now;

public class DailyConsumptionAdapter {

    public void printAggregatedConsumption(LocalDate startTime, LocalDate endTime) {
        List<LocalDateTime> closureTimes = DailyClosureAdapter.getClosureTimes(startTime, endTime);
        Receipt aggregatedReceipt = createReceiptOfAggregatedConsumption(closureTimes.get(0), closureTimes.get(1));
        new ReceiptPrinter().onClose(aggregatedReceipt);
    }

    public ReceiptView getAggregatedReceipt(LocalDate startTime, LocalDate endTime) {
        List<LocalDateTime> closureTimes = DailyClosureAdapter.getClosureTimes(startTime, endTime);
        Receipt aggregatedReceipt = createReceiptOfAggregatedConsumption(closureTimes.get(0), closureTimes.get(1));
        return new ReceiptViewImpl(new ReceiptAdapterBase(aggregatedReceipt));
    }

    Receipt createReceiptOfAggregatedConsumption(LocalDateTime startTime, LocalDateTime endTime) {
        List<ReceiptAdapterBase> receipts = getReceiptsByClosureTime(startTime, endTime);
        Receipt aggregatedReceipt = buildAggregatedReceipt(startTime);
        Map<PaymentMethod, Integer> incomesByPaymentMethod = initIncomes();
        Map<DiscountType, Integer> discountsByType = initDiscounts();
        receipts.forEach(receipt -> {
            updateIncomes(receipt, incomesByPaymentMethod);
            updateTableDiscount(receipt, discountsByType);
            receipt.getAdaptee().getRecords().forEach(receiptRecord -> {
                updateProductDiscount(receiptRecord, discountsByType);
                receiptRecord.setDiscountPercent(receipt.getAdaptee().getDiscountPercent() + receiptRecord.getDiscountPercent());
                receiptRecord.setSalePrice((int)(receiptRecord.getSalePrice() * getDiscountMultiplier(receiptRecord.getDiscountPercent())));
                mergeReceiptRecords(aggregatedReceipt, receiptRecord);
            });
        });
        aggregatedReceipt.getRecords().sort(Comparator.comparing(ReceiptRecord::getSoldQuantity).reversed());
        addIncomesAsReceiptRecord(aggregatedReceipt, incomesByPaymentMethod);
        addDiscountsAsReceiptRecord(aggregatedReceipt, discountsByType);
        receipts.forEach(receiptAdapter -> GuardedTransaction.detach(receiptAdapter.getAdaptee()));
        return aggregatedReceipt;
    }

    private void updateProductDiscount(ReceiptRecord receiptRecord, Map<DiscountType, Integer> discountsByType) {
        int discount = receiptRecord.getOriginalSalePrice() - receiptRecord.getSalePrice();
        discountsByType.put(DiscountType.PRODUCT, discount + discountsByType.get(DiscountType.PRODUCT));
    }

    private Receipt buildAggregatedReceipt(LocalDateTime startTime) {
        Receipt receipt = Receipt.builder()
                .records(new ArrayList<>())
                .openTime(startTime)
                .closureTime(now())
                .owner(TableAdapter.getTablesByType(TableType.ORPHANAGE).get(0).getAdaptee())
                .paymentMethod(PaymentMethod.CASH)
                .status(ReceiptStatus.OPEN)
                .VATSerie(VATSerieAdapter.getActiveVATSerieAdapter().getAdaptee())
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

    private void updateIncomes(ReceiptAdapterBase receipt, Map<PaymentMethod, Integer> incomes) {
        int salePrice = incomes.get(receipt.getAdaptee().getPaymentMethod());
        incomes.put(receipt.getAdaptee().getPaymentMethod(), salePrice + receipt.getAdaptee().getSumSaleGrossPrice());
    }

    private void updateTableDiscount(ReceiptAdapterBase receipt, Map<DiscountType, Integer> discountsByType) {
        int discount = receipt.getAdaptee().getSumSaleGrossOriginalPrice() - receipt.getAdaptee().getSumSaleGrossPrice();
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

    private enum DiscountType {
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