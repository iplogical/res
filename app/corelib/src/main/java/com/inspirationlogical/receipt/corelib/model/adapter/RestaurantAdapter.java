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
        GuardedTransaction.RunWithRefresh(adaptee, () -> {});
        Collection<Table> tables = adaptee.getTable();
        return tables.stream()
                .filter(table -> table.getType().equals(TableType.NORMAL) ||
                        table.getType().equals(TableType.VIRTUAL))
                .map(TableAdapter::new)
                .collect(Collectors.toList());
    }

    public TableAdapter addTable(Table.TableBuilder builder) {
        final Table[] newTable = new Table[1];
        GuardedTransaction.RunWithRefresh(adaptee, () -> {
            newTable[0] = builder.build();
            adaptee.getTable().stream()
                    .filter(table -> table.getNumber() == newTable[0].getNumber())
                    .map(table -> {throw new IllegalTableStateException("The table number " + newTable[0].getNumber() + " is already in use");})
                    .collect(Collectors.toList());
            adaptee.getTable().add(newTable[0]);
            newTable[0].setOwner(adaptee);
        });
        return new TableAdapter(newTable[0]);
    }

    public void mergeTables(TableAdapter aggregate, List<TableAdapter> consumed) {
        // todo add validataion that aggregate and consumed ones are open
        // todo refactor this method into smaller ones
        GuardedTransaction.Run(() -> {
            ReceiptAdapter activeReceiptAdapter = aggregate.getActiveReceipt();
            List<ReceiptRecord> consumedRecords = consumed.stream()
                    .map(TableAdapter::getActiveReceipt)
                    .filter(Objects::nonNull)
                    .map(receiptAdapter -> {
                        Collection<ReceiptRecordAdapter> receiptRecordAdapters = receiptAdapter.getSoldProducts();
                        receiptAdapter.getAdaptee().setStatus(ReceiptStatus.CANCELED);
                        receiptAdapter.getAdaptee().getRecords().clear();
                        receiptRecordAdapters.forEach(receiptRecordAdapter -> receiptRecordAdapter
                                .getAdaptee()
                                .setOwner(activeReceiptAdapter.getAdaptee()));
                        return receiptRecordAdapters;
                    })
                    .flatMap(record -> record.stream().map(ReceiptRecordAdapter::getAdaptee))
                    .collect(Collectors.toList());

            activeReceiptAdapter.getAdaptee().getRecords().addAll(consumedRecords);
        });

        GuardedTransaction.Run(() -> {
            consumed.forEach(adapter -> {
                Collection<Receipt> receipts = adapter.getAdaptee().getReceipt();
                receipts.forEach(receipt -> receipt.setOwner(aggregate.getAdaptee()));
                aggregate.getAdaptee().getReceipt().addAll(receipts);
                receipts.clear();
            });
        });

        Integer consumedCapacity = consumed.stream().mapToInt(adapter -> adapter.getAdaptee().getCapacity()).sum();
        Integer consumedGuestCount = consumed.stream().mapToInt(adapter -> adapter.getAdaptee().getGuestNumber()).sum();

        aggregate.setCapacity(aggregate.getAdaptee().getCapacity() + consumedCapacity);
        aggregate.setGuestNumber(aggregate.getAdaptee().getGuestNumber() + consumedCapacity);

        GuardedTransaction.Run(() -> {
            consumed.forEach(TableAdapter::deleteTable);
        });

        List<Table> tables = GuardedTransaction.RunNamedQuery(Table.GET_TABLE_BY_NUMBER,
                query -> query.setParameter("number", aggregate.getAdaptee().getNumber()));

        aggregate.setAdaptee(tables.get(0));
    }
}
