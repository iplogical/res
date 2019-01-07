package com.inspirationlogical.receipt.corelib.service.table;

import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.model.entity.*;
import com.inspirationlogical.receipt.corelib.model.enums.*;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.TableParams;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRepository;
import com.inspirationlogical.receipt.corelib.repository.RestaurantRepository;
import com.inspirationlogical.receipt.corelib.repository.TableRepository;
import com.inspirationlogical.receipt.corelib.service.stock.StockService;
import com.inspirationlogical.receipt.corelib.service.vat.VATService;
import javafx.geometry.Point2D;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

@Service
public class TableServiceConfigImpl implements TableServiceConfig {

    @Autowired
    private StockService stockService;

    @Autowired
    private VATService vatService;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ReceiptRepository receiptRepository;

    @Override
    public List<TableView> getDisplayableTables() {
        return tableRepository.findAllByVisibleIsTrue().stream().map(this::buildTableView).collect(toList());
    }

    private TableView buildTableView(Table table) {
        return TableView.builder()
                .type(table.getType())
                .number(table.getNumber())
                .guestCount(table.getGuestCount())
                .capacity(table.getCapacity())
                .name(table.getName())
                .note(table.getNote())
                .coordinateX(table.getCoordinateX())
                .coordinateY(table.getCoordinateY())
                .width(table.getDimensionX())
                .height(table.getDimensionY())
                .orderDelivered(true)
                .orderDeliveryTime(now())
                .build();
    }

    @Override
    public TableView addTable(RestaurantView restaurantView, TableParams tableParams) {
        if (isTableNumberAlreadyInUse(tableParams.getNumber())) {
            throw new IllegalTableStateException("The table number " + tableParams.getNumber() + " is already in use");
        }
        Table newTable = buildTable(tableParams);
        Restaurant restaurant = restaurantRepository.getOne(restaurantView.getRestaurantId());
        restaurant.getTables().add(newTable);
        newTable.setOwner(restaurant);
        tableRepository.save(newTable);
        return buildTableView(newTable);
    }

    private Table buildTable(TableParams tableParams) {
        return Table.builder()
                .type(tableParams.getType())
                .name(tableParams.getName())
                .number(tableParams.getNumber())
                .note(tableParams.getNote())
                .guestCount(tableParams.getGuestCount())
                .capacity(tableParams.getCapacity())
                .visible(true)
                .coordinateX(tableParams.getPositionX())
                .coordinateY(tableParams.getPositionY())
                .dimensionX(tableParams.getWidth())
                .dimensionY(tableParams.getHeight())
                .build();
    }

    @Override
    public void deleteTable(TableView tableView) {
        Table table = tableRepository.findByNumber(tableView.getNumber());
        Table orphanage = tableRepository.findAllByType(TableType.ORPHANAGE).get(0);
        moveReceiptsToOrphanageTable(table, orphanage);
        table.getReceipts().clear();
        table.getReservations().forEach(reservation -> GuardedTransaction.delete(reservation, () -> {
        }));
        table.getReservations().clear();
        table.getOwner().getTables().remove(table);
        tableRepository.delete(table);
        tableRepository.save(orphanage);
    }


    private void moveReceiptsToOrphanageTable(Table archiveTable, Table orphanage) {
        orphanage.getReceipts().addAll(archiveTable.getReceipts().stream()
                .peek(receipt -> receipt.setOwner(orphanage)).collect(toList()));
    }

    @Override
    public void openTable(TableView tableView) {
        Table table = tableRepository.findByNumber(tableView.getNumber());
        if (isTableOpen(table)) {
            throw new IllegalTableStateException("Open table for an open table. Table number: " + table.getNumber());
        }
        Receipt receipt = buildReceipt(ReceiptType.SALE);
        receipt.setOwner(table);
        table.getReceipts().add(receipt);
        tableRepository.save(table);
    }

    @Override
    public boolean isTableOpen(TableView tableView) {
        Table table = tableRepository.findByNumber(tableView.getNumber());
        return isTableOpen(table);
    }

    private boolean isTableOpen(Table table) {
        return receiptRepository.getOpenReceipt(table.getNumber()) != null;
    }

    private Receipt buildReceipt(ReceiptType type) {
        return Receipt.builder()
                .type(type)
                .status(ReceiptStatus.OPEN)
                .paymentMethod(PaymentMethod.CASH)
                .openTime(now())
                .VATSerie(vatService.findValidVATSerie())
                .records(new ArrayList<>())
                .build();
    }

    @Override
    public boolean reOpenTable(TableView tableView) {
        Table table = tableRepository.findByNumber(tableView.getNumber());
        if (isTableOpen(table)) {
            throw new IllegalTableStateException("Re-open table for an open table. Table number: " + table.getNumber());
        }
        List<Receipt> receipts = receiptRepository.getReceiptByStatusAndOwner(ReceiptStatus.CLOSED, table.getNumber());
        if (receipts.isEmpty()) {
            return false;
        }
        Receipt latestReceipt = receipts.stream().sorted(Comparator.comparing(Receipt::getClosureTime).reversed())
                .collect(toList()).get(0);
        reopenReceipt(latestReceipt);
        updateStockRecords(latestReceipt);
        return true;
    }

    private void reopenReceipt(Receipt latestReceipt) {
        latestReceipt.setStatus(ReceiptStatus.OPEN);
        latestReceipt.setClosureTime(null);
    }

    private void updateStockRecords(Receipt latestReceipt) {
        latestReceipt.getRecords().forEach(receiptRecord -> {
            stockService.decreaseStock(receiptRecord, latestReceipt.getType());
        });
    }

