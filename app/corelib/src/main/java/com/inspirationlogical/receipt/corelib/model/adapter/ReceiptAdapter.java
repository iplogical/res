package com.inspirationlogical.receipt.corelib.model.adapter;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.inspirationlogical.receipt.corelib.exception.IllegalReceiptStateException;
import com.inspirationlogical.receipt.corelib.model.entity.*;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptRecordType;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.enums.VATStatus;
import com.inspirationlogical.receipt.corelib.model.listeners.ReceiptAdapterListeners;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.params.StockParams;
import com.inspirationlogical.receipt.corelib.utility.Wrapper;

public class ReceiptAdapter extends AbstractAdapter<Receipt> {

    public  interface Listener{
        void onOpen(ReceiptAdapter receipt);
        void onClose(ReceiptAdapter receipt);
    }

    public static ReceiptAdapter receiptAdapterFactory(ReceiptType type) {
        ReceiptAdapter newReceipt = new ReceiptAdapter(Receipt.builder()
                                        .type(type)
                                        .status(ReceiptStatus.OPEN)
                                        .paymentMethod(PaymentMethod.CASH)
                                        .openTime(now())
                                        .VATSerie(VATSerieAdapter.vatSerieAdapterFactory().getAdaptee())
                                        .records(new ArrayList<>())
                                        .build());
        ReceiptAdapterListeners.getAllListeners().forEach((l) -> {l.onOpen(newReceipt);});
        return newReceipt;
    }

    public static List<ReceiptAdapter> getReceipts() {
        List<Receipt> receipts = GuardedTransaction.runNamedQuery(Receipt.GET_RECEIPTS);
        return receipts.stream().map(ReceiptAdapter::new).collect(toList());
    }

    public static void deleteReceipts() {
        LocalDateTime latestClosureTime = DailyClosureAdapter.getLatestClosureTime();
        List<Receipt> receiptsToDelete = GuardedTransaction.runNamedQuery(Receipt.GET_RECEIPT_BY_CLOSURE_TIME_AND_TYPE, query -> {
                    query.setParameter("closureTime", latestClosureTime);
                    query.setParameter("type", ReceiptType.SALE);
                    return query;});
        receiptsToDelete.forEach(receipt -> {
            receipt.getOwner().getReceipts().remove(receipt);
            receipt.setOwner(null);
            GuardedTransaction.delete(receipt, () -> {});
        });
    }

    public ReceiptAdapter(Receipt receipt) {
        super(receipt);
    }

    public void sellProduct(ProductAdapter productAdapter, int amount, boolean isTakeAway, boolean isGift) {

        GuardedTransaction.run(() -> {
            List<Object[]> records = getReceiptRecordsByTimeStampAndName(productAdapter, now().minusSeconds(5));
            if(records.size() > 0) {
                ReceiptRecord record = ((ReceiptRecord)records.get(0)[0]);
                record.setSoldQuantity(record.getSoldQuantity() + 1);
                record.getCreatedList().add(ReceiptRecordCreated.builder().created(now()).owner(record).build());
                record.setDiscountPercent(isGift ? 100 : productAdapter.getCategoryAdapter().getDiscount(new ReceiptRecordAdapter(record)));
                applyDiscountOnRecordSalePrice(record);
                return;
            }
            ReceiptRecord record = ReceiptRecord.builder()
                    .product(productAdapter.getAdaptee())
                    .type(isTakeAway ? ReceiptRecordType.TAKE_AWAY : ReceiptRecordType.HERE)
                    .name(productAdapter.getAdaptee().getLongName())
                    .soldQuantity(amount)
                    .purchasePrice(productAdapter.getAdaptee().getPurchasePrice())
                    .salePrice(productAdapter.getAdaptee().getSalePrice())
                    .VAT(VATAdapter.getVatByName(isTakeAway ? ReceiptRecordType.TAKE_AWAY : ReceiptRecordType.HERE, VATStatus.VALID).getAdaptee().getVAT())
                    .createdList(new ArrayList<>())
                    .build();
            record.getCreatedList().add(ReceiptRecordCreated.builder().created(now()).owner(record).build());
            record.setDiscountPercent(isGift ? 100 : productAdapter.getCategoryAdapter().getDiscount(new ReceiptRecordAdapter(record)));
            applyDiscountOnRecordSalePrice(record);
            record.setOwner(adaptee);
            adaptee.getRecords().add(record);
        });
    }

