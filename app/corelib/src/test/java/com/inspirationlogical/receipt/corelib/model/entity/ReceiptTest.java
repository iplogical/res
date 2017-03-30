package com.inspirationlogical.receipt.corelib.model.entity;

import static com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule.NUMBER_OF_RECEIPTS;
import static org.junit.Assert.assertEquals;

import java.util.GregorianCalendar;
import java.util.List;
import javax.persistence.RollbackException;

import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

public class ReceiptTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testReceiptCreation() {
        assertEquals(NUMBER_OF_RECEIPTS, getReceipts().size());
    }

    @Test(expected = RollbackException.class)
    public void receiptWithoutOwner() {
        GuardedTransaction.Run(()->
                schema.getReceiptSaleOne().setOwner(null));
    }

    @Test(expected = RollbackException.class)
    public void receiptWithoutType() {
        GuardedTransaction.Run(()->
                schema.getReceiptSaleOne().setType(null));
    }

    @Test(expected = RollbackException.class)
    public void receiptWithoutStatus() {
        GuardedTransaction.Run(()->
                schema.getReceiptSaleOne().setStatus(null));
    }

    @Test(expected = RollbackException.class)
    public void saleReceiptWithoutPaymentMethod() {
        GuardedTransaction.Run(()->
                schema.getReceiptSaleOne().setPaymentMethod(null));
    }

    @Test(expected = RollbackException.class)
    public void inventoryReceiptWithPaymentMethod() {
        GuardedTransaction.Run(()->
                schema.getReceiptInventory().setPaymentMethod(PaymentMethod.CASH));
    }

    @Test(expected = RollbackException.class)
    public void corruptClient() {
        GuardedTransaction.Run(()->
                schema.getReceiptSaleOne().getClient().setName(null));
    }

    @Test(expected = RollbackException.class)
    public void noVatSerie() {
        GuardedTransaction.Run(()->
                schema.getReceiptSaleOne().setVATSerie(null));
    }

    @Test(expected = RollbackException.class)
    public void moveVirtualReceiptToNormalTableTooManyOpen() {
        GuardedTransaction.Run(()-> {
            schema.getReceiptSaleThree().setOwner(schema.getTableNormal());
            schema.getTableNormal().getReceipt().add(schema.getReceiptSaleThree());});
    }

    @Test(expected = RollbackException.class)
    public void saleReceiptWithInvalidOwner() {
            GuardedTransaction.Run(()->
                    schema.getReceiptSaleOne().setOwner(schema.getTableDisposal()));
    }

    @Test(expected = RollbackException.class)
    public void purchaseReceiptWithInvalidOwner() {
            GuardedTransaction.Run(()->
                    schema.getReceiptPurchase().setOwner(schema.getTableDisposal()));
    }

    @Test(expected = RollbackException.class)
    public void inventoryReceiptWithInvalidOwner() {
            GuardedTransaction.Run(()->
                    schema.getReceiptInventory().setOwner(schema.getTableNormal()));
    }

    @Test(expected = RollbackException.class)
    public void disposalReceiptWithInvalidOwner() {
            GuardedTransaction.Run(()->
                    schema.getReceiptDisposal().setOwner(schema.getTableOther()));
    }

    @Test(expected = RollbackException.class)
    public void otherReceiptWithInvalidOwner() {
            GuardedTransaction.Run(()->
                    schema.getReceiptOther().setOwner(schema.getTableInventory()));
    }

    @Test(expected = RollbackException.class)
    public void openReceiptWithClosureTime() {
            GuardedTransaction.Run(()->
                    schema.getReceiptSaleOne().setClosureTime(new GregorianCalendar()));
    }

    @Test(expected = RollbackException.class)
    public void closedReceiptWithoutClosureTime() {
            GuardedTransaction.Run(()->
                    schema.getReceiptSaleTwo().setClosureTime(null));
    }

    private List<Receipt> getReceipts() {
        @SuppressWarnings("unchecked")
        List<Receipt> entries = schema.getEntityManager().createNamedQuery(Receipt.GET_TEST_RECEIPTS).getResultList();
        return entries;
    }
}
