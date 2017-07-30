package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.exception.RestaurantNotFoundException;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.entity.Restaurant;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.*;
import com.inspirationlogical.receipt.corelib.model.listeners.ReceiptPrinter;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.inspirationlogical.receipt.corelib.model.adapter.ReceiptAdapter.getReceiptsByClosureTime;
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
                newTable.setHost(TableAdapter.getTableByNumber(newTable.getNumber()).getAdaptee());
                newTable.setNumber(TableAdapter.getFirstUnusedNumber());
            } else {
                throw new IllegalTableStateException("The table number " + newTable.getNumber() + " is already in use");
            }
        }
        GuardedTransaction.run(() -> {
            adaptee.getTables().add(newTable);
            newTable.setOwner(adaptee);
        });
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
                .map(ReceiptAdapter::getAdaptee)
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

    public int getConsumptionOfTheDay(Predicate<ReceiptAdapter> filter) {
        LocalDateTime previousClosure = DailyClosureAdapter.getLatestClosureTime();
        List<ReceiptAdapter> closedReceipts = getReceiptsByClosureTime(previousClosure);
        if (closedReceipts.isEmpty()) {
            return 0;
        }
        return closedReceipts.stream()
                .filter(filter)
                .mapToInt(ReceiptAdapter::getTotalPrice).sum();
    }

    public void printDailyConsumption() {
        ReceiptAdapter receiptAdapter = createReceiptOfDailyConsumption();
        new ReceiptPrinter().onClose(receiptAdapter);
    }

    ReceiptAdapter createReceiptOfDailyConsumption() {
        LocalDateTime latestClosure = DailyClosureAdapter.getLatestClosureTime();
        List<ReceiptAdapter> receipts = getReceiptsByClosureTime(latestClosure);
        Receipt aggregatedReceipt = buildAggregatedReceipt(latestClosure);
        Map<PaymentMethod, Integer> incomesByPaymentMethod = initIncomes();
        receipts.forEach(receipt -> {
            updateIncomes(receipt, incomesByPaymentMethod);
            receipt.getAdaptee().getRecords().forEach(receiptRecord -> {
                mergeReceiptRecords(aggregatedReceipt, receiptRecord);
            });
        });
        aggregatedReceipt.getRecords().sort(Comparator.comparing(ReceiptRecord::getSoldQuantity).reversed());
        addIncomesAsReceiptRecord(aggregatedReceipt, incomesByPaymentMethod);
        return new ReceiptAdapter(aggregatedReceipt);
    }

    private Receipt buildAggregatedReceipt(LocalDateTime latestClosure) {
        Receipt receipt = Receipt.builder()
                .records(new ArrayList<>())
                .openTime(latestClosure)
                .closureTime(now())
                .owner(TableAdapter.getTablesByType(TableType.ORPHANAGE).get(0).getAdaptee())
                .paymentMethod(PaymentMethod.CASH)
                .status(ReceiptStatus.OPEN)
                .VATSerie(VATSerieAdapter.vatSerieAdapterFactory().getAdaptee())
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

    private void updateIncomes(ReceiptAdapter receipt, Map<PaymentMethod, Integer> incomes) {
        int salePrice = incomes.get(receipt.getAdaptee().getPaymentMethod());
        incomes.put(receipt.getAdaptee().getPaymentMethod(), salePrice + receipt.getAdaptee().getSumSaleGrossPrice());
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
        return ReceiptRecord.builder()
                .product(receiptRecord.getProduct())
                .type(receiptRecord.getType())
                .name(receiptRecord.getName())
                .soldQuantity(receiptRecord.getSoldQuantity())
                .purchasePrice(receiptRecord.getPurchasePrice())
                .salePrice(receiptRecord.getSalePrice())
                .VAT(receiptRecord.getVAT())
                .discountPercent(receiptRecord.getDiscountPercent())
                .build();
    }

    private void addIncomesAsReceiptRecord(Receipt aggregatedReceipt, Map<PaymentMethod, Integer> salePrices) {
        for(PaymentMethod m : salePrices.keySet()) {
            aggregatedReceipt.getRecords().add(buildIncomeReceiptRecord(salePrices, m));
        }
    }

    private ReceiptRecord buildIncomeReceiptRecord(Map<PaymentMethod, Integer> salePrices, PaymentMethod paymentMethod) {
        return ReceiptRecord.builder()
                .product(null)
                .type(ReceiptRecordType.HERE)
                .name(paymentMethod.toString())
                .soldQuantity(0)
                .purchasePrice(0)
                .salePrice(salePrices.get(paymentMethod))
                .VAT(0)
                .discountPercent(0)
                .build();
    }
}
