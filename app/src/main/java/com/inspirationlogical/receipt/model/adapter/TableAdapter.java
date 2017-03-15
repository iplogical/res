package com.inspirationlogical.receipt.model.adapter;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.inspirationlogical.receipt.model.entity.Receipt;
import com.inspirationlogical.receipt.model.entity.Table;
import com.inspirationlogical.receipt.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.model.utils.GuardedTransaction;
import javafx.geometry.Point2D;

import java.util.List;

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
        Query query = manager.createNamedQuery(Receipt.GET_RECEIPT_BY_STATUS_AND_OWNER);
        query.setParameter("number", adaptee.getNumber());
        query.setParameter("status", ReceiptStatus.OPEN);
        @SuppressWarnings("unchecked")
        List<Receipt> active = query.getResultList();
        if(active == null) {
            return null;
        }
        else return new ReceiptAdapter(active.get(0), manager);
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
}
