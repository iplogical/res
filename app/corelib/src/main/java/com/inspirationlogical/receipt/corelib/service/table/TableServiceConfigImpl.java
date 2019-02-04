package com.inspirationlogical.receipt.corelib.service.table;

import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.exception.RestaurantNotFoundException;
import com.inspirationlogical.receipt.corelib.model.entity.*;
import com.inspirationlogical.receipt.corelib.model.enums.*;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.TableParams;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRepository;
import com.inspirationlogical.receipt.corelib.repository.RestaurantRepository;
import com.inspirationlogical.receipt.corelib.repository.TableRepository;
import com.inspirationlogical.receipt.corelib.service.stock.StockService;
import com.inspirationlogical.receipt.corelib.service.vat.VATService;
import javafx.geometry.Point2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class TableServiceConfigImpl implements TableServiceConfig {

    private static final Logger logger = LoggerFactory.getLogger(TableServiceConfigImpl.class);

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

    TableView buildTableView(Table table) {
        Receipt openReceipt = receiptRepository.getOpenReceipt(table.getNumber());
        return TableView.builder()
                .open(openReceipt != null)
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
                .foodDelivered(openReceipt == null || openReceipt.isFoodDelivered())
                .foodDeliveryTime(openReceipt == null ? now() : openReceipt.getFoodDeliveryTime())
                .drinkDelivered(openReceipt == null || openReceipt.isDrinkDelivered())
                .drinkDeliveryTime(openReceipt == null ? now() : openReceipt.getDrinkDeliveryTime())
                .build();
    }

    @Override
    public int getFirstUnusedNumber() {
        return tableRepository.getFirstUnusedNumber();
    }

    @Override
    public TableView addTable(TableParams tableParams) {
        if (isTableNumberAlreadyInUse(tableParams.getNumber())) {
            throw new IllegalTableStateException("The table number " + tableParams.getNumber() + " is already in use");
        }
        Table newTable = buildTable(tableParams);
        Restaurant restaurant = getActiveRestaurant();
        restaurant.getTables().add(newTable);
        newTable.setOwner(restaurant);
        tableRepository.save(newTable);
        logger.info("A table was added: " + tableParams);
        return buildTableView(newTable);
    }

    private Restaurant getActiveRestaurant() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        if (restaurants.isEmpty()) {
            throw new RestaurantNotFoundException();
        }
        return restaurants.get(0);
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
    public void deleteTable(int tableNumber) {
        Table table = tableRepository.findByNumber(tableNumber);
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
    public TableView openTable(int tableNumber) {
        Table table = tableRepository.findByNumber(tableNumber);
        if (isTableOpen(table)) {
            throw new IllegalTableStateException("Open table for an open table. Table number: " + table.getNumber());
        }
        Receipt receipt = buildReceipt(ReceiptType.SALE);
        receipt.setOwner(table);
        table.getReceipts().add(receipt);
        tableRepository.save(table);
        logger.info("A table was opened: " + tableNumber);
        return buildTableView(table);
    }

    @Override
    public boolean isTableOpen(int tableNumber) {
        Table table = tableRepository.findByNumber(tableNumber);
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
                .foodDelivered(true)
                .foodDeliveryTime(now())
                .drinkDelivered(true)
                .drinkDeliveryTime(now())
                .build();
    }

    @Override
    public TableView reOpenTable(int tableNumber) {
        Table table = tableRepository.findByNumber(tableNumber);
        if (isTableOpen(table)) {
            throw new IllegalTableStateException("Re-open table for an open table. Table number: " + table.getNumber());
        }
        List<Receipt> receipts = receiptRepository.getReceiptByStatusAndOwner(ReceiptStatus.CLOSED, table.getNumber());
        if (receipts.isEmpty()) {
            return openTable(tableNumber);
        }
        Receipt latestReceipt = receipts.stream().sorted(Comparator.comparing(Receipt::getClosureTime).reversed())
                .collect(toList()).get(0);
        reopenReceipt(latestReceipt);
        updateStockRecords(latestReceipt);
        tableRepository.save(table);
        logger.info("A table was re-opened: " + tableNumber);
        return buildTableView(table);
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
    public TableView updateTableParams(int tableNumber, TableParams tableParams) {
        int newTableNumber = tableParams.getNumber();
        if (tableNumber != newTableNumber && isTableNumberAlreadyInUse(newTableNumber)) {
            throw new IllegalTableStateException("The table number " + newTableNumber + " is already in use");
        }
        Table table = tableRepository.findByNumber(tableNumber);
        table.setNumber(newTableNumber);
        table.setName(tableParams.getName());
        table.setGuestCount(tableParams.getGuestCount());
        table.setCapacity(tableParams.getCapacity());
        table.setNote(tableParams.getNote());
        table.setDimensionX(tableParams.getWidth());
        table.setDimensionY( tableParams.getHeight());
        tableRepository.save(table);
        return buildTableView(table);
    }

    @Override
    public TableView setGuestCount(int tableNumber, int guestCount) {
        Table table = tableRepository.findByNumber(tableNumber);
        table.setGuestCount(guestCount);
        tableRepository.save(table);
        return buildTableView(table);
    }

    @Override
    public TableView setTablePosition(int tableNumber, Point2D position) {
        Table table = tableRepository.findByNumber(tableNumber);
        table.setCoordinateX((int) position.getX());
        table.setCoordinateY((int) position.getY());
        tableRepository.save(table);
        return buildTableView(table);
    }

    @Override
    public TableView rotateTable(int tableNumber) {
        Table table = tableRepository.findByNumber(tableNumber);
        int dimensionX = table.getDimensionX();
        int dimensionY = table.getDimensionY();
        table.setDimensionX(dimensionY);
        table.setDimensionY(dimensionX);
        tableRepository.save(table);
        return buildTableView(table);
    }

    private boolean isTableNumberAlreadyInUse(int tableNumber) {
        Table table = tableRepository.findByNumber(tableNumber);
        return table != null;
    }

    @Override
    public List<TableView> exchangeTables(int selectedTableNumber, int otherTableNumber) {
        Table selected = tableRepository.findByNumber(selectedTableNumber);
        Table other = tableRepository.findByNumber(otherTableNumber);
        exchangeReceipts(selected, other);
        exchangeTableParams(selected, other);
        tableRepository.save(selected);
        tableRepository.save(other);
        logger.info("Two tables are exchanged. Selected table number: " + selectedTableNumber +
                ", other table number: " + otherTableNumber);
        return Arrays.asList(buildTableView(selected), buildTableView(other));
    }

    private void exchangeReceipts(Table selected, Table other) {
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

    private void exchangeTableParams(Table selected, Table other) {
        exchangeTableName(selected, other);
        exchangeTableGuestCount(selected, other);
        exchangeTableNote(selected, other);
    }

    private void exchangeTableName(Table selected, Table other) {
        String selectedName = selected.getName();
        selected.setName(other.getName());
        other.setName(selectedName);
    }

    private void exchangeTableGuestCount(Table selected, Table other) {
        int selectedGuestCount = selected.getGuestCount();
        selected.setGuestCount(other.getGuestCount());
        other.setGuestCount(selectedGuestCount);
    }

    private void exchangeTableNote(Table selected, Table other) {
        String selectedNote = selected.getNote();
        selected.setNote(other.getNote());
        other.setNote(selectedNote);
    }

    @Override
    public RecentConsumption getRecentConsumption(int tableNumber) {
        Receipt openReceipt = receiptRepository.getOpenReceipt(tableNumber);
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

    @Override
    public TableView setFoodDelivered(int tableNumber, boolean delivered) {
        Receipt openReceipt = receiptRepository.getOpenReceipt(tableNumber);
        Table table = tableRepository.findByNumber(tableNumber);
        openReceipt.setFoodDelivered(delivered);
        receiptRepository.save(openReceipt);
        return buildTableView(table);
    }

    @Override
    public TableView setFoodDeliveryTime(int tableNumber, LocalDateTime now) {
        Receipt openReceipt = receiptRepository.getOpenReceipt(tableNumber);
        Table table = tableRepository.findByNumber(tableNumber);
        openReceipt.setFoodDeliveryTime(now);
        receiptRepository.save(openReceipt);
        return buildTableView(table);
    }

    @Override
    public TableView setDrinkDelivered(int tableNumber, boolean delivered) {
        Receipt openReceipt = receiptRepository.getOpenReceipt(tableNumber);
        Table table = tableRepository.findByNumber(tableNumber);
        openReceipt.setDrinkDelivered(delivered);
        receiptRepository.save(openReceipt);
        return buildTableView(table);
    }

    @Override
    public TableView setDrinkDeliveryTime(int tableNumber, LocalDateTime now) {
        Receipt openReceipt = receiptRepository.getOpenReceipt(tableNumber);
        Table table = tableRepository.findByNumber(tableNumber);
        openReceipt.setDrinkDeliveryTime(now);
        receiptRepository.save(openReceipt);
        return buildTableView(table);
    }
}
