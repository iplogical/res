package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.exception.RestaurantNotFoundException;
import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.entity.Restaurant;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.*;
import com.inspirationlogical.receipt.corelib.model.listeners.ReceiptPrinter;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptViewImpl;
import com.inspirationlogical.receipt.corelib.utility.Wrapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase.getDiscountMultiplier;
import static com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase.getReceiptsByClosureTime;
import static com.inspirationlogical.receipt.corelib.model.enums.TableType.canBeHosted;
import static java.time.LocalDateTime.now;

/**
 * Created by Bálint on 2017.03.13..
 */
public class RestaurantAdapter extends AbstractAdapter<Restaurant> {

    public RestaurantAdapter(Restaurant adaptee) {
        super(adaptee);
    }

    public static RestaurantAdapter getActiveRestaurant() {
        List<Restaurant> restaurantList = GuardedTransaction.runNamedQuery(Restaurant.GET_ACTIVE_RESTAURANT);
        if (restaurantList.isEmpty()) {
            throw new RestaurantNotFoundException();
        }
        return new RestaurantAdapter(restaurantList.get(0));
    }

    public TableAdapter addTable(Table.TableBuilder builder) {
        Table newTable = builder.build();
        if (isTableNumberAlreadyInUse(newTable.getNumber())) {
            if(canBeHosted(newTable.getType())) {
                newTable.setHost(TableAdapter.getTable(newTable.getNumber()).getAdaptee());
                newTable.setNumber(TableAdapter.getFirstUnusedNumber());
            } else {
                throw new IllegalTableStateException("The table number " + newTable.getNumber() + " is already in use");
            }
        }
        persistTable(newTable);
        return new TableAdapter(newTable);
    }

    public boolean isTableNumberAlreadyInUse(int tableNumber) {
        for (Table table : adaptee.getTables()) {
            if (table.getNumber() == tableNumber) {
                return true;
            }
        }
        return false;
    }

    private void persistTable(Table newTable) {
        GuardedTransaction.run(() -> {
            adaptee.getTables().add(newTable);
            newTable.setOwner(adaptee);
        });
    }

    public void mergeTables(TableAdapter consumer, List<TableAdapter> consumed) {
        openConsumerIfClosed(consumer);
        GuardedTransaction.run(() -> {
            addConsumedTablesToConsumer(consumer, consumed);
            moveConsumedRecordsToConsumer(consumer, consumed);
        });
    }

    private void openConsumerIfClosed(TableAdapter consumer) {
        if (!consumer.isTableOpen()) {
            consumer.openTable();
        }
    }

    private void addConsumedTablesToConsumer(TableAdapter consumer, List<TableAdapter> consumed) {
        consumed.forEach(consumedTableAdapter -> {
            if (consumedTableAdapter.isConsumerTable()) {
                throw new IllegalTableStateException("");
            }
            consumedTableAdapter.hideTable();
            bindConsumedToConsumer(consumer.getAdaptee(), consumedTableAdapter.getAdaptee());
        });
    }

    private void bindConsumedToConsumer(Table consumer, Table consumedTable) {
        consumedTable.setConsumer(consumer);
        consumer.getConsumed().add(consumedTable);
    }