    public void sellAdHocProduct(AdHocProductParams adHocProductParams, boolean takeAway) {
        GuardedTransaction.run(() -> {
            ProductAdapter adHocProduct = ProductAdapter.getAdHocProduct();
            ReceiptRecord record = ReceiptRecord.builder()
                    .product(adHocProduct.getAdaptee())
                    .type(takeAway ? ReceiptRecordType.TAKE_AWAY : ReceiptRecordType.HERE)
                    .name(adHocProductParams.getName())
                    .soldQuantity(adHocProductParams.getQuantity())
                    .purchasePrice(adHocProductParams.getPurchasePrice())
                    .salePrice(adHocProductParams.getSalePrice())
                    .VAT(VATAdapter.getVatByName(takeAway ? ReceiptRecordType.TAKE_AWAY : ReceiptRecordType.HERE, VATStatus.VALID).getAdaptee().getVAT())
                    .discountPercent(0)
                    .createdList(new ArrayList<>())
                    .build();
            record.getCreatedList().add(ReceiptRecordCreated.builder().owner(record).created(now()).build());
            record.setOwner(adaptee);
            adaptee.getRecords().add(record);
        });
    }

    public ReceiptRecordAdapter sellGameFee(int quantity) {
        Wrapper<ReceiptRecord> newRecordWrapper = new Wrapper<>();
        GuardedTransaction.run(() -> {
            ProductAdapter gameFeeProduct = ProductAdapter.getGameFeeProduct();
            List<Object[]> records = getReceiptRecordsByTimeStampAndName(gameFeeProduct, now().minusSeconds(2));
            if(records.size() > 0) {
                ReceiptRecord record = ((ReceiptRecord)records.get(0)[0]);
                record.setSoldQuantity(record.getSoldQuantity() + 1);
                record.getCreatedList().add(ReceiptRecordCreated.builder().created(now()).owner(record).build());
                newRecordWrapper.setContent(record);
                return;
            }
            ReceiptRecord record = ReceiptRecord.builder()
                    .product(gameFeeProduct.getAdaptee())
                    .type(ReceiptRecordType.HERE)
                    .name(gameFeeProduct.getAdaptee().getLongName())
                    .soldQuantity(quantity)
                    .purchasePrice(gameFeeProduct.getAdaptee().getPurchasePrice())
                    .salePrice(gameFeeProduct.getAdaptee().getSalePrice())
                    .VAT(VATAdapter.getVatByName(ReceiptRecordType.HERE, VATStatus.VALID).getAdaptee().getVAT())
                    .discountPercent(0)
                    .createdList(new ArrayList<>())
                    .build();
            for (int i = 0; i < quantity; i++) {
                record.getCreatedList().add(ReceiptRecordCreated.builder().created(now()).owner(record).build());
            }
            record.setOwner(adaptee);
            adaptee.getRecords().add(record);
            newRecordWrapper.setContent(record);
        });
        return new ReceiptRecordAdapter(newRecordWrapper.getContent());
    }

    public void addStockRecords(List<StockParams> paramsList) {
        GuardedTransaction.run(() -> {
            paramsList.forEach(params -> {
                List<Product> productList = GuardedTransaction.runNamedQuery(Product.GET_PRODUCT_BY_NAME,
                        query -> {query.setParameter("longName", params.getProductName());
                   return query;});
                Product product = productList.get(0);
                ReceiptRecord record = ReceiptRecord.builder()
                        .product(product)
                        .type(ReceiptRecordType.HERE)
                        .name(product.getLongName())
                        .absoluteQuantity(params.isAbsoluteQuantity() ? params.getQuantity() : params.getQuantity() * product.getStorageMultiplier())
                        .purchasePrice(product.getPurchasePrice())
                        .salePrice(product.getSalePrice())
                        .VAT(VATAdapter.getVatByName(ReceiptRecordType.HERE, VATStatus.VALID).getAdaptee().getVAT())
                        .discountPercent(0)
                        .createdList(new ArrayList<>())
                        .build();
                record.getCreatedList().add(ReceiptRecordCreated.builder().created(now()).owner(record).build());
                record.setOwner(adaptee);
                adaptee.getRecords().add(record);
            });
        });

    }

    public Collection<ReceiptRecordAdapter> getSoldProducts() {
        List<ReceiptRecord> records = getReceiptRecords();
        return records.stream().map(receiptRecord -> new ReceiptRecordAdapter(receiptRecord)).collect(toList());
    }

    public Collection<ReceiptRecordAdapter> getSoldProductsNoRefresh() {
        return adaptee.getRecords().stream()
                .map(receiptRecord -> new ReceiptRecordAdapter(receiptRecord))
                .collect(toList());
    }

