package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.listeners.StockListener;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.params.StockParams;
import com.inspirationlogical.receipt.corelib.params.TableParams;
import javafx.geometry.Point2D;
import lombok.NonNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase.getReceiptsByStatusAndOwner;
import static java.util.stream.Collectors.toList;
public class TableAdapter extends AbstractAdapter<Table> {

    public TableAdapter(@NonNull Table adaptee) {
        super(adaptee);
    }

    public static TableAdapter getTable(int number) {
        return getTableByNumber(number);
    }

    private static TableAdapter getTableByNumber(int number) {
        @SuppressWarnings("unchecked")
        List<Table> tables = getTablesByNumber(number);
        if (tables.isEmpty()) {
            return null;
        }
        return new TableAdapter(tables.get(0));
    }

    private static List<Table> getTablesByNumber(int number) {
        return GuardedTransaction.runNamedQuery(Table.GET_TABLE_BY_NUMBER, query ->
                query.setParameter("number", number));
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

//    public static int getFirstUnusedNumber() {
//        List<Table> tables = GuardedTransaction.runNamedQuery(Table.GET_FIRST_UNUSED_NUMBER, query -> query);
//        return tables.stream().mapToInt(Table::getNumber).min().orElse(0) + 1;
//    }

    public ReceiptAdapterBase getOpenReceipt() {
        return (ReceiptAdapterBase)ReceiptAdapter.getOpenReceipt(adaptee.getNumber());
    }

//    public void updateStock(List<StockParams> paramsList, ReceiptType receiptType, StockListener.StockUpdateListener listener) {
//        ReceiptAdapterBase receiptAdapter = (ReceiptAdapterBase) ReceiptAdapter.receiptAdapterFactory(receiptType);
//        receiptAdapter.addStockRecords(paramsList);
//        bindReceiptToTable(receiptAdapter);
//        StockListener.addObserver(listener);
//        GuardedTransaction.persist(receiptAdapter.getAdaptee());
//        receiptAdapter.close(PaymentParams.builder().paymentMethod(PaymentMethod.CASH)
//                                                    .discountAbsolute(0)
//                                                    .discountPercent(0)
//                                                    .build());
//    }
//
//    private void bindReceiptToTable(ReceiptAdapterBase receiptAdapter) {
//        receiptAdapter.getAdaptee().setOwner(adaptee);
//        adaptee.getReceipts().add(receiptAdapter.getAdaptee());
//    }

    public TableAdapter getConsumer() {
        if(adaptee.getConsumer() == null) {
            return null;
        }
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
            adaptee.setHost(TableAdapter.getTable(tableNumber).getAdaptee());
        }
    }

