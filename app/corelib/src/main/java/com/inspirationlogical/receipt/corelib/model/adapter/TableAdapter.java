package com.inspirationlogical.receipt.corelib.model.adapter;

import static com.inspirationlogical.receipt.corelib.model.entity.Receipt.GET_RECEIPT_BY_STATUS_AND_OWNER;
import static com.inspirationlogical.receipt.corelib.model.entity.Receipt.GRAPH_RECEIPT_AND_RECORDS;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.model.view.TableViewImpl;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.params.StockParams;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import lombok.NonNull;
public class TableAdapter extends AbstractAdapter<Table> {

    public TableAdapter(@NonNull Table adaptee) {
        super(adaptee);
    }

    public static TableAdapter getTableByNumber(int number) {
        @SuppressWarnings("unchecked")
        List<Table> tables = getTablesByNumber(number);
        if (tables.isEmpty()) {
            return null;
        }
        return new TableAdapter(tables.get(0));
    }

    public static List<TableView> getTables() {
        List<Table> tables = GuardedTransaction.runNamedQuery(Table.GET_ALL_TABLES);
        return tables.stream()
                .filter(table -> !table.getType().equals(TableType.PURCHASE))
                .filter(table -> !table.getType().equals(TableType.INVENTORY))
                .filter(table -> !table.getType().equals(TableType.DISPOSAL))
                .filter(table -> !table.getType().equals(TableType.OTHER))
                .filter(table -> !table.getType().equals(TableType.ORPHANAGE))
                .map(TableAdapter::new).map(TableViewImpl::new).collect(toList());
    }

    private static List<Table> getTablesByNumber(int number) {
        return GuardedTransaction.runNamedQuery(Table.GET_TABLE_BY_NUMBER, query -> {
            query.setParameter("number", number);
            return query;
        });
    }

    public static List<Table> getTablesByType(TableType tableType) {
        return GuardedTransaction.runNamedQuery(Table.GET_TABLE_BY_TYPE,
                query -> {query.setParameter("type", tableType);
                    return query;});
    }

    public static int getFirstUnusedNumber() {
        List<Table> tables = GuardedTransaction.runNamedQuery(Table.GET_FIRST_UNUSED_NUMBER, query -> query);
        return tables.stream().mapToInt(Table::getNumber).min().orElse(0) + 1;
    }

    public void updateStock(List<StockParams> paramsList, ReceiptType receiptType) {
        ReceiptAdapter receiptAdapter = ReceiptAdapter.receiptAdapterFactory(receiptType);
        receiptAdapter.addStockRecords(paramsList);
        bindReceiptToTable(receiptAdapter);
        GuardedTransaction.persist(receiptAdapter.getAdaptee());
        receiptAdapter.close(PaymentParams.builder().paymentMethod(PaymentMethod.CASH)
                                                    .discountAbsolute(0)
                                                    .discountPercent(0)
                                                    .build());
    }

    public ReceiptAdapter getActiveReceipt() {
        List<Receipt> adapters = getReceiptsByStatusAndOwner(ReceiptStatus.OPEN, adaptee.getNumber());
        if (adapters.size() == 0) {
            return null;
        } else if (adapters.size() > 1) {
            throw new RuntimeException();
        }
        return new ReceiptAdapter(adapters.get(0));
    }

    public void setName(String name) {
        GuardedTransaction.run(() -> adaptee.setName(name));
    }

    public void setNumber(int tableNumber) {
        GuardedTransaction.run(() -> adaptee.setNumber(tableNumber));
    }

    public void setType(TableType tableType) {
        GuardedTransaction.run(() -> adaptee.setType(tableType));
    }

    public void setCapacity(int capacity) {
        GuardedTransaction.run(() -> adaptee.setCapacity(capacity));
    }

    public void setGuestCount(int guestCount) {
        GuardedTransaction.run(() -> adaptee.setGuestCount(guestCount));
    }

