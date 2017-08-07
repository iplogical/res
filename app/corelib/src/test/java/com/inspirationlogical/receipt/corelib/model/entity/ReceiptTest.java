package com.inspirationlogical.receipt.corelib.model.entity;

import static com.inspirationlogical.receipt.corelib.model.BuildTestSchema.NUMBER_OF_RECEIPTS;
import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.RollbackException;

import com.inspirationlogical.receipt.corelib.model.AbstractTest;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

public class ReceiptTest extends AbstractTest {

    @Test
    public void testReceiptCreation() {
        assertEquals(NUMBER_OF_RECEIPTS, getReceipts().size());
    }

    @Test(expected = RollbackException.class)
    public void receiptWithoutOwner() {
        GuardedTransaction.run(()->
                schema.getReceiptSaleOne().setOwner(null));
    }

    @Test(expected = RollbackException.class)
    public void receiptWithoutType() {
        GuardedTransaction.run(()->
                schema.getReceiptSaleOne().setType(null));
    }

    @Test(expected = RollbackException.class)
    public void receiptWithoutStatus() {
        GuardedTransaction.run(()->
                schema.getReceiptSaleOne().setStatus(null));
    }

    @Test(expected = RollbackException.class)
    public void rceiptWithoutPaymentMethod() {
        GuardedTransaction.run(()->
                schema.getReceiptSaleOne().setPaymentMethod(null));
    }

    @Test(expected = RollbackException.class)
    public void noVatSerie() {
        GuardedTransaction.run(()->
                schema.getReceiptSaleOne().setVATSerie(null));
    }

    @Test(expected = RollbackException.class)
    public void moveLoitererReceiptToNormalTableTooManyOpen() {
        GuardedTransaction.run(()-> {
            schema.getReceiptSaleThree().setOwner(schema.getTableNormal());
            schema.getTableNormal().getReceipts().add(schema.getReceiptSaleThree());});
    }

    @Test(expected = RollbackException.class)
    public void saleReceiptWithInvalidOwner() {
            GuardedTransaction.run(()->
                    schema.getReceiptSaleOne().setOwner(schema.getTableDisposal()));
    }

    @Test(expected = RollbackException.class)
    public void purchaseReceiptWithInvalidOwner() {
            GuardedTransaction.run(()->
                    schema.getReceiptPurchase().setOwner(schema.getTableDisposal()));
    }

    @Test(expected = RollbackException.class)
    public void inventoryReceiptWithInvalidOwner() {
            GuardedTransaction.run(()->
                    schema.getReceiptInventory().setOwner(schema.getTableNormal()));
    }

    @Test(expected = RollbackException.class)
    public void disposalReceiptWithInvalidOwner() {
            GuardedTransaction.run(()->
                    schema.getReceiptDisposal().setOwner(schema.getTableOther()));
    }

    @Test(expected = RollbackException.class)
    public void otherReceiptWithInvalidOwner() {
            GuardedTransaction.run(()->
                    schema.getReceiptOther().setOwner(schema.getTableInventory()));
    }

    @Test(expected = RollbackException.class)
    public void openReceiptWithClosureTime() {
            GuardedTransaction.run(()->
                    schema.getReceiptSaleOne().setClosureTime(LocalDateTime.now()));
    }

    @Test(expected = RollbackException.class)
    public void closedReceiptWithoutClosureTime() {
            GuardedTransaction.run(()->
                    schema.getReceiptSaleTwo().setClosureTime(null));
    }

    private List<Receipt> getReceipts() {
        @SuppressWarnings("unchecked")
        List<Receipt> entries = schema.getEntityManager().createNamedQuery(Receipt.GET_RECEIPTS).getResultList();
        return entries;
    }
}