    private void moveConsumedRecordsToConsumer(TableAdapter consumer, List<TableAdapter> consumed) {
        Receipt consumerReceipt = consumer.getOpenReceipt().getAdaptee();
        List<ReceiptRecord> consumedRecords = consumed.stream()
                .map(TableAdapter::getOpenReceipt)
                .filter(Objects::nonNull)
                .map(ReceiptAdapterBase::getAdaptee)
                .map(receipt -> {
                    List<ReceiptRecord> receiptRecords = new ArrayList<>();
                    receiptRecords.addAll(receipt.getRecords());
                    receiptRecords.forEach(receiptRecord ->
                            receiptRecord.setOwner(consumerReceipt));
                    receipt.setStatus(ReceiptStatus.CANCELED);
                    receipt.getRecords().clear();
                    return receiptRecords;
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        consumerReceipt.getRecords().addAll(consumedRecords);
    }

    public List<TableAdapter> splitTables(TableAdapter consumer) {
        List<Table> consumed = consumer.getConsumedTables();
        GuardedTransaction.run(() -> {
            consumer.getAdaptee().setConsumed(new ArrayList<>());
            consumed.forEach(table -> {
                table.setConsumer(null);
                table.setVisible(true);
            });
        });
        return consumed.stream().map(TableAdapter::new).collect(Collectors.toList());
    }

    public int getConsumptionOfTheDay(PaymentMethod paymentMethod) {
        DailyClosureAdapter openDailyClosure = DailyClosureAdapter.getOpenDailyClosure();
        if(paymentMethod == null)
            return openDailyClosure.getAdaptee().getSumSaleGrossPriceCash() + openDailyClosure.getAdaptee().getSumSaleGrossPriceCreditCard();
        if(paymentMethod.equals(PaymentMethod.CASH))
            return openDailyClosure.getAdaptee().getSumSaleGrossPriceCash();
        if(paymentMethod.equals(PaymentMethod.CREDIT_CARD))
            return openDailyClosure.getAdaptee().getSumSaleGrossPriceCreditCard();
        if(paymentMethod.equals(PaymentMethod.COUPON))
            return openDailyClosure.getAdaptee().getSumSaleGrossPriceCoupon();
        else
            return 0;
    }

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
        Wrapper<Integer> totalDiscount = new Wrapper<>();
        totalDiscount.setContent(0);
        receipts.forEach(receipt -> {
            updateIncomes(receipt, incomesByPaymentMethod);
            totalDiscount.setContent(updateDiscount(receipt, totalDiscount.getContent()));
            receipt.getAdaptee().getRecords().forEach(receiptRecord -> {
                receiptRecord.setDiscountPercent(receipt.getAdaptee().getDiscountPercent() + receiptRecord.getDiscountPercent());
                receiptRecord.setSalePrice((int)(receiptRecord.getSalePrice() * getDiscountMultiplier(receiptRecord.getDiscountPercent())));
                mergeReceiptRecords(aggregatedReceipt, receiptRecord);
            });
        });
        aggregatedReceipt.getRecords().sort(Comparator.comparing(ReceiptRecord::getSoldQuantity).reversed());
        addIncomesAsReceiptRecord(aggregatedReceipt, incomesByPaymentMethod);
        addDiscountsAsReceiptRecord(aggregatedReceipt, totalDiscount);
        receipts.forEach(receiptAdapter -> GuardedTransaction.detach(receiptAdapter.getAdaptee()));
        return aggregatedReceipt;
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

    private void updateIncomes(ReceiptAdapterBase receipt, Map<PaymentMethod, Integer> incomes) {
        int salePrice = incomes.get(receipt.getAdaptee().getPaymentMethod());
        incomes.put(receipt.getAdaptee().getPaymentMethod(), salePrice + receipt.getAdaptee().getSumSaleGrossPrice());
    }

    private int updateDiscount(ReceiptAdapterBase receipt, int totalDiscount) {
        int discount = receipt.getAdaptee().getSumSaleGrossOriginalPrice() - receipt.getAdaptee().getSumSaleGrossPrice();
        return discount + totalDiscount;
    }

    private void mergeReceiptRecords(Receipt aggregatedReceipt, ReceiptRecord receiptRecord) {
        List<ReceiptRecord> aggregatedRecords = aggregatedReceipt.getRecords().stream()
                .filter(sameNameAndDiscount(receiptRecord))
                .map(aggregatedRecord -> {
                    aggregatedRecord.setSoldQuantity(aggregatedRecord.getSoldQuantity() + receiptRecord.getSoldQuantity());
                    return aggregatedRecord;
                }).collect(Collectors.toList());
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
        for(PaymentMethod m : salePrices.keySet()) {
            aggregatedReceipt.getRecords().add(buildIncomeReceiptRecord(salePrices, m));
        }
    }

    private ReceiptRecord buildIncomeReceiptRecord(Map<PaymentMethod, Integer> salePrices, PaymentMethod paymentMethod) {
        return ReceiptRecord.builder()
                .product(null)
                .createdList(new ArrayList<>())
                .type(ReceiptRecordType.HERE)
                .name(paymentMethod.toString())
                .soldQuantity(0)
                .purchasePrice(0)
                .salePrice(salePrices.get(paymentMethod))
                .VAT(0)
                .discountPercent(0)
                .build();
    }

    private void addDiscountsAsReceiptRecord(Receipt aggregatedReceipt, Wrapper<Integer> totalDiscount) {
        aggregatedReceipt.getRecords().add(buildDiscountReceiptRecord(totalDiscount));
    }

    private ReceiptRecord buildDiscountReceiptRecord(Wrapper<Integer> totalDiscount) {
        return ReceiptRecord.builder()
                .product(null)
                .createdList(new ArrayList<>())
                .type(ReceiptRecordType.HERE)
                .name("DISCOUNT")
                .soldQuantity(0)
                .purchasePrice(0)
                .salePrice(totalDiscount.getContent())
                .VAT(0)
                .discountPercent(0)
                .build();
    }

}
