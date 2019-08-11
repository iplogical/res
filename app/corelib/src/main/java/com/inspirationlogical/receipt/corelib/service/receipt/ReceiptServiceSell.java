package com.inspirationlogical.receipt.corelib.service.receipt;

import com.inspirationlogical.receipt.corelib.model.entity.*;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptRecordType;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.repository.ProductRepository;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRecordRepository;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRepository;
import com.inspirationlogical.receipt.corelib.service.product_category.ProductCategoryService;
import com.inspirationlogical.receipt.corelib.service.vat.VATService;
import javafx.scene.control.Label;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private VATService vatService;

    void sellProduct(TableView tableView, ProductView productView, boolean takeAway, boolean gift) {
        Receipt openReceipt = receiptRepository.getOpenReceipt(tableView.getNumber());
        Product product = productRepository.findById(productView.getId());
        Optional<ReceiptRecord> optionalReceiptRecord = openReceipt.getRecords().stream()
                .filter(receiptRecord -> receiptRecord.getName().equals(product.getLongName()))
                .filter(receiptRecord -> (receiptRecord.getDiscountPercent() == 100) == gift)
                .filter(receiptRecord -> receiptRecord.getDiscountPercent() == 0)
                .findAny();
        ReceiptRecord record;
        if(optionalReceiptRecord.isPresent()) {
            record = optionalReceiptRecord.get();
            increaseReceiptRecordSoldQuantity(record);
        } else {
            record = addNewReceiptRecord(openReceipt, product, takeAway, gift);
        }
        record.setDiscountPercent(record.getDiscountPercent() == 100 ? 100 : getDiscountPercent(product, record));
        record.setSalePrice((int)Math.round(record.getOriginalSalePrice() * getDiscountMultiplier(record.getDiscountPercent())));
        mergeEquivalentRecords(openReceipt, record);
        receiptRepository.save(openReceipt);
    }

    private void mergeEquivalentRecords(Receipt openReceipt, ReceiptRecord record) {
        Optional<ReceiptRecord> receiptRecordOptional = openReceipt.getRecords().stream()
                .filter(receiptRecord -> receiptRecord.getName().equals(record.getName()))
                .filter(receiptRecord -> receiptRecord.getDiscountPercent() == record.getDiscountPercent())
                .filter(receiptRecord -> receiptRecord != record)
                .findAny();
        if(receiptRecordOptional.isPresent()) {
            ReceiptRecord equivalentRecord = receiptRecordOptional.get();
            record.setSoldQuantity(record.getSoldQuantity() + equivalentRecord.getSoldQuantity());
            record.getCreatedList().addAll(equivalentRecord.getCreatedList());
            equivalentRecord.getCreatedList().forEach(created -> created.setOwner(record));
            equivalentRecord.getCreatedList().clear();
            openReceipt.getRecords().remove(equivalentRecord);
            equivalentRecord.setOwner(null);
            receiptRecordRepository.delete(equivalentRecord);
        }
    }

    private double getDiscountPercent(Product product, ReceiptRecord record) {
        PriceModifier priceModifier = productCategoryService.getPriceModifier(product.getCategory(), record);
        if(priceModifier == null) {
            return 0;
        }
        if(priceModifier.getType().equals(PriceModifierType.SIMPLE_DISCOUNT)) {
            return priceModifier.getDiscountPercent();
        }
        if(priceModifier.getType().equals(PriceModifierType.QUANTITY_DISCOUNT)) {
            return (int)record.getSoldQuantity() == priceModifier.getQuantityLimit() ? priceModifier.getDiscountPercent() : 0;
        }
        return 0;
    }

    private void increaseReceiptRecordSoldQuantity(ReceiptRecord record) {
        record.setSoldQuantity(record.getSoldQuantity() + 1);
        record.getCreatedList().add(buildReceiptRecordCreated(record));
    }

    private ReceiptRecordCreated buildReceiptRecordCreated(ReceiptRecord record) {
        return ReceiptRecordCreated.builder().created(now()).owner(record).build();
    }

    private ReceiptRecord addNewReceiptRecord(Receipt receipt, Product product, boolean takeAway, boolean gift) {
        ReceiptRecord record = buildReceiptRecord(product, takeAway);
        record.getCreatedList().add(buildReceiptRecordCreated(record));
        record.setDiscountPercent(gift ? 100 : 0);
        record.setOwner(receipt);
        receipt.getRecords().add(record);
        return record;
    }

    private ReceiptRecord buildReceiptRecord(Product product, boolean isTakeAway) {
        return ReceiptRecord.builder()
                .product(product)
                .type(isTakeAway ? ReceiptRecordType.TAKE_AWAY : ReceiptRecordType.HERE)
                .name(product.getLongName())
                .soldQuantity(1)
                .purchasePrice(product.getPurchasePrice())
                .salePrice(product.getSalePrice())
                .originalSalePrice(product.getSalePrice())
                .VAT(isTakeAway ? product.getVATTakeAway(): product.getVATLocal())
                .createdList(new ArrayList<>())
                .build();
    }

    void sellAdHocProduct(TableView tableView, AdHocProductParams adHocProductParams, boolean takeAway) {
        Receipt openReceipt = receiptRepository.getOpenReceipt(tableView.getNumber());
        Product adHocProduct = productRepository.findFirstByType(ProductType.AD_HOC_PRODUCT);
        ReceiptRecord record = buildAdHocReceiptRecord(adHocProductParams, takeAway, adHocProduct);
        addCreatedListEntries(adHocProductParams.getQuantity(), record);
        record.setOwner(openReceipt);
        openReceipt.getRecords().add(record);
        receiptRepository.save(openReceipt);
    }

    private ReceiptRecord buildAdHocReceiptRecord(AdHocProductParams adHocProductParams, boolean takeAway, Product adHocProduct) {
        return ReceiptRecord.builder()
                .product(adHocProduct)
                .type(takeAway ? ReceiptRecordType.TAKE_AWAY : ReceiptRecordType.HERE)
                .name(adHocProductParams.getName())
                .soldQuantity(adHocProductParams.getQuantity())
                .purchasePrice(adHocProductParams.getPurchasePrice())
                .salePrice(adHocProductParams.getSalePrice())
                .originalSalePrice(adHocProductParams.getSalePrice())
                .VAT(takeAway ? adHocProduct.getVATTakeAway(): adHocProduct.getVATLocal())
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
        Product gameFeeProduct = productRepository.findFirstByType(ProductType.GAME_FEE_PRODUCT);
        Optional<ReceiptRecord> optionalGameFeeRecord = openReceipt.getRecords().stream()
                .filter(receiptRecord -> receiptRecord.getName().equals(gameFeeProduct.getLongName())).findAny();
        if(optionalGameFeeRecord.isPresent()) {
            ReceiptRecord gameFeeRecord = optionalGameFeeRecord.get();
            gameFeeRecord.setSoldQuantity(gameFeeRecord.getSoldQuantity() + quantity);
            gameFeeRecord.getCreatedList().add(buildReceiptRecordCreated(gameFeeRecord));
        } else {
            ReceiptRecord record = buildGameFeeReceiptRecord(quantity, gameFeeProduct);
            addCreatedListEntries(quantity, record);
            record.setOwner(openReceipt);
            openReceipt.getRecords().add(record);
        }
        receiptRepository.save(openReceipt);
    }

    private ReceiptRecord buildGameFeeReceiptRecord(int quantity, Product gameFeeProduct) {
        return ReceiptRecord.builder()
                .product(gameFeeProduct)
                .type(ReceiptRecordType.HERE)
                .name(gameFeeProduct.getLongName())
                .soldQuantity(quantity)
                .purchasePrice(gameFeeProduct.getPurchasePrice())
                .salePrice(gameFeeProduct.getSalePrice())
                .originalSalePrice(gameFeeProduct.getSalePrice())
                .VAT(gameFeeProduct.getVATLocal())
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
