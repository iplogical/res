package com.inspirationlogical.receipt.corelib.service.receipt;

import com.inspirationlogical.receipt.corelib.exception.AdHocProductNotFoundException;
import com.inspirationlogical.receipt.corelib.exception.GameFeeProductNotFoundException;
import com.inspirationlogical.receipt.corelib.model.adapter.VATAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecordCreated;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptRecordType;
import com.inspirationlogical.receipt.corelib.model.enums.VATStatus;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordViewImpl;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.repository.ProductRepository;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRecordRepository;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRepository;
import com.inspirationlogical.receipt.corelib.service.product_category.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase.getDiscountMultiplier;
import static java.time.LocalDateTime.now;

@Service
public class ReceiptServiceSell {

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private ReceiptRecordRepository receiptRecordRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryService productCategoryService;

    public void sellProduct(Receipt receipt, ProductView productView, int amount, boolean isTakeAway, boolean isGift) {
        Product product = productRepository.getOne(productView.getId());
        List<ReceiptRecord> records = receiptRecordRepository.getReceiptRecordByTimestamp(productView.getLongName(), now().minusSeconds(5));
        if(records.size() > 0) {
            increaseReceiptRecordSoldQuantity(product, records.get(0));
            return;
        }
        addNewReceiptRecord(receipt, product, amount, isTakeAway, isGift);
        receiptRepository.save(receipt);
    }

    private void increaseReceiptRecordSoldQuantity(Product product, ReceiptRecord record) {
        record.setSoldQuantity(record.getSoldQuantity() + 1);
        record.getCreatedList().add(buildReceiptRecordCreated(record));
        record.setDiscountPercent(record.getDiscountPercent() == 100 ? 100 : productCategoryService.getDiscount(product.getCategory(), record));
        applyDiscountOnRecordSalePrices(record);
    }

    private ReceiptRecordCreated buildReceiptRecordCreated(ReceiptRecord record) {
        return ReceiptRecordCreated.builder().created(now()).owner(record).build();
    }

    private void applyDiscountOnRecordSalePrices(ReceiptRecord receiptRecord) {
        receiptRecord.setOriginalSalePrice(receiptRecord.getProduct().getSalePrice());
        receiptRecord.setSalePrice((int)Math.round(receiptRecord.getOriginalSalePrice() * getDiscountMultiplier(receiptRecord.getDiscountPercent())));
    }

    private void addNewReceiptRecord(Receipt receipt, Product product, int amount, boolean isTakeAway, boolean isGift) {
        ReceiptRecord record = buildReceiptRecord(product, amount, isTakeAway);
        record.getCreatedList().add(buildReceiptRecordCreated(record));
        record.setDiscountPercent(isGift ? 100 : productCategoryService.getDiscount(product.getCategory(), record));
        applyDiscountOnRecordSalePrices(record);
        record.setOwner(receipt);
        receipt.getRecords().add(record);
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

    public void sellAdHocProduct(Receipt receipt, AdHocProductParams adHocProductParams, boolean takeAway) {
        Product adHocProduct = getAdHocProduct();
        ReceiptRecord record = buildReceiptRecord(adHocProductParams, takeAway, adHocProduct);
        addCreatedListEntries(adHocProductParams.getQuantity(), record);
        record.setOwner(receipt);
        receipt.getRecords().add(record);
        receiptRepository.save(receipt);
    }

    private Product getAdHocProduct() {
        List<Product> adHocProductList = productRepository.findAllByType(ProductType.AD_HOC_PRODUCT);
        if (adHocProductList.isEmpty()) {
            throw new AdHocProductNotFoundException();
        }
        return adHocProductList.get(0);
    }

    private ReceiptRecord buildReceiptRecord(AdHocProductParams adHocProductParams, boolean takeAway, Product adHocProduct) {
        return ReceiptRecord.builder()
                .product(adHocProduct)
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

    private void addCreatedListEntries(int quantity, ReceiptRecord record) {
        for (int i = 0; i < quantity; i++) {
            record.getCreatedList().add(ReceiptRecordCreated.builder().created(now()).owner(record).build());
        }
    }

    public ReceiptRecordView sellGameFee(Receipt receipt, int quantity) {
        Product gameFeeProduct = getGameFeeProduct();
        List<ReceiptRecord> records = receiptRecordRepository.getReceiptRecordByTimestamp(gameFeeProduct.getLongName(), now().minusSeconds(2));

        if(records.size() > 0) {
            ReceiptRecord record = records.get(0);
            record.setSoldQuantity(record.getSoldQuantity() + 1);
            record.getCreatedList().add(ReceiptRecordCreated.builder().created(now()).owner(record).build());
            return new ReceiptRecordViewImpl(record);
        }
        ReceiptRecord record = buildReceiptRecord(quantity, gameFeeProduct);
        addCreatedListEntries(quantity, record);
        record.setOwner(receipt);
        receipt.getRecords().add(record);
        return new ReceiptRecordViewImpl(record);
    }

    private Product getGameFeeProduct() {
        List<Product> gameFeeProductList = productRepository.findAllByType(ProductType.GAME_FEE_PRODUCT);
        if(gameFeeProductList.isEmpty()) {
            throw new GameFeeProductNotFoundException();
        }
        return gameFeeProductList.get(0);
    }

    private ReceiptRecord buildReceiptRecord(int quantity, Product gameFeeProduct) {
        return ReceiptRecord.builder()
                .product(gameFeeProduct)
                .type(ReceiptRecordType.HERE)
                .name(gameFeeProduct.getLongName())
                .soldQuantity(quantity)
                .purchasePrice(gameFeeProduct.getPurchasePrice())
                .salePrice(gameFeeProduct.getSalePrice())
                .VAT(VATAdapter.getVatByName(ReceiptRecordType.HERE, VATStatus.VALID).getAdaptee().getVAT())
                .discountPercent(0)
                .createdList(new ArrayList<>())
                .build();
    }
}
