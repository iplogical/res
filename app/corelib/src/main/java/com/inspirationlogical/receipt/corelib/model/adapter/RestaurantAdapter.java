package com.inspirationlogical.receipt.corelib.model.adapter;

import static com.inspirationlogical.receipt.corelib.model.adapter.TableAdapter.canBeHosted;
import static com.inspirationlogical.receipt.corelib.model.adapter.TableAdapter.isDisplayable;
import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.exception.RestaurantNotFoundException;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.entity.Restaurant;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.listeners.ReceiptPrinter;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.utility.Wrapper;

/**
 * Created by Bálint on 2017.03.13..
 */
public class RestaurantAdapter extends AbstractAdapter<Restaurant> {

    public static RestaurantAdapter restaurantAdapterFactory(EntityManager manager) {
        List<Restaurant> restaurantList = manager.createNamedQuery(Restaurant.GET_ACTIVE_RESTAURANT).getResultList();
        if (restaurantList.isEmpty()) {
            throw new RestaurantNotFoundException();
        }
        return new RestaurantAdapter(restaurantList.get(0));
    }

    public RestaurantAdapter(Restaurant adaptee) {
        super(adaptee);
    }


    public List<TableAdapter> getDisplayableTables() {
        GuardedTransaction.runWithRefresh(adaptee, () -> {});
        Collection<Table> tables = adaptee.getTables();
        return tables.stream()
                .filter(table -> isDisplayable(table.getType()))
                .map(TableAdapter::new)
                .collect(Collectors.toList());
    }

    public TableAdapter addTable(Table.TableBuilder builder) {
        Table newTable = builder.build();
        if (isTableNumberAlreadyInUse(newTable.getNumber(), newTable.getType())) {
            newTable.setHost(TableAdapter.getTableByNumber(newTable.getNumber()).getAdaptee());
            newTable.setNumber(TableAdapter.getFirstUnusedNumber());
        }
        GuardedTransaction.runWithRefresh(adaptee, () -> {
            adaptee.getTables().add(newTable);
            newTable.setOwner(adaptee);
        });
        return new TableAdapter(newTable);
    }

    public boolean isTableNumberAlreadyInUse(int tableNumber, TableType tableType) {
        Wrapper<Boolean> alreadyInUse = new Wrapper<>();
        alreadyInUse.setContent(false);
        GuardedTransaction.runWithRefresh(adaptee, () -> {
            for (Table table : adaptee.getTables()) {
                if (table.getNumber() == tableNumber) {
                    if (canBeHosted(tableType)) {
                        alreadyInUse.setContent(true);
                        break;
                    } else {
                        throw new IllegalTableStateException("The table number " + tableNumber + " is already in use");
                    }
                }
            }
        });
        return alreadyInUse.getContent();
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

    public int getConsumptionOfTheDay(Predicate<Receipt> filter) {
        LocalDateTime previousClosure = DailyClosureAdapter.getLatestClosureTime();
        List<Receipt> closedReceipts = getReceiptsByClosureTime(previousClosure);
        if (closedReceipts.isEmpty()) {
            return 0;
        }
        return closedReceipts.stream()
                .filter(filter)
                .map(ReceiptAdapter::new)
                .mapToInt(ReceiptAdapter::getTotalPrice).sum();
    }

    private List<Receipt> getReceiptsByClosureTime(LocalDateTime finalPreviousClosure) {
        return GuardedTransaction.runNamedQuery(Receipt.GET_RECEIPT_BY_CLOSURE_TIME_AND_TYPE,
                query -> query.setParameter("closureTime", finalPreviousClosure).setParameter("type", ReceiptType.SALE));
    }

    public void printDailyConsumption() {
        LocalDateTime latestClosure = DailyClosureAdapter.getLatestClosureTime();
        List<Receipt> receipts = GuardedTransaction.runNamedQuery(Receipt.GET_RECEIPT_BY_OPEN_TIME_AND_TYPE,
                query -> query
                        .setParameter("openTime",latestClosure)
                        .setParameter("type",ReceiptType.SALE));
        Receipt aggregatedReceipt = Receipt.builder()
                .records(new ArrayList<>()) // TODO: refactor using Lombok builder prototype pattern see @ Restaurant
                .openTime(latestClosure)
                .closureTime(now())
                .owner(TableAdapter.getTablesByType(TableType.ORPHANAGE).get(0))
                .paymentMethod(PaymentMethod.CASH) // TODO: intorduce new payment method for this purpose
                .status(ReceiptStatus.OPEN)
                .VATSerie(VATSerieAdapter.vatSerieAdapterFactory().getAdaptee())
                .type(ReceiptType.SALE)
                .paymentMethod(PaymentMethod.CASH).build();

        aggregatedReceipt.setId(-1L);
        receipts.forEach(receipt -> {
            receipt.getRecords().forEach(receiptRecord -> {
                List<ReceiptRecord> aggregatedRecords = aggregatedReceipt.getRecords().stream().filter(aggregatedRecord -> aggregatedRecord.getName().equals(receiptRecord.getName()))
                        .map(aggregatedRecord -> {
                            aggregatedRecord.setSoldQuantity(aggregatedRecord.getSoldQuantity() + receiptRecord.getSoldQuantity());
                            return aggregatedRecord;
                        }).collect(Collectors.toList());
                if (aggregatedRecords.isEmpty()) {
                    aggregatedReceipt.getRecords().add(ReceiptRecord.builder()
                            .product(receiptRecord.getProduct())
                            .type(receiptRecord.getType())
                            .created(now())
                            .name(receiptRecord.getName())
                            .soldQuantity(receiptRecord.getSoldQuantity())
                            .purchasePrice(receiptRecord.getPurchasePrice())
                            .salePrice(receiptRecord.getSalePrice())
                            .VAT(receiptRecord.getVAT())
                            .discountPercent(0)
                            .build());
                }
            });
        });
        ReceiptAdapter adapter = new ReceiptAdapter(aggregatedReceipt);
        new ReceiptPrinter().onClose(adapter);
    }
}
