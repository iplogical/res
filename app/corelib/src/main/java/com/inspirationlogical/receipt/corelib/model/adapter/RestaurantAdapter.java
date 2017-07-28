package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.exception.RestaurantNotFoundException;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.entity.Restaurant;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.entity.Table.TableBuilder;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.listeners.ReceiptPrinter;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.utility.Wrapper;

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
        if (isTableNumberAlreadyInUse(newTable.getNumber(), newTable.getType())) {
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


    public boolean isTableNumberAlreadyInUse(int tableNumber, TableType tableType) {
        for (Table table : adaptee.getTables()) {
            if (table.getNumber() == tableNumber) {
                return true;
            }
        }
        return false;
    }

    public void mergeTables(TableAdapter consumer, List<TableAdapter> consumed) {
        if (!consumer.isTableOpen() && consumed.stream().anyMatch(TableAdapter::isTableOpen)) {
            consumer.openTable();
        }
        // todo refactor this method into smaller ones
        GuardedTransaction.run(() -> {
            List<ReceiptRecord> consumedRecords = consumed.stream()
                    .map(TableAdapter::getActiveReceipt)
                    .filter(Objects::nonNull)
                    .map(receiptAdapter -> {
                        Wrapper<ReceiptAdapter> receiptAdapterWrapper = new Wrapper<>();
                        receiptAdapterWrapper.setContent(consumer.getActiveReceipt());
                        Collection<ReceiptRecordAdapter> receiptRecordAdapters = receiptAdapter.getSoldProducts();
                        receiptAdapter.getAdaptee().setStatus(ReceiptStatus.CANCELED);
                        receiptAdapter.getAdaptee().getRecords().clear();
                        receiptRecordAdapters.forEach(receiptRecordAdapter -> receiptRecordAdapter
                                .getAdaptee()
                                .setOwner(receiptAdapterWrapper.getContent().getAdaptee()));
                        return receiptRecordAdapters;
                    })
                    .flatMap(record -> record.stream().map(ReceiptRecordAdapter::getAdaptee))
                    .collect(Collectors.toList());

            if (consumer.isTableOpen()) {
                consumer.getActiveReceipt().getAdaptee().getRecords().addAll(consumedRecords);
            }
        });


        GuardedTransaction.run(() -> {
            consumer.getAdaptee().setConsumed(new ArrayList<>());
            consumed.forEach(adapter -> {
                Table table = adapter.getAdaptee();

                Collection<Receipt> receipts = table.getReceipts();
                if (receipts != null) {
                    receipts.forEach(receipt -> receipt.setOwner(consumer.getAdaptee()));
                    consumer.getAdaptee().getReceipts().addAll(receipts);
                    receipts.clear();
                }

                table.setConsumer(consumer.getAdaptee());
                consumer.getAdaptee().getConsumed().add(table);
                if (adapter.isTableConsumer()) {
                    for (Table t : table.getConsumed()) {
                        t.setConsumer(consumer.getAdaptee());
                        consumer.getAdaptee().getConsumed().add(t);
                    }
                }
                adapter.hideTable();
            });
        });
    }

    public List<TableAdapter> splitTables(TableAdapter consumer) {

        List<Table> consumed = consumer.getConsumedTables();

        GuardedTransaction.run(() -> {
            consumer.getConsumedTables().clear();
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
        LocalDateTime latestClosure = DailyClosureAdapter.getLatestClosureTime();
        List<ReceiptAdapter> receipts = getReceiptsByClosureTime(latestClosure);
        Receipt aggregatedReceipt = Receipt.builder()
                .openTime(latestClosure)
                .closureTime(now())
                .owner(TableAdapter.getTablesByType(TableType.ORPHANAGE).get(0).getAdaptee())
                .paymentMethod(PaymentMethod.CASH) // TODO: intorduce new payment method for this purpose
                .status(ReceiptStatus.OPEN)
                .VATSerie(VATSerieAdapter.vatSerieAdapterFactory().getAdaptee())
                .type(ReceiptType.SALE)
                .paymentMethod(PaymentMethod.CASH).build();

        aggregatedReceipt.setId(-1L);
        receipts.forEach(receipt -> {
            receipt.getAdaptee().getRecords().forEach(receiptRecord -> {
                List<ReceiptRecord> aggregatedRecords = aggregatedReceipt.getRecords().stream()
                        .filter(aggregatedRecord -> aggregatedRecord.getName().equals(receiptRecord.getName()))
                        .filter(aggregatedRecord -> aggregatedRecord.getDiscountPercent() == receiptRecord.getDiscountPercent())
                        .map(aggregatedRecord -> {
                            aggregatedRecord.setSoldQuantity(aggregatedRecord.getSoldQuantity() + receiptRecord.getSoldQuantity());
                            return aggregatedRecord;
                        }).collect(Collectors.toList());
                if (aggregatedRecords.isEmpty()) {
                    aggregatedReceipt.getRecords().add(ReceiptRecord.builder()
                            .product(receiptRecord.getProduct())
                            .type(receiptRecord.getType())
                            .name(receiptRecord.getName())
                            .soldQuantity(receiptRecord.getSoldQuantity())
                            .purchasePrice(receiptRecord.getPurchasePrice())
                            .salePrice(receiptRecord.getSalePrice())
                            .VAT(receiptRecord.getVAT())
                            .discountPercent(receiptRecord.getDiscountPercent())
                            .build());
                }
            });
        });
        aggregatedReceipt.getRecords().sort(Comparator.comparing(ReceiptRecord::getSoldQuantity).reversed());
        ReceiptAdapter adapter = new ReceiptAdapter(aggregatedReceipt);
        new ReceiptPrinter().onClose(adapter);
    }
}
