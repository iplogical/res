package com.inspirationlogical.receipt.corelib.model.adapter.receipt;

import com.inspirationlogical.receipt.corelib.model.adapter.AbstractAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.ProductAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptRecordAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.VATAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecordCreated;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptRecordType;
import com.inspirationlogical.receipt.corelib.model.enums.VATStatus;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.utility.Wrapper;

import java.util.ArrayList;
import java.util.List;

import static com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase.getDiscountMultiplier;
import static com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase.getReceiptRecordsByTimeStampAndName;
import static java.time.LocalDateTime.now;

public class ReceiptAdapterSell extends AbstractAdapter<Receipt> {

    public ReceiptAdapterSell(Receipt adaptee) {
        super(adaptee);
    }

    public void sellProduct(ProductAdapter productAdapter, int amount, boolean isTakeAway, boolean isGift) {
        GuardedTransaction.run(() -> {
            List<Object[]> records = getReceiptRecordsByTimeStampAndName(productAdapter, now().minusSeconds(5));
            if(records.size() > 0) {
                increaseReceiptRecordSoldQuantity(productAdapter, records.get(0)[0]);
                return;
            }
            addNewReceiptRecord(productAdapter, amount, isTakeAway, isGift);
        });
    }

    private void addNewReceiptRecord(ProductAdapter productAdapter, int amount, boolean isTakeAway, boolean isGift) {
        ReceiptRecord record = buildReceiptRecord(productAdapter.getAdaptee(), amount, isTakeAway);
        record.getCreatedList().add(buildReceiptRecordCreated(record));
        record.setDiscountPercent(isGift ? 100 : productAdapter.getCategoryAdapter().getDiscount(new ReceiptRecordAdapter(record)));
        applyDiscountOnRecordSalePrices(record);
        record.setOwner(adaptee);
        adaptee.getRecords().add(record);
    }

    private ReceiptRecord buildReceiptRecord(Product product, int amount, boolean isTakeAway) {
        return ReceiptRecord.builder()
                .product(product)
                .type(isTakeAway ? ReceiptRecordType.TAKE_AWAY : ReceiptRecordType.HERE)
                .name(product.getLongName())
                .soldQuantity(amount)
                .purchasePrice(product.getPurchasePrice())
                .salePrice(product.getSalePrice())
                .VAT(VATAdapter.getVatByName(isTakeAway ? ReceiptRecordType.TAKE_AWAY : ReceiptRecordType.HERE, VATStatus.VALID).getAdaptee().getVAT())
                .createdList(new ArrayList<>())
                .build();
    }

    private void increaseReceiptRecordSoldQuantity(ProductAdapter productAdapter, Object o) {
        ReceiptRecord record = ((ReceiptRecord) o);
        record.setSoldQuantity(record.getSoldQuantity() + 1);
        record.getCreatedList().add(buildReceiptRecordCreated(record));
        record.setDiscountPercent(record.getDiscountPercent() == 100 ? 100 : productAdapter.getCategoryAdapter().getDiscount(new ReceiptRecordAdapter(record)));
        applyDiscountOnRecordSalePrices(record);
    }

    private ReceiptRecordCreated buildReceiptRecordCreated(ReceiptRecord record) {
        return ReceiptRecordCreated.builder().created(now()).owner(record).build();
    }

    private void applyDiscountOnRecordSalePrices(ReceiptRecord receiptRecord) {
        receiptRecord.setOriginalSalePrice(receiptRecord.getProduct().getSalePrice());
        receiptRecord.setSalePrice((int)Math.round(receiptRecord.getOriginalSalePrice() * getDiscountMultiplier(receiptRecord.getDiscountPercent())));
    }

    public void sellAdHocProduct(AdHocProductParams adHocProductParams, boolean takeAway) {
        GuardedTransaction.run(() -> {
            ProductAdapter adHocProduct = ProductAdapter.getAdHocProduct();
            ReceiptRecord record = buildReceiptRecord(adHocProductParams, takeAway, adHocProduct);
            addCreatedListEntries(adHocProductParams.getQuantity(), record);
            record.setOwner(adaptee);
            adaptee.getRecords().add(record);
        });
    }

    private ReceiptRecord buildReceiptRecord(AdHocProductParams adHocProductParams, boolean takeAway, ProductAdapter adHocProduct) {
        return ReceiptRecord.builder()
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
            ReceiptRecord record = buildReceiptRecord(quantity, gameFeeProduct);
            addCreatedListEntries(quantity, record);
            record.setOwner(adaptee);
            adaptee.getRecords().add(record);
            newRecordWrapper.setContent(record);
        });
        return new ReceiptRecordAdapter(newRecordWrapper.getContent());
    }

    private ReceiptRecord buildReceiptRecord(int quantity, ProductAdapter gameFeeProduct) {
        return ReceiptRecord.builder()
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
    }

    private void addCreatedListEntries(int quantity, ReceiptRecord record) {
        for (int i = 0; i < quantity; i++) {
            record.getCreatedList().add(ReceiptRecordCreated.builder().created(now()).owner(record).build());
        }
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

}
