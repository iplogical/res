package com.inspirationlogical.receipt.corelib.model.adapter;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.exception.RestaurantNotFoundException;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.entity.Restaurant;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
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
        Integer consumedGuestCount = consumed.stream().mapToInt(adapter -> adapter.getAdaptee().getGuestNumber()).sum();

        aggregate.setCapacity(aggregate.getAdaptee().getCapacity() + consumedCapacity);
        aggregate.setGuestNumber(aggregate.getAdaptee().getGuestNumber() + consumedGuestCount);

        consumed.forEach(TableAdapter::deleteTable);

        List<Table> tables = GuardedTransaction.runNamedQuery(Table.GET_TABLE_BY_NUMBER,
                query -> query.setParameter("number", aggregate.getAdaptee().getNumber()));

        aggregate.setAdaptee(tables.get(0));
    }
}
