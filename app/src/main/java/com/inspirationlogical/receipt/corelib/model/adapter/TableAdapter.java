package com.inspirationlogical.receipt.corelib.model.adapter;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.service.PaymentParams;

import javafx.geometry.Point2D;
import lombok.NonNull;

public class TableAdapter extends AbstractAdapter<Table>
{

    public TableAdapter(@NonNull Table adaptee) {
        super(adaptee);
    }

    public static TableAdapter getTableByNumber(EntityManager manager, int number) {
        @SuppressWarnings("unchecked")
        List<Table> table = getTablesByNumber(manager, number);
        return new TableAdapter(table.get(0));
    }

    private static List<Table> getTablesByNumber(EntityManager manager, int number) {
        Query query = manager.createNamedQuery(Table.GET_TABLE_BY_NUMBER);
        query.setParameter("number", number);
        return (List<Table>) query.getResultList();
    }

    public ReceiptAdapter getActiveReceipt() {
        GuardedTransaction.RunWithRefresh(adaptee, () -> {});
        List<ReceiptAdapter> adapters = adaptee.getReceipt()
                .stream()
                .filter(elem -> elem.getStatus().equals(ReceiptStatus.OPEN))
                .map(elem -> new ReceiptAdapter(elem))
                .collect(Collectors.toList());
        if(adapters.size() == 0) {
            return null;
        } else if(adapters.size() > 1) {
            throw new RuntimeException();
        }
        return adapters.get(0);
    }

    public void setTableName(String name) {
        GuardedTransaction.Run(() -> adaptee.setName(name));
    }

    public void setCapacity(int capacity) {
        GuardedTransaction.Run(() -> adaptee.setCapacity(capacity));
    }

    public void setNote(String note) {
        GuardedTransaction.Run(() -> adaptee.setNote(note));
    }

    public void displayTable() {
        GuardedTransaction.Run(() -> adaptee.setVisibility(true));
    }

    public void hideTable() {
        GuardedTransaction.Run(() -> adaptee.setVisibility(false));
    }

    public void moveTable(Point2D position) {
        GuardedTransaction.Run(() -> {
            adaptee.setCoordinateX((int)position.getX());
            adaptee.setCoordinateY((int)position.getY());
        });
    }

    public void deleteTable() {
        // todo: implement
    }

    public void openTable() {
        GuardedTransaction.RunWithRefresh(adaptee, () -> {
            if(isTableOpen()) {
                throw new IllegalTableStateException("Open table for an open table. Table number: " + adaptee.getNumber());
            }
            ReceiptAdapter receiptAdapter = ReceiptAdapter.receiptAdapterFactory(ReceiptType.SALE);
            bindReceiptToTable(receiptAdapter);
            // FIXME: persist new Receipt?
        });
    }

    public void payTable(PaymentParams paymentParams) {
        GuardedTransaction.RunWithRefresh(adaptee, () -> {});
        if(!isTableOpen()) {
            throw new IllegalTableStateException("Pay table for a closed table. Table number: " + adaptee.getNumber());
        }

    }

    private void bindReceiptToTable(ReceiptAdapter receiptAdapter) {
        receiptAdapter.getAdaptee().setOwner(adaptee);
        adaptee.getReceipt().add(receiptAdapter.getAdaptee());
    }

    private boolean isTableOpen() {
        return this.getActiveReceipt() != null;
    }
}
