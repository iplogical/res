package com.inspirationlogical.receipt.corelib.service.receipt;

import com.inspirationlogical.receipt.corelib.model.adapter.ProductAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptRecordAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.VATAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecordCreated;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptRecordType;
import com.inspirationlogical.receipt.corelib.model.enums.VATStatus;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase.getDiscountMultiplier;
import static com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase.getReceiptRecordsByTimeStampAndName;
import static java.time.LocalDateTime.now;

@Service
public class ReceiptServiceSell {

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
}
