package com.inspirationlogical.receipt.model.utils;

import com.inspirationlogical.receipt.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.model.EntityManagerFactoryRule;
import com.inspirationlogical.receipt.model.Table;
import com.inspirationlogical.receipt.model.adapter.TableAdapter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;

import java.util.List;

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

}
