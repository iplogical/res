package com.inspirationlogical.receipt.corelib.model.adapter;

import static com.inspirationlogical.receipt.corelib.model.enums.Orientation.HORIZONTAL;
import static com.inspirationlogical.receipt.corelib.model.enums.Orientation.VERTICAL;
import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.*;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.service.PaymentParams;

import javafx.geometry.Point2D;
import lombok.NonNull;

public class TableAdapter extends AbstractAdapter<Table> {

    public TableAdapter(@NonNull Table adaptee) {
        super(adaptee);
    }

    public static TableAdapter getTableByNumber(EntityManager manager, int number) {
        @SuppressWarnings("unchecked")
        List<Table> table = getTablesByNumber(manager, number);
        if (table.isEmpty()) {
            return null;
        }
        return new TableAdapter(table.get(0));
    }

    private static List<Table> getTablesByNumber(EntityManager manager, int number) {
        Query query = manager.createNamedQuery(Table.GET_TABLE_BY_NUMBER);
        query.setParameter("number", number);
        return (List<Table>) query.getResultList();
    }

    public ReceiptAdapter getActiveReceipt() {

        List<Receipt> adapters = GuardedTransaction.RunNamedQuery(Receipt.GET_RECEIPT_BY_STATUS_AND_OWNER,
                query -> {
                    query.setParameter("status", ReceiptStatus.OPEN);
                    query.setParameter("number", adaptee.getNumber());
                    return query;
                });

        if (adapters.size() == 0) {
            return null;
        } else if (adapters.size() > 1) {
            throw new RuntimeException();
        }
        return new ReceiptAdapter(adapters.get(0));
    }

    public void setTableName(String name) {
        GuardedTransaction.Run(() -> adaptee.setName(name));
    }

    public void setTableNumber(int tableNumber) {
        GuardedTransaction.Run(() -> adaptee.setNumber(tableNumber));
    }

    public void setTableType(TableType tableType) {
        GuardedTransaction.Run(() -> adaptee.setType(tableType));
    }

    public void setCapacity(int capacity) {
        GuardedTransaction.Run(() -> adaptee.setCapacity(capacity));
    }

    public void setGuestNumber(int guestNumber) {
        GuardedTransaction.Run(() -> adaptee.setGuestNumber(guestNumber));
    }

    public void setNote(String note) {
        GuardedTransaction.Run(() -> adaptee.setNote(note));
    }

    public void displayTable() {
        GuardedTransaction.Run(() -> adaptee.setVisible(true));
    }

    public void hideTable() {
        GuardedTransaction.Run(() -> adaptee.setVisible(false));
    }

    public void moveTable(Point2D position) {
        GuardedTransaction.Run(() -> {
            adaptee.setCoordinateX((int) position.getX());
            adaptee.setCoordinateY((int) position.getY());
        });
    }

    public void rotateTable() {
        GuardedTransaction.Run(() -> {
            Orientation orientation = adaptee.getOrientation();
            if (orientation == HORIZONTAL) {
                adaptee.setOrientation(VERTICAL);
            } else {
                adaptee.setOrientation(HORIZONTAL);
            }
        });
    }

    public void openTable() {
        GuardedTransaction.RunWithRefresh(adaptee, () -> {
            if (isTableOpen()) {
                throw new IllegalTableStateException("Open table for an open table. Table number: " + adaptee.getNumber());
            }
            ReceiptAdapter receiptAdapter = ReceiptAdapter.receiptAdapterFactory(ReceiptType.SALE);
            bindReceiptToTable(receiptAdapter);
        });
    }

    public void payTable(PaymentParams paymentParams) {
        GuardedTransaction.RunWithRefresh(adaptee, () -> {
            if (!isTableOpen()) {
                throw new IllegalTableStateException("Pay table for a closed table. Table number: " + adaptee.getNumber());
            }
            getActiveReceipt().close(paymentParams);
            adaptee.setName("");
            adaptee.setGuestNumber(0);
            adaptee.setNote("");
        });
    }

    public void paySelective(Collection<ReceiptRecordView> records, PaymentParams paymentParams) {
        GuardedTransaction.RunWithRefresh(adaptee, () -> {
            if (!isTableOpen()) {
                throw new IllegalTableStateException("Pay selective for a closed table. Table number: " + adaptee.getNumber());
            }
        });
        getActiveReceipt().paySelective(this, records, paymentParams);
    }

    public void deleteTable() {
        if (isTableOpen()) {
            throw new IllegalTableStateException("Delete table for an open table. Table number: " + adaptee.getNumber());
        }
        GuardedTransaction.RunWithRefresh(adaptee, () -> {
            List<Table> orpahnageList = GuardedTransaction.RunNamedQuery(Table.GET_TABLE_ORPHANAGE);
            Table orpahnage = orpahnageList.get(0);
            if(orpahnage.getReceipt() == null)
                orpahnage.setReceipt(new ArrayList<>());
            orpahnage.getReceipt().addAll(adaptee.getReceipt().stream()
                    .map(receipt -> {
                        receipt.setOwner(orpahnage);
                        return receipt;
                    }).collect(Collectors.toList()));
            adaptee.getReceipt().clear();
        });
        GuardedTransaction.RunWithDelete(adaptee, () -> {
        });
    }

    public int getPaidConsumptionOfTheDay() {
        /// TODO: Replace this with the time of the previous closure.
        LocalDateTime previousClosure = now();
        previousClosure = previousClosure.plusHours(-1 * previousClosure.getHour());
        LocalDateTime finalPreviousClosure = previousClosure;
        List<Receipt> closedReceipts = getReceiptsByClosureTime(finalPreviousClosure);
        if (closedReceipts.size() == 0) {
            return 0;
        }
        return closedReceipts.stream()
                .map(ReceiptAdapter::new)
                .mapToInt(ReceiptAdapter::getTotalPrice).sum();
    }

    private List<Receipt> getReceiptsByClosureTime(LocalDateTime finalPreviousClosure) {
        return GuardedTransaction.RunNamedQuery(Receipt.GET_RECEIPT_BY_CLOSURE_TIME,
                query -> {
                    query.setParameter("closureTime", finalPreviousClosure);
                    return query;
                });
    }

    private void bindReceiptToTable(ReceiptAdapter receiptAdapter) {
        receiptAdapter.getAdaptee().setOwner(adaptee);
        adaptee.getReceipt().add(receiptAdapter.getAdaptee());
    }

    protected boolean isTableOpen() {
        return this.getActiveReceipt() != null;
    }
}
