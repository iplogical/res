package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.exception.IllegalReceiptStateException;
import com.inspirationlogical.receipt.corelib.model.entity.Client;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.enums.*;
import com.inspirationlogical.receipt.corelib.model.listeners.ReceiptAdapterListeners;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.service.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.service.PaymentParams;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReceiptAdapter extends AbstractAdapter<Receipt> {

    public  interface Listener{
        void onOpen(ReceiptAdapter receipt);
        void onClose(ReceiptAdapter receipt);
    }

    public static ReceiptAdapter receiptAdapterFactory(ReceiptType type) {
        return new ReceiptAdapter(Receipt.builder()
                                        .type(type)
                                        .status(ReceiptStatus.OPEN)
                                        .paymentMethod(PaymentMethod.CASH)
                                        .openTime(new GregorianCalendar())
                                        .VATSerie(VATSerieAdapter.vatSerieAdapterFactory().getAdaptee())
                                        .client(getDefaultClient())
                                        .build());
    }

    private static Client getDefaultClient() {
        // FIXME
        return Client.builder().name("client").address("dummy").TAXNumber("123").build();
    }

    public ReceiptAdapter(Receipt receipt) {
        super(receipt);
    }

    public void sellProduct(ProductAdapter productAdapter, int amount, boolean takeAway) {

        GuardedTransaction.RunWithRefresh(adaptee, () -> {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, -5);
            List<ReceiptRecord> records = GuardedTransaction.RunNamedQuery(ReceiptRecord.GET_RECEIPTS_RECORDS_BY_TIMESTAMP,
                    query -> {query.setParameter("created", calendar);
                              query.setParameter("name", productAdapter.getAdaptee().getLongName());
                              return query;});
            if(records.size() > 0) {
                records.get(0).setSoldQuantity(records.get(0).getSoldQuantity() + 1);
                records.get(0).setCreated(Calendar.getInstance());
                return;
            }
            ReceiptRecord record = ReceiptRecord.builder()
                    .product(productAdapter.getAdaptee())
                    .type(takeAway ? ReceiptRecordType.TAKE_AWAY : ReceiptRecordType.HERE)
                    .created(Calendar.getInstance())
                    .name(productAdapter.getAdaptee().getLongName())
                    .soldQuantity(amount)
                    .purchasePrice(productAdapter.getAdaptee().getPurchasePrice())
                    .salePrice(productAdapter.getAdaptee().getSalePrice())
                    .VAT(VATAdapter.getVatByName(takeAway ? ReceiptRecordType.TAKE_AWAY : ReceiptRecordType.HERE, VATStatus.VALID).getAdaptee().getVAT())
                    // TODO: calculate discount based on the PriceModifiers.
                    //.discountPercent(paymentParams.getDiscountPercent())
                    .build();
            record.setOwner(adaptee);
            adaptee.getRecords().add(record);
        });
    }

    public void sellAdHocProduct(int amount, AdHocProductParams adHocProductParams, PaymentParams paymentParams) {
        GuardedTransaction.RunWithRefresh(adaptee, () -> {
            ProductAdapter adHocProduct = ProductAdapter.getAdHocProduct(EntityManagerProvider.getEntityManager());
            ReceiptRecord record = ReceiptRecord.builder()
                    .product(adHocProduct.getAdaptee())
                    .type(paymentParams.getReceiptRecordType())
                    .created(new GregorianCalendar())
                    .name(adHocProductParams.getName())
                    .soldQuantity(amount)
                    .purchasePrice(adHocProductParams.getPurchasePrice())
                    .salePrice(adHocProductParams.getSalePrice())
                    .VAT(VATAdapter.getVatByName(paymentParams.getReceiptRecordType(), VATStatus.VALID).getAdaptee().getVAT())
                    .discountPercent(0)
                    .build();
            record.setOwner(adaptee);
            adaptee.getRecords().add(record);
        });

    }

    public Collection<ReceiptRecordAdapter> getSoldProducts() {
        GuardedTransaction.RunWithRefresh(adaptee, () -> {});
        return adaptee.getRecords().stream()
                .map(receiptRecord -> new ReceiptRecordAdapter(receiptRecord))
                .collect(Collectors.toList());
    }

    public void close(PaymentParams paymentParams){
        GuardedTransaction.RunWithRefresh(adaptee, () -> {
            if(adaptee.getStatus() == ReceiptStatus.CLOSED) {
                throw new IllegalReceiptStateException("Close operation is illegal for a CLOSED receipt");
            }
            adaptee.setStatus(ReceiptStatus.CLOSED);
            adaptee.setClosureTime(Calendar.getInstance());
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

        GuardedTransaction.RunWithRefresh(adaptee, () -> {
            Map<Long, ReceiptRecordView> recordsToPay = records.stream()
                    .collect(Collectors.toMap(ReceiptRecordView::getId, Function.identity()));
            List<ReceiptRecord> notPaidRecords = adaptee.getRecords().stream()
                    .filter(record -> !recordsToPay.containsKey(record.getId()))
                    .collect(Collectors.toList());
            List<ReceiptRecord> paidRecords = adaptee.getRecords().stream()
                    .filter(record -> recordsToPay.containsKey(record.getId()))
                    .filter(record -> recordsToPay.get(record.getId()).getSoldQuantity() == record.getSoldQuantity())
                    .collect(Collectors.toList());
            List<ReceiptRecord> partiallyNotPaidRecords = adaptee.getRecords().stream()
                    .filter(record -> recordsToPay.containsKey(record.getId()))
                    .filter(record -> recordsToPay.get(record.getId()).getSoldQuantity() != record.getSoldQuantity())
                    .filter(record -> { paidRecords.add(ReceiptRecord.builder()
                            .product(record.getProduct())
                            .type(record.getType())
                            .created(Calendar.getInstance())
                            .name(record.getName())
                            .soldQuantity(recordsToPay.get(record.getId()).getPaidQuantity())
                            .purchasePrice(record.getPurchasePrice())
                            .salePrice(record.getSalePrice())
                            .VAT(record.getVAT())
                            .discountPercent(record.getDiscountPercent())
                            .build());
                        record.setSoldQuantity(record.getSoldQuantity() - recordsToPay.get(record.getId()).getPaidQuantity());
                        return true;})
                    .collect(Collectors.toList());
            notPaidRecords.addAll(partiallyNotPaidRecords);
            notPaidRecords.forEach(record -> record.setOwner(adaptee));
            adaptee.setRecords(notPaidRecords);

            paidReceipt[0] = receiptAdapterFactory(ReceiptType.SALE);
            paidReceipt[0].getAdaptee().setRecords(paidRecords);
            paidRecords.forEach(record -> record.setOwner(paidReceipt[0].getAdaptee()));
            paidReceipt[0].getAdaptee().setStatus(ReceiptStatus.PENDING);
            paidReceipt[0].getAdaptee().setOwner(tableAdapter.getAdaptee());
            tableAdapter.getAdaptee().getReceipt().add(paidReceipt[0].getAdaptee());
            GuardedTransaction.Persist(paidReceipt[0].adaptee);
        });
        paidReceipt[0].close(paymentParams);
    }

    public long getTotalPrice() {
        GuardedTransaction.RunWithRefresh(adaptee, () -> {});
        return adaptee.getRecords().stream()
                .mapToInt(record -> (int)(record.getSalePrice() * record.getSoldQuantity())).sum();
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

    private static double calculatePurchaseGrossPrice(ReceiptRecord record) {
        return record.getPurchasePrice() * record.getSoldQuantity();
    }

    private static double calculatePurchaseNetPrice(ReceiptRecord record) {
        return calculatePurchaseGrossPrice(record) / calculateVATDivider(record);
    }

    private static double calculateSaleGrossPrice(ReceiptRecord record) {
        return record.getSalePrice() * record.getSoldQuantity() * getDiscountMultiplier(record.getDiscountPercent());
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
