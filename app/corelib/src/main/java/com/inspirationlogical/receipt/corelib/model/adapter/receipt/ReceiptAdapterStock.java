package com.inspirationlogical.receipt.corelib.model.adapter.receipt;

import com.inspirationlogical.receipt.corelib.model.adapter.AbstractAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.VATAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecordCreated;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptRecordType;
import com.inspirationlogical.receipt.corelib.model.enums.VATStatus;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.params.StockParams;

import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;

public class ReceiptAdapterStock extends AbstractAdapter<Receipt> {
    public ReceiptAdapterStock(Receipt adaptee) {
        super(adaptee);
    }

    public void addStockRecords(List<StockParams> paramsList) {
        GuardedTransaction.run(() -> {
            paramsList.forEach(params -> {
                List<Product> productList = GuardedTransaction.runNamedQuery(Product.GET_PRODUCT_BY_NAME,
                        query -> {query.setParameter("longName", params.getProductName());
                            return query;});
                Product product = productList.get(0);
                ReceiptRecord record = buildReceiptRecord(params, product);
                record.getCreatedList().add(ReceiptRecordCreated.builder().created(now()).owner(record).build());
                record.setOwner(adaptee);
                adaptee.getRecords().add(record);
            });
        });
    }

    private ReceiptRecord buildReceiptRecord(StockParams params, Product product) {
        return ReceiptRecord.builder()
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
    }

}
