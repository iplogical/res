package com.inspirationlogical.receipt.corelib.service.receipt;

import com.inspirationlogical.receipt.corelib.model.adapter.VATAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.*;
import com.inspirationlogical.receipt.corelib.model.enums.*;
import com.inspirationlogical.receipt.corelib.model.listeners.StockListener;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.params.StockParams;
import com.inspirationlogical.receipt.corelib.repository.ProductRepository;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRepository;
import com.inspirationlogical.receipt.corelib.repository.TableRepository;
import com.inspirationlogical.receipt.corelib.repository.VATSerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;

@Service
public class ReceiptServiceStock {

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private VATSerieRepository vatSerieRepository;

    @Autowired
    private ReceiptService receiptService;

    public void updateStock(List<StockParams> paramsList, ReceiptType receiptType, StockListener.StockUpdateListener listener) {
        Receipt receipt = buildReceipt(receiptType);
        addStockRecords(receipt, paramsList);
        bindReceiptToTable(receipt);
        StockListener.addObserver(listener);

        receiptService.close(receipt, PaymentParams.builder().paymentMethod(PaymentMethod.CASH)
                .discountAbsolute(0)
                .discountPercent(0)
                .build());
    }

    private Receipt buildReceipt(ReceiptType type) {
        return Receipt.builder()
                .type(type)
                .status(ReceiptStatus.OPEN)
                .paymentMethod(PaymentMethod.CASH)
                .openTime(now())
                .VATSerie(vatSerieRepository.findFirstByStatus(VATStatus.VALID))
                .records(new ArrayList<>())
                .build();
    }

    private void bindReceiptToTable(Receipt receipt) {
        Table table = tableRepository.findAllByType(TableType.getTableType(receipt.getType())).get(0);
        receipt.setOwner(table);
        table.getReceipts().add(receipt);
    }

    private void addStockRecords(Receipt receipt, List<StockParams> paramsList) {
        paramsList.forEach(params -> {
            Product product = productRepository.findByLongName(params.getProductName());
            ReceiptRecord record = buildReceiptRecord(params, product);
            record.getCreatedList().add(ReceiptRecordCreated.builder().created(now()).owner(record).build());
            record.setOwner(receipt);
            receipt.getRecords().add(record);
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