    public void close(PaymentParams paymentParams){
        if(adaptee.getRecords().isEmpty()) {
            deleteReceipt();
            return;
        }
        GuardedTransaction.run(() -> {
            if(adaptee.getStatus() == ReceiptStatus.CLOSED) {
                throw new IllegalReceiptStateException("Close operation is illegal for a CLOSED receipt");
            }
            adaptee.setStatus(ReceiptStatus.CLOSED);
            adaptee.setClosureTime(now());
            adaptee.setSumPurchaseGrossPrice((int)adaptee.getRecords().stream()
                    .mapToDouble(ReceiptAdapter::calculatePurchaseGrossPrice).sum());
            adaptee.setSumPurchaseNetPrice((int)adaptee.getRecords().stream()
                    .mapToDouble(ReceiptAdapter::calculatePurchaseNetPrice).sum());
            adaptee.setSumSaleGrossPrice((int)adaptee.getRecords().stream()
                    .mapToDouble(ReceiptAdapter::calculateSaleGrossPrice).sum());
            adaptee.setSumSaleNetPrice((int)adaptee.getRecords().stream()
                    .mapToDouble(ReceiptAdapter::calculateSaleNetPrice).sum());
            adaptee.setDiscountPercent(calculateDiscount(paymentParams));
            adaptee.setUserCode(paymentParams.getUserCode());
            adaptee.setPaymentMethod(paymentParams.getPaymentMethod());
            applyDiscountOnSalePrices();
            ReceiptAdapterListeners.getAllListeners().forEach((l) -> {l.onClose(this);});
        });
    }

    public void paySelective(TableAdapter tableAdapter, Collection<ReceiptRecordView> records, PaymentParams paymentParams) {
        final ReceiptAdapter[] paidReceipt = new ReceiptAdapter[1];

        GuardedTransaction.run(() -> {
            Map<Long, ReceiptRecordView> recordsToPay = records.stream()
                    .collect(Collectors.toMap(ReceiptRecordView::getId, Function.identity()));
            List<ReceiptRecord> notPaidRecords = adaptee.getRecords().stream()
                    .filter(record -> !recordsToPay.containsKey(record.getId()))
                    .collect(toList());
            List<ReceiptRecord> paidRecords = adaptee.getRecords().stream()
                    .filter(record -> recordsToPay.containsKey(record.getId()))
                    .collect(toList());
            notPaidRecords.forEach(record -> record.setOwner(adaptee));
            adaptee.setRecords(notPaidRecords);

            paidReceipt[0] = receiptAdapterFactory(ReceiptType.SALE);
            paidReceipt[0].getAdaptee().setRecords(paidRecords);
            paidRecords.forEach(record -> record.setOwner(paidReceipt[0].getAdaptee()));
            paidReceipt[0].getAdaptee().setStatus(ReceiptStatus.PENDING);
            paidReceipt[0].getAdaptee().setOwner(tableAdapter.getAdaptee());
            tableAdapter.getAdaptee().getReceipts().add(paidReceipt[0].getAdaptee());
            GuardedTransaction.persist(paidReceipt[0].adaptee);
        });
        paidReceipt[0].close(paymentParams);
    }

    public ReceiptRecordAdapter cloneReceiptRecordAdapter(ReceiptRecordAdapter record, double amount) {
        final ReceiptRecord[] receiptRecord = new ReceiptRecord[1];
        GuardedTransaction.run(() -> {
            receiptRecord[0] = ReceiptRecord.builder()
                    .owner(this.getAdaptee())
                    .product(record.getAdaptee().getProduct())
                    .type(record.getAdaptee().getType())
                    .name(record.getAdaptee().getName())
                    .soldQuantity(amount)
                    .purchasePrice(record.getAdaptee().getPurchasePrice())
                    .salePrice(record.getAdaptee().getSalePrice())
                    .VAT(record.getAdaptee().getVAT())
                    .discountPercent(record.getAdaptee().getDiscountPercent())
                    .createdList(new ArrayList<>())
                    .build();
            receiptRecord[0].getCreatedList().add(ReceiptRecordCreated.builder().created(now()).owner(receiptRecord[0]).build());
            this.getAdaptee().getRecords().add(receiptRecord[0]);
        });
        return new ReceiptRecordAdapter(receiptRecord[0]);
    }

    public void cancelReceiptRecord(ReceiptRecordAdapter receiptRecordAdapter) {
        GuardedTransaction.delete(receiptRecordAdapter.getAdaptee(), () -> {
            adaptee.getRecords().remove(receiptRecordAdapter.getAdaptee());
        });
    }

    public void cancelReceiptRecord(ReceiptRecord receiptRecord, Receipt receipt) {
        GuardedTransaction.delete(receiptRecord, () -> {
            receipt.getRecords().remove(receiptRecord);
        });
    }

