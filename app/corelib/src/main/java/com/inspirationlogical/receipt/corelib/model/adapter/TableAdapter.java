package com.inspirationlogical.receipt.corelib.model.adapter;

import static com.inspirationlogical.receipt.corelib.model.entity.Receipt.GET_RECEIPT_BY_STATUS_AND_OWNER;
import static com.inspirationlogical.receipt.corelib.model.entity.Receipt.GRAPH_RECEIPT_AND_RECORDS;
import static java.util.stream.Collectors.toList;

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

import com.inspirationlogical.receipt.corelib.params.TableParams;
import javafx.geometry.Point2D;
import lombok.NonNull;
public class TableAdapter extends AbstractAdapter<Table> {

    public TableAdapter(@NonNull Table adaptee) {
        super(adaptee);
    }

    static TableAdapter getTableFromActual(int number) {
        return getTableByNumber(number, true);
    }

    static TableAdapter getTableFromArchive(int number) {
        return getTableByNumber(number, false);
    }

    private static TableAdapter getTableByNumber(int number, boolean actual) {
        @SuppressWarnings("unchecked")
        List<Table> tables = getTablesByNumber(number, actual);
        if (tables.isEmpty()) {
            return null;
        }
        return new TableAdapter(tables.get(0));
    }

    private static List<Table> getTablesByNumber(int number, boolean actual) {
        if(actual) {
            return GuardedTransaction.runNamedQuery(Table.GET_TABLE_BY_NUMBER, query ->
                    query.setParameter("number", number));
        } else {
            return GuardedTransaction.runNamedQueryArchive(Table.GET_TABLE_BY_NUMBER, query ->
                    query.setParameter("number", number));
        }
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

    public ReceiptAdapter getOpenReceipt() {
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
            adaptee.setHost(TableAdapter.getTableFromActual(tableNumber).getAdaptee());
        }
    }

    public void setNumber(int tableNumber) {
        int originalNumber = adaptee.getNumber();
        GuardedTransaction.run(() -> {
            adaptee.setNumber(tableNumber);
        });
        GuardedTransaction.runArchive(() -> {
            Table tableArchive = getTableFromArchive(originalNumber).getAdaptee();
            tableArchive.setNumber(tableNumber);
        });
    }

    public void setTableParams(TableParams tableParams) {
        GuardedTransaction.run(() -> {
            adaptee.setName(tableParams.getName());
            adaptee.setGuestCount(tableParams.getGuestCount());
            adaptee.setCapacity(tableParams.getCapacity());
            adaptee.setNote(tableParams.getNote());
            adaptee.setDimensionX((int)tableParams.getDimension().getWidth());
            adaptee.setDimensionY((int)tableParams.getDimension().getHeight());
        });
    }

    public void setGuestCount(int guestCount) {
        GuardedTransaction.run(() -> adaptee.setGuestCount(guestCount));
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
        getOpenReceipt().close(paymentParams);
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
        ReceiptAdapter activeReceipt = getOpenReceipt();
        if(activeReceipt == null) {
            throw new IllegalTableStateException("Pay selective for a closed table. Table number: " + adaptee.getNumber());
        }
        activeReceipt.paySelective(this, records, paymentParams);
    }

    public void deleteTable() {
        if (isTableOpen()) {
            throw new IllegalTableStateException("Delete table for an open table. Table number: " + adaptee.getNumber());
        }
        if (isConsumerTable()) {
            throw new IllegalTableStateException("Delete table for a consumer table. Table number: " + adaptee.getNumber());
        }
        deleteTableFromActual();
        deleteTableFromArchive();
    }

    private void deleteTableFromActual() {
        GuardedTransaction.run(() -> {
            Table orphanage = getTablesByType(TableType.ORPHANAGE).get(0).getAdaptee();
            moveReceiptsToOrphanageTable(adaptee, orphanage);
            adaptee.getReceipts().clear();
            adaptee.getReservations().forEach(reservation -> GuardedTransaction.delete(reservation, () -> {}));
            adaptee.getReservations().clear();
            adaptee.getOwner().getTables().remove(adaptee);
            GuardedTransaction.delete(adaptee, () -> {});
        });
    }

    private void deleteTableFromArchive() {
        GuardedTransaction.runArchive(() -> {
            Table archiveTable = getTableFromArchive(adaptee.getNumber()).getAdaptee();
            Table orphanageArchive = (Table)GuardedTransaction.runNamedQueryArchive(Table.GET_TABLE_BY_TYPE,
                    query -> query.setParameter("type", TableType.ORPHANAGE)).get(0);
            moveReceiptsToOrphanageTable(archiveTable, orphanageArchive);
            archiveTable.getReceipts().clear();
            archiveTable.getOwner().getTables().remove(archiveTable);
            GuardedTransaction.deleteArchive(archiveTable, () -> {});
        });
    }

    private void moveReceiptsToOrphanageTable(Table archiveTable, Table orphanage) {
        orphanage.getReceipts().addAll(archiveTable.getReceipts().stream()
                .map(receipt -> {
                    receipt.setOwner(orphanage);
                    return receipt;
                }).collect(toList()));
    }

    public boolean isTableOpen() {
        return this.getOpenReceipt() != null;
    }

    public boolean isConsumerTable() {
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
