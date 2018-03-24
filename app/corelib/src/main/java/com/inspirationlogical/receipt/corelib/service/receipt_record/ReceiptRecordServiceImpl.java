package com.inspirationlogical.receipt.corelib.service.receipt_record;

import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecordCreated;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordViewImpl;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRecordCreatedRepository;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.inspirationlogical.receipt.corelib.utility.Round.roundToTwoDecimals;
import static java.time.LocalDateTime.now;

@Service
public class ReceiptRecordServiceImpl implements ReceiptRecordService {

    private static final Logger logger = LoggerFactory.getLogger(ReceiptRecordServiceImpl.class);

    @Autowired
    private ReceiptRecordRepository receiptRecordRepository;

    @Autowired
    private ReceiptRecordCreatedRepository receiptRecordCreatedRepository;

    @Override
    public void increaseSoldQuantity(ReceiptRecordView receiptRecordView, double amount, boolean isSale) {
        ReceiptRecord receiptRecord  = receiptRecordRepository.getOne(receiptRecordView.getId());
        logger.info("Increase quantity of a receiptRecord: name: {} soldQuantity: {}, amount: {}, size {}",  receiptRecord.getName(), receiptRecord.getSoldQuantity(), amount,  receiptRecord.getCreatedList().size());
        receiptRecord.setSoldQuantity(roundToTwoDecimals(receiptRecord.getSoldQuantity() + amount));
        if(isSale) {
            receiptRecord.getCreatedList().add(ReceiptRecordCreated.builder().created(now()).owner(receiptRecord).build());
        } else {
            receiptRecord.getCreatedList().add(ReceiptRecordCreated.builder().created(now().minusMinutes(25)).owner(receiptRecord).build());
        }
        receiptRecordRepository.save(receiptRecord);
    }

    @Override
    public void decreaseSoldQuantity(ReceiptRecordView receiptRecordView, double amount) {
        ReceiptRecord receiptRecord  = receiptRecordRepository.getOne(receiptRecordView.getId());
        if(receiptRecord.getSoldQuantity() - amount <= 0 || receiptRecord.getCreatedList().isEmpty())
        {
            logger.info("Delete a receiptRecord: {}", receiptRecord.getName());
            receiptRecord.getOwner().getRecords().remove(receiptRecord);
            receiptRecord.setOwner(null);
            receiptRecordRepository.delete(receiptRecord);
        } else {
            int size = receiptRecord.getCreatedList().size() - 1;
            logger.info("Decrease quantity of a receiptRecord: name: {} soldQuantity: {}, amount: {}, size: {}", receiptRecord.getName(), receiptRecord.getSoldQuantity(), amount, size);
            ReceiptRecordCreated toDelete = receiptRecord.getCreatedList().get(size);
            toDelete.setOwner(null);
            receiptRecord.getCreatedList().remove(toDelete);
            receiptRecordCreatedRepository.delete(toDelete);

            receiptRecord.setSoldQuantity(roundToTwoDecimals(receiptRecord.getSoldQuantity() - amount));
            receiptRecordRepository.save(receiptRecord);
        }
    }

    @Override
    public void cancelReceiptRecord(ReceiptRecordView receiptRecordView) {
        ReceiptRecord receiptRecord = receiptRecordRepository.getOne(receiptRecordView.getId());
        receiptRecord.getOwner().getRecords().remove(receiptRecord);
        receiptRecordRepository.delete(receiptRecord);
    }

    @Override
    public ReceiptRecordView cloneReceiptRecordAdapter(ReceiptRecordView receiptRecordView, double quantity) {
        ReceiptRecord receiptRecord = receiptRecordRepository.getOne(receiptRecordView.getId());

        ReceiptRecord cloneRecord = ReceiptRecord.builder()
                .owner(receiptRecord.getOwner())
                .product(receiptRecord.getProduct())
                .type(receiptRecord.getType())
                .name(receiptRecord.getName())
                .soldQuantity(quantity)
                .purchasePrice(receiptRecord.getPurchasePrice())
                .salePrice(receiptRecord.getSalePrice())
                .VAT(receiptRecord.getVAT())
                .discountPercent(receiptRecord.getDiscountPercent())
                .createdList(new ArrayList<>())
                .build();
        cloneRecord.getCreatedList().add(ReceiptRecordCreated.builder().created(now()).owner(cloneRecord).build());
        receiptRecord.getOwner().getRecords().add(cloneRecord);
        receiptRecordRepository.save(cloneRecord);
        return new ReceiptRecordViewImpl(cloneRecord);
    }
}