    public void mergeReceiptRecords() {
        GuardedTransaction.run(() -> {
            Map<Double, Map<String, List<ReceiptRecord>>> receiptsByDiscountAndName =
                    adaptee.getRecords().stream().collect(groupingBy(ReceiptRecord::getDiscountPercent, groupingBy(ReceiptRecord::getName)));

            List<Map<String, List<ReceiptRecord>>> listOfReceiptsByDiscountAndName = new ArrayList<>(receiptsByDiscountAndName.values());

            List<ReceiptRecord> mergedRecords = new ArrayList<>();

            listOfReceiptsByDiscountAndName.forEach(stringListMap -> {
                stringListMap.values().forEach(group -> {
                    if (group.size() > 1) {
                        ReceiptRecord mergedRecord = group.stream().reduce((a, b) -> {
                            cancelReceiptRecord(a, adaptee);
                            cancelReceiptRecord(b, adaptee);
                            ReceiptRecord receiptRecord = ReceiptRecord.builder()
                                    .product(a.getProduct())
                                    .type(a.getType())
                                    .name(a.getName())
                                    .soldQuantity(a.getSoldQuantity() + b.getSoldQuantity())
                                    .absoluteQuantity(a.getAbsoluteQuantity() + b.getAbsoluteQuantity())
                                    .purchasePrice(a.getPurchasePrice())
                                    .salePrice(a.getSalePrice())
                                    .VAT(a.getVAT())
                                    .discountPercent(a.getDiscountPercent())
                                    .owner(adaptee)
                                    .createdList(new ArrayList<>())
                                    .build();
                            receiptRecord.getCreatedList().addAll(a.getCreatedList());
                            receiptRecord.getCreatedList().addAll(b.getCreatedList());
                            receiptRecord.getCreatedList().forEach(created -> created.setOwner(receiptRecord));
                            return receiptRecord;
                        }).get();
                        mergedRecords.add(mergedRecord);
                    }
                });
            });
            mergedRecords.forEach(receiptRecord -> adaptee.getRecords().add(receiptRecord));
        });
    }

    public int getTotalPrice() {
        return getReceiptRecords().stream()
                .mapToInt(record -> (int)(record.getSalePrice() * record.getSoldQuantity())).sum();
    }

    private List<Object[]> getReceiptRecordsByTimeStampAndName(ProductAdapter productAdapter, LocalDateTime dateTime) {
        return GuardedTransaction.runNamedQueryWithJoin(ReceiptRecord.GET_RECEIPT_RECORDS_BY_TIMESTAMP,
                query -> query.setParameter("created", dateTime)
                        .setParameter("name", productAdapter.getAdaptee().getLongName()));
    }

    private void applyDiscountOnRecordSalePrice(ReceiptRecord receiptRecord) {
        receiptRecord.setSalePrice(receiptRecord.getProduct().getSalePrice());
        receiptRecord.setSalePrice((int)Math.round(receiptRecord.getSalePrice() * getDiscountMultiplier(receiptRecord.getDiscountPercent())));
    }

    private List<ReceiptRecord> getReceiptRecords() {
        return GuardedTransaction.runNamedQuery(ReceiptRecord.GET_RECEIPT_RECORDS_BY_RECEIPT,
                query -> {query.setParameter("owner_id", adaptee.getId());
                    return query;});
    }

    private double calculateDiscount(PaymentParams paymentParams) {
        if(paymentParams.getDiscountPercent() != 0) {
            return paymentParams.getDiscountPercent();
        } else if(paymentParams.getDiscountAbsolute() != 0) {
            double discountAbs = paymentParams.getDiscountAbsolute();
            double sumSale = getAdaptee().getSumSaleGrossPrice();
            double discount = discountAbs / sumSale * 100;
            return discount;
        } else return 0;
    }

    private void applyDiscountOnSalePrices() {
        adaptee.setSumSaleGrossPrice((int)(adaptee.getSumSaleGrossPrice() * getDiscountMultiplier(adaptee.getDiscountPercent())));
        adaptee.setSumSaleNetPrice((int)(adaptee.getSumSaleNetPrice() * getDiscountMultiplier(adaptee.getDiscountPercent())));
    }

    private void deleteReceipt() {
        GuardedTransaction.delete(adaptee, () -> {
            adaptee.getOwner().getReceipts().remove(adaptee);
            adaptee.setOwner(null);
        });
    }

    private static double calculatePurchaseGrossPrice(ReceiptRecord record) {
        return record.getPurchasePrice() * record.getSoldQuantity();
    }

    private static double calculatePurchaseNetPrice(ReceiptRecord record) {
        return calculatePurchaseGrossPrice(record) / calculateVATDivider(record);
    }

    private static double calculateSaleGrossPrice(ReceiptRecord record) {
        return record.getSalePrice() * record.getSoldQuantity();
    }

    private static double calculateSaleNetPrice(ReceiptRecord record) {
        return calculateSaleGrossPrice(record) / calculateVATDivider(record);
    }

    private static double calculateVATDivider(ReceiptRecord record) {
        return (100 + record.getVAT()) / 100;
    }

    private static double getDiscountMultiplier(double discountPercent) {
        return (100D - discountPercent) / 100;
    }
}
