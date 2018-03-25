package com.inspirationlogical.receipt.corelib.service.table;

import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.model.adapter.TableAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.VATSerieAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.*;
import com.inspirationlogical.receipt.corelib.model.enums.*;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.model.view.TableViewImpl;
import com.inspirationlogical.receipt.corelib.params.TableParams;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRepository;
import com.inspirationlogical.receipt.corelib.repository.RestaurantRepository;
import com.inspirationlogical.receipt.corelib.repository.TableRepository;
import javafx.geometry.Point2D;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.inspirationlogical.receipt.corelib.model.enums.TableType.canBeHosted;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class TableServiceConfigImpl implements TableServiceConfig {

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ReceiptRepository receiptRepository;

    @Override
    public Table addTable(RestaurantView restaurantView, TableParams tableParams) {
        Table newTable = Table.builder()
                .type(tableParams.getType())
                .name(tableParams.getName())
                .number(tableParams.getNumber())
                .note(tableParams.getNote())
                .guestCount(tableParams.getGuestCount())
                .capacity(tableParams.getCapacity())
                .visible(true)
                .coordinateX((int) tableParams.getPosition().getX())
                .coordinateY((int) tableParams.getPosition().getY())
                .dimensionX((int) tableParams.getDimension().getWidth())
                .dimensionY((int) tableParams.getDimension().getHeight())
                .build();
        if (isTableNumberAlreadyInUse(newTable.getNumber())) {
            if (canBeHosted(newTable.getType())) {
                newTable.setHost(tableRepository.findByNumber(newTable.getNumber()));
                newTable.setNumber(tableRepository.getFirstUnusedNumber());
            } else {
                throw new IllegalTableStateException("The table number " + newTable.getNumber() + " is already in use");
            }
        }
        Restaurant restaurant = restaurantRepository.findById(restaurantView.getRestaurantId());
        restaurant.getTables().add(newTable);
        newTable.setOwner(restaurant);
        tableRepository.save(newTable);

        return newTable;
    }

    @Override
    public void deleteTable(TableView tableView) {
        Table adaptee = tableRepository.getOne(tableView.getId());
        Table orphanage = tableRepository.findAllByType(TableType.ORPHANAGE).get(0);
        moveReceiptsToOrphanageTable(adaptee, orphanage);
        adaptee.getReceipts().clear();
        adaptee.getReservations().forEach(reservation -> GuardedTransaction.delete(reservation, () -> {}));
        adaptee.getReservations().clear();
        adaptee.getOwner().getTables().remove(adaptee);
        tableRepository.delete(adaptee);
        tableRepository.save(orphanage);
    }


    private void moveReceiptsToOrphanageTable(Table archiveTable, Table orphanage) {
        orphanage.getReceipts().addAll(archiveTable.getReceipts().stream()
                .map(receipt -> {
                    receipt.setOwner(orphanage);
                    return receipt;
                }).collect(toList()));
    }

    @Override
    public void openTable(TableView tableView) {
        Table table = tableRepository.getOne(tableView.getId());
        if (isTableOpen(table)) {
            throw new IllegalTableStateException("Open table for an open table. Table number: " + table.getNumber());
        }
        Receipt receipt = buildReceipt(ReceiptType.SALE);
        receipt.setOwner(table);
        table.getReceipts().add(receipt);
        tableRepository.save(table);
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
                .VATSerie(VATSerieAdapter.getActiveVATSerieAdapter().getAdaptee())
                .records(new ArrayList<>())
                .build();
    }

    @Override
    public void setTableNumber(TableView tableView, int tableNumber) {
        Table table = tableRepository.getOne(tableView.getId());
        if(isTableNumberAlreadyInUse(tableNumber)) {
            if(canBeHosted(table.getType())) {
                setHost(table, tableNumber);
            } else {
                throw new IllegalTableStateException("The table number " + tableView.getNumber() + " is already in use");
            }
            return;
        }
        removePreviousHost(table);
        table.setNumber(tableNumber);
        tableRepository.save(table);
    }

    private void setHost(Table table, int tableNumber) {
        removePreviousHost(table);
        setNewHost(table, tableNumber);
    }

    private void removePreviousHost(Table table) {
        table.setHost(null);
    }

    private void setNewHost(Table table, int tableNumber) {
        if(tableNumber != table.getNumber()) {    // Prevent a table being hosted by itself.
            table.setHost(tableRepository.findByNumber(tableNumber));
        }
    }

    @Override
    public void setTableParams(TableView tableView, TableParams tableParams) {
        Table table = tableRepository.getOne(tableView.getId());
        table.setName(tableParams.getName());
        table.setGuestCount(tableParams.getGuestCount());
        table.setCapacity(tableParams.getCapacity());
        table.setNote(tableParams.getNote());
        table.setDimensionX((int)tableParams.getDimension().getWidth());
        table.setDimensionY((int)tableParams.getDimension().getHeight());
        tableRepository.save(table);
    }

    @Override
    public void setGuestCount(TableView tableView, int guestCount) {
        Table table = tableRepository.getOne(tableView.getId());
        table.setGuestCount(guestCount);
        tableRepository.save(table);
    }

    @Override
    public void displayTable(TableView tableView) {
        Table table = tableRepository.getOne(tableView.getId());
        table.setVisible(true);
        tableRepository.save(table);
    }

    @Override
    public void hideTable(TableView tableView) {
        Table table = tableRepository.getOne(tableView.getId());
        table.setVisible(false);
        tableRepository.save(table);
    }

    @Override
    public void setPosition(TableView tableView, Point2D position) {
        Table table = tableRepository.getOne(tableView.getId());
        table.setCoordinateX((int) position.getX());
        table.setCoordinateY((int) position.getY());
        tableRepository.save(table);
    }

    @Override
    public void rotateTable(TableView tableView) {
        Table table = tableRepository.getOne(tableView.getId());
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
    public void mergeTables(TableView consumerView, List<TableView> consumedView) {
        Table consumer = tableRepository.getOne(consumerView.getId());
        List<Table> consumedTables = consumedView.stream()
                .map(consumed -> tableRepository.getOne(consumed.getId()))
                .collect(toList());
        openConsumerIfClosed(consumer);
        addConsumedTablesToConsumer(consumer, consumedTables);
        moveConsumedRecordsToConsumer(consumer, consumedTables);
        tableRepository.save(consumer);
    }

    private void openConsumerIfClosed(Table consumer) {
        if (!isTableOpen(consumer)) {
            openTable(new TableViewImpl(consumer));
        }
    }

    private void addConsumedTablesToConsumer(Table consumer, List<Table> consumed) {
        consumed.forEach(consumedTable -> {
            if (!consumedTable.getConsumed().isEmpty()) {
                throw new IllegalTableStateException("");
            }
            consumedTable.setVisible(false);
            bindConsumedToConsumer(consumer, consumedTable);
        });
    }

    private void bindConsumedToConsumer(Table consumer, Table consumedTable) {
        consumedTable.setConsumer(consumer);
        consumer.getConsumed().add(consumedTable);
    }

    private void moveConsumedRecordsToConsumer(Table consumer, List<Table> consumedTables) {
        Receipt consumerReceipt = receiptRepository.getOpenReceipt(consumer.getNumber());
        List<ReceiptRecord> consumedRecords = consumedTables.stream()
                .map(consumedTable -> receiptRepository.getOpenReceipt(consumedTable.getNumber()))
                .filter(Objects::nonNull)
                .map(receipt -> {
                    List<ReceiptRecord> receiptRecords = new ArrayList<>(receipt.getRecords());
                    receiptRecords.forEach(receiptRecord ->
                            receiptRecord.setOwner(consumerReceipt));
                    receipt.setStatus(ReceiptStatus.CANCELED);
                    receipt.getRecords().clear();
                    return receiptRecords;
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        consumerReceipt.getRecords().addAll(consumedRecords);
    }

    @Override
    public List<TableView> splitTables(TableView consumerView) {
        Table consumer = tableRepository.getOne(consumerView.getId());
        List<Table> consumed = consumer.getConsumed();
        consumer.setConsumed(new ArrayList<>());
        consumed.forEach(table -> {
            table.setConsumer(null);
            table.setVisible(true);
        });
        tableRepository.save(consumer);
        tableRepository.saveAll(consumed);
        return consumed.stream().map(TableViewImpl::new).collect(Collectors.toList());
    }

    @Override
    public int getTotalPrice(TableView tableView) {
        Receipt openReceipt = receiptRepository.getOpenReceipt(tableView.getNumber());
        if(tableView == null || openReceipt == null ) {
            return 0;
        }
        return openReceipt.getRecords().stream()
                .mapToInt(record -> (int)(record.getSalePrice() * record.getSoldQuantity())).sum();

    }

    @Override
    public RecentConsumption getRecentConsumption(TableView tableView) {
        Receipt openReceipt = receiptRepository.getOpenReceipt(tableView.getNumber());
        if(openReceipt == null) {
            return RecentConsumption.NO_RECENT;
        }
        LocalDateTime latestSellTime = getLatestSellTime(openReceipt);
        if(latestSellTime.isAfter(now().minusMinutes(10))) {
            return RecentConsumption.RECENT_10_MINUTES;
        } else if(latestSellTime.isAfter(now().minusMinutes(30))) {
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
        if(latest.isPresent()) {
            return latest.get().getCreated();
        } else {
            return now().minusDays(1);
        }
    }
}
