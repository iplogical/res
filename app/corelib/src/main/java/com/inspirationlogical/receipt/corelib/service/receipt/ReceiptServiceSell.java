package com.inspirationlogical.receipt.corelib.service.receipt;

import com.inspirationlogical.receipt.corelib.exception.AdHocProductNotFoundException;
import com.inspirationlogical.receipt.corelib.exception.GameFeeProductNotFoundException;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecordCreated;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptRecordType;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.repository.ProductRepository;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRecordCreatedRepository;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRecordRepository;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRepository;
import com.inspirationlogical.receipt.corelib.service.product_category.ProductCategoryService;
import com.inspirationlogical.receipt.corelib.service.vat.VATService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.inspirationlogical.receipt.corelib.service.receipt.ReceiptService.getDiscountMultiplier;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

@Service
public class ReceiptServiceSell {

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private ReceiptRecordRepository receiptRecordRepository;

    @Autowired
    private ReceiptRecordCreatedRepository receiptRecordCreatedRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private VATService vatService;

    void sellProduct(TableView tableView, ProductView productView, int amount, boolean isTakeAway, boolean isGift) {
        Receipt openReceipt = receiptRepository.getOpenReceipt(tableView.getNumber());
        Product product = productRepository.findById(productView.getId());
        List<ReceiptRecordCreated>  recordsCreated =
                receiptRecordCreatedRepository.findRecentByTimestamp(productView.getLongName(), now().minusSeconds(5));
        if(recordsCreated.size() > 0) {
            increaseReceiptRecordSoldQuantity(product, recordsCreated.get(0).getOwner());
            return;
        }
        addNewReceiptRecord(openReceipt, product, amount, isTakeAway, isGift);
        receiptRepository.save(openReceipt);
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
                .VAT(vatService.getVatByName(isTakeAway ? ReceiptRecordType.TAKE_AWAY : ReceiptRecordType.HERE).getVAT())
                .createdList(new ArrayList<>())
                .build();
    }

    void sellAdHocProduct(TableView tableView, AdHocProductParams adHocProductParams, boolean takeAway) {
        Receipt openReceipt = receiptRepository.getOpenReceipt(tableView.getNumber());
        Product adHocProduct = getAdHocProduct();
        ReceiptRecord record = buildReceiptRecord(adHocProductParams, takeAway, adHocProduct);
        addCreatedListEntries(adHocProductParams.getQuantity(), record);
        record.setOwner(openReceipt);
        openReceipt.getRecords().add(record);
        receiptRepository.save(openReceipt);
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
                .VAT(vatService.getVatByName(takeAway ? ReceiptRecordType.TAKE_AWAY : ReceiptRecordType.HERE).getVAT())
                .discountPercent(0)
                .createdList(new ArrayList<>())
                .build();
    }

    private void addCreatedListEntries(int quantity, ReceiptRecord record) {
        for (int i = 0; i < quantity; i++) {
            record.getCreatedList().add(ReceiptRecordCreated.builder().created(now()).owner(record).build());
        }
    }

    void sellGameFee(TableView tableView, int quantity) {
        Receipt openReceipt = receiptRepository.getOpenReceipt(tableView.getNumber());
        Product gameFeeProduct = getGameFeeProduct();
        List<ReceiptRecordCreated>  records = receiptRecordCreatedRepository.findRecentByTimestamp(gameFeeProduct.getLongName(), now().minusSeconds(5));
        if(records.size() > 0) {
            ReceiptRecord record = records.get(0).getOwner();
            record.setSoldQuantity(record.getSoldQuantity() + 1);
            record.getCreatedList().add(ReceiptRecordCreated.builder().created(now()).owner(record).build());
            receiptRecordRepository.save(record);
            return;
        }
        ReceiptRecord record = buildReceiptRecord(quantity, gameFeeProduct);
        addCreatedListEntries(quantity, record);
        record.setOwner(openReceipt);
        openReceipt.getRecords().add(record);
        receiptRepository.save(openReceipt);
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
                .VAT(vatService.getVatByName(ReceiptRecordType.HERE).getVAT())
                .discountPercent(0)
                .createdList(new ArrayList<>())
                .build();
    }

    ReceiptRecordView getLatestGameFee(TableView tableView) {
        Receipt openReceipt = receiptRepository.getOpenReceipt(tableView.getNumber());
        List<ReceiptRecord> gameFeeProductList = openReceipt.getRecords().stream()
                .filter(receiptRecord -> receiptRecord.getProduct().getType().equals(ProductType.GAME_FEE_PRODUCT))
                .sorted(Comparator.comparing(this::getNewestCreated).reversed()).collect(toList());
        return new ReceiptRecordView(gameFeeProductList.get(0));
    }

    private LocalDateTime getNewestCreated(ReceiptRecord receiptRecord) {
        List<ReceiptRecordCreated> sortedCreatedList =  receiptRecord.getCreatedList();
        sortedCreatedList.sort(Comparator.comparing(ReceiptRecordCreated::getCreated).reversed());
        return sortedCreatedList.get(0).getCreated();
    }
}