    public void setNumber(int tableNumber) {
        int originalNumber = adaptee.getNumber();
        GuardedTransaction.run(() -> {
            adaptee.setNumber(tableNumber);
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
        if (isTableOpen()) {
            throw new IllegalTableStateException("Open table for an open table. Table number: " + adaptee.getNumber());
        }
        ReceiptAdapterBase receiptAdapter = (ReceiptAdapterBase)ReceiptAdapter.receiptAdapterFactory(ReceiptType.SALE);
        bindReceiptToTable(receiptAdapter);
        GuardedTransaction.persist(receiptAdapter.getAdaptee());
    }

    public boolean reOpenTable() {
        if (isTableOpen()) {
            throw new IllegalTableStateException("Re-open table for an open table. Table number: " + adaptee.getNumber());
        }
        List<Receipt> receipts = getReceiptsByStatusAndOwner(ReceiptStatus.CLOSED, adaptee.getNumber());
        if(receipts.isEmpty()) {
            return false;
        }
        Receipt latestReceipt = receipts.stream().sorted(Comparator.comparing(Receipt::getClosureTime).reversed())
                .collect(toList()).get(0);
        reopenReceipt(latestReceipt);
        updateStockRecords(latestReceipt);
        return true;
    }

    private void reopenReceipt(Receipt latestReceipt) {
        GuardedTransaction.run(() -> {
            latestReceipt.setStatus(ReceiptStatus.OPEN);
            latestReceipt.setClosureTime(null);
        });
    }

    private void updateStockRecords(Receipt latestReceipt) {
        latestReceipt.getRecords().forEach(receiptRecord -> {
            StockAdapter.decreaseStock(receiptRecord, latestReceipt.getType());
        });
    }

//    public void payTable(PaymentParams paymentParams) {
//        ReceiptAdapter openReceipt = ReceiptAdapter.getOpenReceipt(adaptee.getNumber());
//        if (openReceipt == null) {
//            throw new IllegalTableStateException("Pay table for a closed table. Table number: " + adaptee.getNumber());
//        }
//        openReceipt.close(paymentParams);
//        setDefaultTableParams();
//    }
//
//    private void setDefaultTableParams() {
//        if(TableType.isVirtualTable(adaptee.getType())) return;
//        GuardedTransaction.run(() -> {
//            adaptee.setName("");
//            adaptee.setGuestCount(0);
//            adaptee.setNote("");
//        });
//    }

//    public void paySelective(Collection<ReceiptRecordView> records, PaymentParams paymentParams) {
//        ReceiptAdapter openReceipt = ReceiptAdapter.getOpenReceipt(adaptee.getNumber());
//        if(openReceipt == null) {
//            throw new IllegalTableStateException("Pay selective for a closed table. Table number: " + adaptee.getNumber());
//        }
//        openReceipt.paySelective(records, paymentParams);
//    }
//
//    public void payPartial(double partialValue, PaymentParams paymentParams) {
//        ReceiptAdapter openReceipt = ReceiptAdapter.getOpenReceipt(adaptee.getNumber());
//        if(openReceipt == null) {
//            throw new IllegalTableStateException("Pay selective for a closed table. Table number: " + adaptee.getNumber());
//        }
//        openReceipt.payPartial(partialValue, paymentParams);
//    }

//    public void deleteTable() {
//        GuardedTransaction.run(() -> {
//            Table orphanage = getTablesByType(TableType.ORPHANAGE).get(0).getAdaptee();
//            moveReceiptsToOrphanageTable(adaptee, orphanage);
//            adaptee.getReceipts().clear();
//            adaptee.getReservations().forEach(reservation -> GuardedTransaction.delete(reservation, () -> {}));
//            adaptee.getReservations().clear();
//            adaptee.getOwner().getTables().remove(adaptee);
//            GuardedTransaction.delete(adaptee, () -> {});
//        });
//    }
//
//    private void moveReceiptsToOrphanageTable(Table archiveTable, Table orphanage) {
//        orphanage.getReceipts().addAll(archiveTable.getReceipts().stream()
//                .map(receipt -> {
//                    receipt.setOwner(orphanage);
//                    return receipt;
//                }).collect(toList()));
//    }

    public boolean isTableOpen() {
        return ReceiptAdapter.getOpenReceipt(adaptee.getNumber()) != null;
    }

//    public boolean isConsumerTable() {
//        return !this.getConsumedTables().isEmpty();
//    }
//
//    public boolean isTableConsumed() {
//        return adaptee.getConsumer() != null;
//    }
//
//    public boolean isTableHost() {
//        return !this.getHostedTables().isEmpty();
//    }
//
//    public boolean isTableHosted() {
//        return adaptee.getHost() != null;
//    }

    public List<Table> getConsumedTables() {
        return GuardedTransaction.runNamedQuery(Table.GET_TABLE_BY_CONSUMER, query ->
            query.setParameter("consumer", adaptee));
    }

    public List<Table> getHostedTables() {
        return GuardedTransaction.runNamedQuery(Table.GET_TABLE_BY_HOST, query ->
            query.setParameter("host", adaptee));
    }

    public void exchangeTables(TableAdapter other) {
        GuardedTransaction.run(() -> {
            ReceiptAdapterBase receiptOfThis = (ReceiptAdapterBase)ReceiptAdapter.getOpenReceipt(adaptee.getNumber());
            ReceiptAdapterBase receiptOfOther = (ReceiptAdapterBase)ReceiptAdapter.getOpenReceipt(other.getAdaptee().getNumber());
            if(bothAreOpen(receiptOfThis, receiptOfOther)) {
                exchangeOwners(other, receiptOfThis, receiptOfOther);
            } else if(oneIsOpen(receiptOfThis, receiptOfOther)) {
                ReceiptAdapterBase openReceipt = getTheOpenReceipt(receiptOfThis, receiptOfOther);
                changeOwner(openReceipt, other);
            }
        });
    }

    private boolean bothAreOpen(ReceiptAdapterBase receiptOfThis, ReceiptAdapterBase receiptOfOther) {
        return receiptOfThis != null && receiptOfOther != null;
    }

    private void exchangeOwners(TableAdapter other, ReceiptAdapterBase receiptOfThis, ReceiptAdapterBase receiptOfOther) {
        removeFromThisAndAddToOther(other, receiptOfThis);
        receiptOfThis.getAdaptee().setOwner(other.getAdaptee());
        removeFromOtherAndAddToThis(other, receiptOfOther);
        receiptOfOther.getAdaptee().setOwner(adaptee);
    }

    private void removeFromThisAndAddToOther(TableAdapter other, ReceiptAdapterBase receiptOfThis) {
        adaptee.getReceipts().remove(receiptOfThis.getAdaptee());
        other.getAdaptee().getReceipts().add(receiptOfThis.getAdaptee());
    }

    private void removeFromOtherAndAddToThis(TableAdapter other, ReceiptAdapterBase receiptOfOther) {
        other.getAdaptee().getReceipts().remove(receiptOfOther.getAdaptee());
        adaptee.getReceipts().add(receiptOfOther.getAdaptee());
    }

    private boolean oneIsOpen(ReceiptAdapterBase receiptOfThis, ReceiptAdapterBase receiptOfOther) {
        return receiptOfThis != null || receiptOfOther != null;
    }

    private void changeOwner(ReceiptAdapterBase openReceipt, TableAdapter other) {
        if(openReceipt.getAdaptee().getOwner().equals(adaptee)) {
            removeFromThisAndAddToOther(other, openReceipt);
            openReceipt.getAdaptee().setOwner(other.getAdaptee());
        } else {
            removeFromOtherAndAddToThis(other, openReceipt);
            openReceipt.getAdaptee().setOwner(adaptee);
        }
    }

    private ReceiptAdapterBase getTheOpenReceipt(ReceiptAdapterBase receiptOfThis, ReceiptAdapterBase receiptOfOther) {
        if(receiptOfThis != null) {
            return receiptOfThis;
        }
        return receiptOfOther;
    }
}
