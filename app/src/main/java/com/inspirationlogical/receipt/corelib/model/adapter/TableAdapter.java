package com.inspirationlogical.receipt.corelib.model.adapter;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.inspirationlogical.receipt.corelib.exception.TableAlreadyOpenException;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import javafx.geometry.Point2D;

import java.util.List;
import java.util.stream.Collectors;

public class TableAdapter extends AbstractAdapter<Table>
{

    public TableAdapter(Table adaptee, EntityManager manager) {
        super(adaptee, manager);
    }

    public static TableAdapter getTableByNumber(EntityManager manager, int number) {
        @SuppressWarnings("unchecked")
        List<Table> table = getTablesByNumber(manager, number);
        return new TableAdapter(table.get(0), manager);
    }

    private static List<Table> getTablesByNumber(EntityManager manager, int number) {
        Query query = manager.createNamedQuery(Table.GET_TABLE_BY_NUMBER);
        query.setParameter("number", number);
        return (List<Table>) query.getResultList();
    }

    public ReceiptAdapter getActiveReceipt() {
        GuardedTransaction.Run(manager, () -> manager.refresh(adaptee));
        List<ReceiptAdapter> adapters = adaptee.getReceipt()
                .stream()
                .filter(elem -> elem.getStatus().equals(ReceiptStatus.OPEN))
                .map(elem -> new ReceiptAdapter(elem, manager))
                .collect(Collectors.toList());
        if(adapters.size() == 0) {
            return null;
        } else if(adapters.size() > 1) {
            throw new RuntimeException();
        }
        return adapters.get(0);
//        Query query = manager.createNamedQuery(Receipt.GET_RECEIPT_BY_STATUS_AND_OWNER);
//        query.setParameter("number", adaptee.getNumber());
//        query.setParameter("status", ReceiptStatus.OPEN);
//        @SuppressWarnings("unchecked")
//        List<Receipt> active = query.getResultList();
//        if(active.size() == 0) {
//            return null;
//        }
//        else return new ReceiptAdapter(active.get(0), manager);
    }

    public void setTableName(String name) {
        GuardedTransaction.Run(manager,() -> adaptee.setName(name));
    }

    public void setCapacity(int capacity) {
        GuardedTransaction.Run(manager,() -> adaptee.setCapacity(capacity));
    }

    public void setNote(String note) {
        GuardedTransaction.Run(manager,() -> adaptee.setNote(note));
    }

    public void displayTable() {
        GuardedTransaction.Run(manager,() -> adaptee.setVisibility(true));
    }

    public void hideTable() {
        GuardedTransaction.Run(manager,() -> adaptee.setVisibility(false));
    }

    public void moveTable(Point2D position) {
        GuardedTransaction.Run(manager,() -> {
            adaptee.setCoordinateX((int)position.getX());
            adaptee.setCoordinateY((int)position.getY());
        });
    }

    public void openTable() {
        GuardedTransaction.Run(manager,() -> manager.refresh(adaptee));
        if(isTableOpen()) {
            throw new TableAlreadyOpenException(adaptee.getNumber());
        }
        ReceiptAdapter receiptAdapter = ReceiptAdapter.receiptAdapterFactory(manager, ReceiptType.SALE);
        bindReceiptToTable(receiptAdapter);
        GuardedTransaction.Run(manager,() -> manager.persist(adaptee));
    }

    private void bindReceiptToTable(ReceiptAdapter receiptAdapter) {
        receiptAdapter.getAdaptee().setOwner(adaptee);
        adaptee.getReceipt().add(receiptAdapter.getAdaptee());
    }

    private boolean isTableOpen() {
        return this.getActiveReceipt() != null;
    }
}