    @Override
    public void setTableNumber(TableView tableView, int tableNumber) {
        Table table = tableRepository.findByNumber(tableView.getNumber());
        if (isTableNumberAlreadyInUse(tableNumber)) {
                throw new IllegalTableStateException("The table number " + tableView.getNumber() + " is already in use");
        }
        table.setNumber(tableNumber);
        tableRepository.save(table);
    }

    @Override
    public void setTableParams(TableView tableView, TableParams tableParams) {
        Table table = tableRepository.findByNumber(tableView.getNumber());
        table.setName(tableParams.getName());
        table.setGuestCount(tableParams.getGuestCount());
        table.setCapacity(tableParams.getCapacity());
        table.setNote(tableParams.getNote());
        table.setDimensionX(tableParams.getWidth());
        table.setDimensionY( tableParams.getHeight());
        tableRepository.save(table);
    }

    @Override
    public void setGuestCount(TableView tableView, int guestCount) {
        Table table = tableRepository.findByNumber(tableView.getNumber());
        table.setGuestCount(guestCount);
        tableRepository.save(table);
    }

    @Override
    public void setPosition(TableView tableView, Point2D position) {
        Table table = tableRepository.findByNumber(tableView.getNumber());
        table.setCoordinateX((int) position.getX());
        table.setCoordinateY((int) position.getY());
        tableRepository.save(table);
    }

    @Override
    public void rotateTable(TableView tableView) {
        Table table = tableRepository.findByNumber(tableView.getNumber());
        int dimensionX = table.getDimensionX();
        int dimensionY = table.getDimensionY();
        table.setDimensionX(dimensionY);
        table.setDimensionY(dimensionX);
        tableRepository.save(table);
    }

    @Override
    public boolean isTableNumberAlreadyInUse(int tableNumber) {
        Table table = tableRepository.findByNumber(tableNumber);
        if (table == null) {
            return false;
        }
        return true;
    }

    @Override
    public void exchangeTables(TableView selectedView, TableView otherView) {
        Table selected = tableRepository.findByNumber(selectedView.getNumber());
        Table other = tableRepository.findByNumber(otherView.getNumber());
        Receipt receiptOfSelected = receiptRepository.getOpenReceipt(selected.getNumber());
        Receipt receiptOfOther = receiptRepository.getOpenReceipt(other.getNumber());
        if (bothAreOpen(receiptOfSelected, receiptOfOther)) {
            exchangeOwners(selected, other, receiptOfSelected, receiptOfOther);
        } else if (oneIsOpen(receiptOfSelected, receiptOfOther)) {
            Receipt openReceipt = getTheOpenReceipt(receiptOfSelected, receiptOfOther);
            changeOwner(selected, other, openReceipt);
        }
    }

    private boolean bothAreOpen(Receipt receiptOfThis, Receipt receiptOfOther) {
        return receiptOfThis != null && receiptOfOther != null;
    }

    private void exchangeOwners(Table selected, Table other, Receipt receiptOfThis, Receipt receiptOfOther) {
        removeFromSelectedAndAddToOther(selected, other, receiptOfThis);
        receiptOfThis.setOwner(other);
        removeFromOtherAndAddToSelected(selected, other, receiptOfOther);
        receiptOfOther.setOwner(selected);
    }

    private void removeFromSelectedAndAddToOther(Table selected, Table other, Receipt receiptOfThis) {
        selected.getReceipts().remove(receiptOfThis);
        other.getReceipts().add(receiptOfThis);
    }

    private void removeFromOtherAndAddToSelected(Table selected, Table other, Receipt receiptOfOther) {
        other.getReceipts().remove(receiptOfOther);
        selected.getReceipts().add(receiptOfOther);
    }

    private boolean oneIsOpen(Receipt receiptOfThis, Receipt receiptOfOther) {
        return receiptOfThis != null || receiptOfOther != null;
    }

    private Receipt getTheOpenReceipt(Receipt receiptOfThis, Receipt receiptOfOther) {
        if (receiptOfThis != null) {
            return receiptOfThis;
        }
        return receiptOfOther;
    }

    private void changeOwner(Table selected, Table other, Receipt openReceipt) {
        if (openReceipt.getOwner().equals(selected)) {
            removeFromSelectedAndAddToOther(selected, other, openReceipt);
            openReceipt.setOwner(other);
        } else {
            removeFromOtherAndAddToSelected(selected, other, openReceipt);
            openReceipt.setOwner(selected);
        }
    }

    @Override
    public RecentConsumption getRecentConsumption(TableView tableView) {
        Receipt openReceipt = receiptRepository.getOpenReceipt(tableView.getNumber());
        if (openReceipt == null) {
            return RecentConsumption.NO_RECENT;
        }
        LocalDateTime latestSellTime = getLatestSellTime(openReceipt);
        if (latestSellTime.isAfter(now().minusMinutes(10))) {
            return RecentConsumption.RECENT_10_MINUTES;
        } else if (latestSellTime.isAfter(now().minusMinutes(30))) {
            return RecentConsumption.RECENT_30_MINUTES;
        } else {
            return RecentConsumption.NO_RECENT;
        }
    }

    private LocalDateTime getLatestSellTime(Receipt receipt) {
        Optional<ReceiptRecordCreated> latest = receipt.getRecords().stream()
                .map(ReceiptRecord::getCreatedList)
                .flatMap(Collection::stream)
                .max(Comparator.comparing(ReceiptRecordCreated::getCreated));
        if (latest.isPresent()) {
            return latest.get().getCreated();
        } else {
            return now().minusDays(1);
        }
    }
}
