package com.inspirationlogical.receipt.corelib.service.table;

import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.model.adapter.TableAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase;
import com.inspirationlogical.receipt.corelib.model.entity.*;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.enums.RecentConsumption;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.TableParams;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRepository;
import com.inspirationlogical.receipt.corelib.repository.RestaurantRepository;
import com.inspirationlogical.receipt.corelib.repository.TableRepository;
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
    public boolean isTableNumberAlreadyInUse(int tableNumber) {
        Table table = tableRepository.findByNumber(tableNumber);
        if (table == null) {
            return false;
        }
        return true;
    }

    @Override
    public void mergeTables(TableAdapter consumer, List<TableAdapter> consumed) {
        openConsumerIfClosed(consumer);
        addConsumedTablesToConsumer(consumer, consumed);
        moveConsumedRecordsToConsumer(consumer, consumed);
        tableRepository.save(consumer.getAdaptee());
    }

    private void openConsumerIfClosed(TableAdapter consumer) {
        if (!consumer.isTableOpen()) {
            consumer.openTable();
        }
    }

    private void addConsumedTablesToConsumer(TableAdapter consumer, List<TableAdapter> consumed) {
        consumed.forEach(consumedTableAdapter -> {
            if (consumedTableAdapter.isConsumerTable()) {
                throw new IllegalTableStateException("");
            }
            consumedTableAdapter.hideTable();
            bindConsumedToConsumer(consumer.getAdaptee(), consumedTableAdapter.getAdaptee());
        });
    }

    private void bindConsumedToConsumer(Table consumer, Table consumedTable) {
        consumedTable.setConsumer(consumer);
        consumer.getConsumed().add(consumedTable);
    }

    private void moveConsumedRecordsToConsumer(TableAdapter consumer, List<TableAdapter> consumed) {
        Receipt consumerReceipt = consumer.getOpenReceipt().getAdaptee();
        List<ReceiptRecord> consumedRecords = consumed.stream()
                .map(TableAdapter::getOpenReceipt)
                .filter(Objects::nonNull)
                .map(ReceiptAdapterBase::getAdaptee)
                .map(receipt -> {
                    List<ReceiptRecord> receiptRecords = new ArrayList<>();
                    receiptRecords.addAll(receipt.getRecords());
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
    public List<TableAdapter> splitTables(TableAdapter consumer) {
        List<Table> consumed = consumer.getConsumedTables();
        consumer.getAdaptee().setConsumed(new ArrayList<>());
        consumed.forEach(table -> {
            table.setConsumer(null);
            table.setVisible(true);
        });
        tableRepository.save(consumer.getAdaptee());
        tableRepository.saveAll(consumed);
        return consumed.stream().map(TableAdapter::new).collect(Collectors.toList());
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
