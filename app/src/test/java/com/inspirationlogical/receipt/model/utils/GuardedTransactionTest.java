package com.inspirationlogical.receipt.model.utils;

import com.inspirationlogical.receipt.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.model.EntityManagerFactoryRule;
import com.inspirationlogical.receipt.model.entity.Table;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Ferenc on 2017. 03. 11..
 */
public class GuardedTransactionTest {
    private EntityManager manager;

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule();

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void persistObjects() {
        manager = factory.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getTableNormal());
        manager.getTransaction().commit();
    }

    @Test
    public void test_if_exception_happens_inside_a_guarded_transaction_modifications_wont_be_visible() {
        int old_capacity = schema.getTableNormal().getCapacity();
        try{
            GuardedTransaction.Run(manager,() -> {
                schema.getTableNormal().setCapacity(old_capacity+1);
                throw new RuntimeException("test exception");
            });
        }catch (Exception e){}
        Table table = (Table)manager.createNamedQuery(Table.GET_TABLE_BY_NUMBER)
                .setParameter("number",schema.getTableNormal().getNumber())
                .getSingleResult();
        assertEquals(old_capacity,table.getCapacity());
    }

    @Test
    public void test_nested_guarded_transactions_can_be_executed_successfully(){
        int old_capacity = schema.getTableNormal().getCapacity();
        GuardedTransaction.Run(manager,() -> {
            schema.getTableNormal().setCapacity(schema.getTableNormal().getCapacity()+1);
            GuardedTransaction.Run(manager,()->{
                schema.getTableNormal().setCoordinateX(100);
            });
        });
        assertEquals(old_capacity+1,schema.getTableNormal().getCapacity());
        assertEquals(100,schema.getTableNormal().getCoordinateX());
    }

}
