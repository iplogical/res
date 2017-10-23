package com.inspirationlogical.receipt.corelib.model.adapter.restaurant;

import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.exception.RestaurantNotFoundException;
import com.inspirationlogical.receipt.corelib.model.adapter.AbstractAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.DailyClosureAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.TableAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.VATSerieAdapter;
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
}
