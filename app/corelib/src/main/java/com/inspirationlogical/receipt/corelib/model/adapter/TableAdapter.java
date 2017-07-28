package com.inspirationlogical.receipt.corelib.model.adapter;

import static com.inspirationlogical.receipt.corelib.model.entity.Receipt.GET_RECEIPT_BY_STATUS_AND_OWNER;
import static com.inspirationlogical.receipt.corelib.model.entity.Receipt.GRAPH_RECEIPT_AND_RECORDS;
import static java.util.stream.Collectors.toList;

import java.security.Guard;
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

    private static List<Table> getTablesByNumber(int number) {
        return GuardedTransaction.runNamedQuery(Table.GET_TABLE_BY_NUMBER, query -> {
            query.setParameter("number", number);
            return query;
        });
    }

    public static List<TableAdapter> getDisplayableTables() {
        List<Table> tables = GuardedTransaction.runNamedQuery(Table.GET_DISPLAYABLE_TABLES);
        return tables.stream()
                .map(TableAdapter::new)
                .collect(toList());
    }

    public static List<TableAdapter> getTablesByType(TableType tableType) {
        List<Table> tables = GuardedTransaction.runNamedQuery(Table.GET_TABLE_BY_TYPE,
                query -> query.setParameter("type", tableType));
        return tables.stream()
                .map(TableAdapter::new)
                .collect(toList());
    }

    public static int getFirstUnusedNumber() {
        List<Table> tables = GuardedTransaction.runNamedQuery(Table.GET_FIRST_UNUSED_NUMBER, query -> query);
        return tables.stream().mapToInt(Table::getNumber).min().orElse(0) + 1;
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

    private List<Receipt> getReceiptsByStatusAndOwner(ReceiptStatus status, int tableNumber) {
        return GuardedTransaction.runNamedQuery(GET_RECEIPT_BY_STATUS_AND_OWNER, GRAPH_RECEIPT_AND_RECORDS,
                query -> {
                    query.setParameter("status", status);
                    query.setParameter("number", tableNumber);
                    return query;
                });
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

    private void bindReceiptToTable(ReceiptAdapter receiptAdapter) {
        receiptAdapter.getAdaptee().setOwner(adaptee);
        adaptee.getReceipts().add(receiptAdapter.getAdaptee());
    }

    public TableAdapter getConsumer() {
        return new TableAdapter(adaptee.getConsumer());
    }

    public TableAdapter getHost() {
        if(adaptee.getHost() == null) {
            return null;
        }
        return new TableAdapter(adaptee.getHost());
    }

    public void setHost(int tableNumber) {
        GuardedTransaction.run(() -> {
            removePreviousHost();
            setNewHost(tableNumber);
        });
    }

    public void removePreviousHost() {
        adaptee.setHost(null);
    }

    private void setNewHost(int tableNumber) {
        if(tableNumber != adaptee.getNumber()) {    // Prevent a table being hosted by itself.
            adaptee.setHost(TableAdapter.getTableByNumber(tableNumber).getAdaptee());
        }
    }

    public void setNumber(int tableNumber) {
        GuardedTransaction.run(() -> {
            adaptee.setNumber(tableNumber);
        });
    }

    public void setName(String name) {
        GuardedTransaction.run(() -> adaptee.setName(name));
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
        GuardedTransaction.run(() -> {
            if (isTableOpen()) {
                throw new IllegalTableStateException("Open table for an open table. Table number: " + adaptee.getNumber());
            }
            ReceiptAdapter receiptAdapter = ReceiptAdapter.receiptAdapterFactory(ReceiptType.SALE);
            bindReceiptToTable(receiptAdapter);
        });
    }

    public void payTable(PaymentParams paymentParams) {
        if (!isTableOpen()) {
            throw new IllegalTableStateException("Pay table for a closed table. Table number: " + adaptee.getNumber());
        }
        getActiveReceipt().close(paymentParams);
        setDefaultTableParams();
    }

    private void setDefaultTableParams() {
        if(TableType.isVirtualTable(adaptee.getType())) return;
        GuardedTransaction.run(() -> {
            adaptee.setName("");
            adaptee.setGuestCount(0);
            adaptee.setNote("");
        });
    }

    public void paySelective(Collection<ReceiptRecordView> records, PaymentParams paymentParams) {
        ReceiptAdapter activeReceipt = getActiveReceipt();
        if(activeReceipt == null) {
            throw new IllegalTableStateException("Pay selective for a closed table. Table number: " + adaptee.getNumber());
        }
        activeReceipt.paySelective(this, records, paymentParams);
    }

    public void deleteTable() {
        if (isTableOpen()) {
            throw new IllegalTableStateException("Delete table for an open table. Table number: " + adaptee.getNumber());
        }
        if (isTableConsumer()) {
            throw new IllegalTableStateException("Delete table for a consumer table. Table number: " + adaptee.getNumber());
        }
        GuardedTransaction.run(() -> {
            Table orphanage = getTablesByType(TableType.ORPHANAGE).get(0).getAdaptee();
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

    public boolean isTableOpen() {
        return this.getActiveReceipt() != null;
    }

    public boolean isTableConsumer() {
        return !this.getConsumedTables().isEmpty();
    }

    public boolean isTableConsumed() {
        return adaptee.getConsumer() != null;
    }

    public boolean isTableHost() {
        return !this.getHostedTables().isEmpty();
    }

    public boolean isTableHosted() {
        return adaptee.getHost() != null;
    }

    public List<Table> getConsumedTables() {
        return GuardedTransaction.runNamedQuery(Table.GET_TABLE_BY_CONSUMER, query ->
            query.setParameter("consumer", adaptee));
    }

    public List<Table> getHostedTables() {
        return GuardedTransaction.runNamedQuery(Table.GET_TABLE_BY_HOST, query ->
            query.setParameter("host", adaptee));
    }
}
