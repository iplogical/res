package com.inspirationlogical.receipt.corelib.model.adapter;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.inspirationlogical.receipt.corelib.exception.IllegalReceiptStateException;
import com.inspirationlogical.receipt.corelib.model.entity.Client;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptRecordType;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.enums.VATStatus;
import com.inspirationlogical.receipt.corelib.model.listeners.ReceiptAdapterListeners;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.service.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.service.PaymentParams;

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
                records.get(0).setDiscountPercent(productAdapter.getCategoryAdapter().getDiscount(new ReceiptRecordAdapter(records.get(0))));
                applyDiscountOnRecordSalePrice(records.get(0));
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
                    .build();
            // TODO: calculate discount based on the PriceModifiers.
            record.setDiscountPercent(productAdapter.getCategoryAdapter().getDiscount(new ReceiptRecordAdapter(record)));
            applyDiscountOnRecordSalePrice(record);
            record.setOwner(adaptee);
            adaptee.getRecords().add(record);
        });
    }

    private void applyDiscountOnRecordSalePrice(ReceiptRecord receiptRecord) {
        receiptRecord.setSalePrice(receiptRecord.getProduct().getSalePrice());
        receiptRecord.setSalePrice((int)Math.round(receiptRecord.getSalePrice() * getDiscountMultiplier(receiptRecord.getDiscountPercent())));
    }

    public void sellAdHocProduct(AdHocProductParams adHocProductParams, boolean takeAway) {
        GuardedTransaction.RunWithRefresh(adaptee, () -> {
            ProductAdapter adHocProduct = ProductAdapter.getAdHocProduct(EntityManagerProvider.getEntityManager());
            ReceiptRecord record = ReceiptRecord.builder()
                    .product(adHocProduct.getAdaptee())
                    .type(takeAway ? ReceiptRecordType.TAKE_AWAY : ReceiptRecordType.HERE)
                    .created(new GregorianCalendar())
                    .name(adHocProductParams.getName())
                    .soldQuantity(adHocProductParams.getQuantity())
                    .purchasePrice(adHocProductParams.getPurchasePrice())
                    .salePrice(adHocProductParams.getSalePrice())
                    .VAT(VATAdapter.getVatByName(takeAway ? ReceiptRecordType.TAKE_AWAY : ReceiptRecordType.HERE, VATStatus.VALID).getAdaptee().getVAT())
                    .discountPercent(0)
                    .build();
            record.setOwner(adaptee);
            adaptee.getRecords().add(record);
        });
    }

    public void sellGameFee(int quantity) {
        GuardedTransaction.RunWithRefresh(adaptee, () -> {
            ProductAdapter gameFeeProduct = ProductAdapter.getGameFeeProduct(EntityManagerProvider.getEntityManager());
            ReceiptRecord record = ReceiptRecord.builder()
                    .product(gameFeeProduct.getAdaptee())
                    .type(ReceiptRecordType.HERE)
                    .created(new GregorianCalendar())
                    .name(gameFeeProduct.getAdaptee().getLongName())
                    .soldQuantity(quantity)
                    .purchasePrice(gameFeeProduct.getAdaptee().getPurchasePrice())
                    .salePrice(gameFeeProduct.getAdaptee().getSalePrice())
                    .VAT(VATAdapter.getVatByName(ReceiptRecordType.HERE, VATStatus.VALID).getAdaptee().getVAT())
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
                    .collect(Collectors.toList());
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

    public ReceiptRecordAdapter cloneReceiptRecordAdapter(ReceiptRecordAdapter record, double amount) {
        final ReceiptRecord[] receiptRecord = new ReceiptRecord[1];
        GuardedTransaction.RunWithRefresh(adaptee, () -> {
            receiptRecord[0] = ReceiptRecord.builder()
                    .owner(this.getAdaptee())
                    .product(record.getAdaptee().getProduct())
                    .type(record.getAdaptee().getType())
                    .created(Calendar.getInstance())
                    .name(record.getAdaptee().getName())
                    .soldQuantity(amount)
                    .purchasePrice(record.getAdaptee().getPurchasePrice())
                    .salePrice(record.getAdaptee().getSalePrice())
                    .VAT(record.getAdaptee().getVAT())
                    .discountPercent(record.getAdaptee().getDiscountPercent())
                    .build();
            this.getAdaptee().getRecords().add(receiptRecord[0]);
        });
        return new ReceiptRecordAdapter(receiptRecord[0]);
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