    public void setNote(String note) {
        GuardedTransaction.run(() -> adaptee.setNote(note));
    }

    public void displayTable() {
        GuardedTransaction.run(() -> adaptee.setVisible(true));
    }

    public void hideTable() {
        GuardedTransaction.run(() -> adaptee.setVisible(false));
    }

    public void setPosition(Point2D position) {
        GuardedTransaction.run(() -> {
            adaptee.setCoordinateX((int) position.getX());
            adaptee.setCoordinateY((int) position.getY());
        });
    }

    public void setDimension(Dimension2D dimension) {
        GuardedTransaction.run(() -> {
            adaptee.setDimensionX((int) dimension.getWidth());
            adaptee.setDimensionY((int) dimension.getHeight());
        });
    }

    public void rotateTable() {
        GuardedTransaction.run(() -> {
            int dimensionX = adaptee.getDimensionX();
            int dimensionY = adaptee.getDimensionY();
            adaptee.setDimensionX(dimensionY);
            adaptee.setDimensionY(dimensionX);
        });
    }

    public void openTable() {
        GuardedTransaction.runWithRefresh(adaptee, () -> {
            if (isTableOpen()) {
                throw new IllegalTableStateException("Open table for an open table. Table number: " + adaptee.getNumber());
            }
            ReceiptAdapter receiptAdapter = ReceiptAdapter.receiptAdapterFactory(ReceiptType.SALE);
            bindReceiptToTable(receiptAdapter);
        });
    }

    public void payTable(PaymentParams paymentParams) {
        GuardedTransaction.runWithRefresh(adaptee, () -> {
            if (!isTableOpen()) {
                throw new IllegalTableStateException("Pay table for a closed table. Table number: " + adaptee.getNumber());
            }
            getActiveReceipt().close(paymentParams);
            adaptee.setName("");
            adaptee.setGuestCount(0);
            adaptee.setNote("");
        });
    }

    public void paySelective(Collection<ReceiptRecordView> records, PaymentParams paymentParams) {
        GuardedTransaction.runWithRefresh(adaptee, () -> {
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
        GuardedTransaction.runWithRefresh(adaptee, () -> {
            List<Table> orphanageList = getTablesByType(TableType.ORPHANAGE);
            Table orphanage = orphanageList.get(0);
            if(orphanage.getReceipts() == null)
                orphanage.setReceipts(new ArrayList<>());
            orphanage.getReceipts().addAll(adaptee.getReceipts().stream()
                    .map(receipt -> {
                        receipt.setOwner(orphanage);
                        return receipt;
                    }).collect(toList()));
            adaptee.getReceipts().clear();
            adaptee.getReservations().forEach(reservation -> GuardedTransaction.delete(reservation, () -> {}));
            adaptee.getReservations().clear();
            adaptee.getOwner().getTables().remove(adaptee);
            GuardedTransaction.delete(adaptee, () -> {});
        });
    }

    private List<Receipt> getReceiptsByStatusAndOwner(ReceiptStatus status, int tableNumber) {
        return GuardedTransaction.runNamedQuery(GET_RECEIPT_BY_STATUS_AND_OWNER, GRAPH_RECEIPT_AND_RECORDS,
                query -> {
                    query.setParameter("status", status);
                    query.setParameter("number", tableNumber);
                    return query;
                });
    }

    private void bindReceiptToTable(ReceiptAdapter receiptAdapter) {
        receiptAdapter.getAdaptee().setOwner(adaptee);
        adaptee.getReceipts().add(receiptAdapter.getAdaptee());
    }

    protected boolean isTableOpen() {
        return this.getActiveReceipt() != null;
    }

    public List<Table> getConsumedTables() {
        return GuardedTransaction.runNamedQuery(Table.GET_TABLE_BY_CONSUMER, query -> {
            query.setParameter("consumer", adaptee);
            return query;
        });
    }
}
