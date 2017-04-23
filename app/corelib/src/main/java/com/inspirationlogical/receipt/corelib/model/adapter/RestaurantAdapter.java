package com.inspirationlogical.receipt.corelib.model.adapter;

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

import static java.time.LocalDateTime.now;

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
                .filter(table -> table.getType().equals(TableType.NORMAL) ||
                        table.getType().equals(TableType.VIRTUAL))
                .map(TableAdapter::new)
                .collect(Collectors.toList());
    }

    public TableAdapter addTable(Table.TableBuilder builder) {
        Table newTable = builder.build();
        checkTableNumberCollision(newTable.getNumber());
        GuardedTransaction.runWithRefresh(adaptee, () -> {
            adaptee.getTables().add(newTable);
            newTable.setOwner(adaptee);
        });
        return new TableAdapter(newTable);
    }

    public void checkTableNumberCollision(int tableNumber) {
        GuardedTransaction.runWithRefresh(adaptee, () -> {
            if (adaptee.getTables().stream().anyMatch(t -> t.getNumber() == tableNumber)) {
                throw new IllegalTableStateException("The table number " + tableNumber + " is already in use");
            }
        });
    }

    public void mergeTables(TableAdapter aggregate, List<TableAdapter> consumed) {
        if (!aggregate.isTableOpen() && consumed.stream().anyMatch(TableAdapter::isTableOpen)) {
            aggregate.openTable();
        }
        // todo refactor this method into smaller ones
        GuardedTransaction.run(() -> {
            List<ReceiptRecord> consumedRecords = consumed.stream()
                    .map(TableAdapter::getActiveReceipt)
                    .filter(Objects::nonNull)
                    .map(receiptAdapter -> {
                        Wrapper<ReceiptAdapter> receiptAdapterWrapper = new Wrapper<>();
                        receiptAdapterWrapper.setContent(aggregate.getActiveReceipt());
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

            if (aggregate.isTableOpen()) {
                aggregate.getActiveReceipt().getAdaptee().getRecords().addAll(consumedRecords);
            }
        });

        GuardedTransaction.run(() -> {
            consumed.forEach(adapter -> {
                Collection<Receipt> receipts = adapter.getAdaptee().getReceipts();
                if (receipts != null) {
                    receipts.forEach(receipt -> receipt.setOwner(aggregate.getAdaptee()));
                    aggregate.getAdaptee().getReceipts().addAll(receipts);
                    receipts.clear();
                }
            });
        });

        Integer consumedCapacity = consumed.stream().mapToInt(adapter -> adapter.getAdaptee().getCapacity()).sum();
        Integer consumedGuestCount = consumed.stream().mapToInt(adapter -> adapter.getAdaptee().getGuestCount()).sum();

        aggregate.setCapacity(aggregate.getAdaptee().getCapacity() + consumedCapacity);
        aggregate.setGuestCount(aggregate.getAdaptee().getGuestCount() + consumedGuestCount);

        consumed.forEach(TableAdapter::deleteTable);

        List<Table> tables = GuardedTransaction.runNamedQuery(Table.GET_TABLE_BY_NUMBER,
                query -> query.setParameter("number", aggregate.getAdaptee().getNumber()));

        aggregate.setAdaptee(tables.get(0));
    }

    public int getConsumptionOfTheDay(Predicate<Receipt> filter) {
        LocalDateTime previousClosure = DailyClosureAdapter.getLatestClosureTime();
        List<Receipt> closedReceipts = getReceiptsByClosureTime(previousClosure);
        if (closedReceipts.size() == 0) {
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
                .closureTime(LocalDateTime.now())
                .owner(TableAdapter.getTablesByType(TableType.ORPHANAGE).get(0))
                .paymentMethod(PaymentMethod.CASH) // TODO: intorduce new payment method for this purpose
                .status(ReceiptStatus.OPEN)
                .VATSerie(VATSerieAdapter.vatSerieAdapterFactory().getAdaptee())
                .type(ReceiptType.SALE)
                .paymentMethod(PaymentMethod.CASH).build();

        aggregatedReceipt.setId(-1L);
        receipts.stream().forEach(receipt -> aggregatedReceipt.getRecords().addAll(receipt.getRecords()));

        ReceiptAdapter adapter = new ReceiptAdapter(aggregatedReceipt);
        new ReceiptPrinter().onClose(adapter);
    }
}
