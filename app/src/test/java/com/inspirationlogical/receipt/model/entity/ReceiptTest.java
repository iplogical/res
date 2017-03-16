package com.inspirationlogical.receipt.model.entity;

import com.inspirationlogical.receipt.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.model.EntityManagerFactoryRule;
import com.inspirationlogical.receipt.model.enums.PaymentMethod;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ReceiptTest {

    private EntityManager manager;

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testReceiptCreation() {
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void receiptWithoutOwner() {
        schema.getReceiptSaleOne().setOwner(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void receiptWithoutType() {
        schema.getReceiptSaleOne().setType(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void receiptWithoutStatus() {
        schema.getReceiptSaleOne().setStatus(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void saleReceiptWithoutPaymentMethod() {
        schema.getReceiptSaleOne().setPaymentMethod(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void inventoryReceiptWithPaymentMethod() {
        schema.getReceiptInventory().setPaymentMethod(PaymentMethod.CASH);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void corruptClient() {
        schema.getReceiptSaleOne().getClient().setName(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void noVatSerie() {
        schema.getReceiptSaleOne().setVATSerie(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void moveVirtualReceiptToNormalTableTooManyOpen() {
        schema.getReceiptSaleThree().setOwner(schema.getTableNormal());
        schema.getTableNormal().getReceipt().add(schema.getReceiptSaleThree());
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void saleReceiptWithInvalidOwner() {
        schema.getReceiptSaleOne().setOwner(schema.getTableDisposal());
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void purchaseReceiptWithInvalidOwner() {
        schema.getReceiptPurchase().setOwner(schema.getTableDisposal());
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void inventoryReceiptWithInvalidOwner() {
        schema.getReceiptInventory().setOwner(schema.getTableNormal());
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void disposalReceiptWithInvalidOwner() {
        schema.getReceiptDisposal().setOwner(schema.getTableOther());
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void otherReceiptWithInvalidOwner() {
        schema.getReceiptOther().setOwner(schema.getTableInventory());
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void openReceiptWithClosureTime() {
        schema.getReceiptSaleOne().setClosureTime(new GregorianCalendar());
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void closedReceiptWithoutClosureTime() {
        schema.getReceiptSaleTwo().setClosureTime(null);
        assertListSize();
    }

    private void assertListSize() {
        assertEquals(8, persistReceiptAndGetList().size());
    }

    private List<Receipt> persistReceiptAndGetList() {
        persistReceipt();
        @SuppressWarnings("unchecked")
        List<Receipt> entries = manager.createNamedQuery(Receipt.GET_TEST_RECEIPTS).getResultList();
        return entries;
    }

    private void persistReceipt() {
        manager = schema.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getRestaurant());
        manager.getTransaction().commit();
    }
}
