package com.inspirationlogical.receipt.corelib.model.utils;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Ferenc on 2017. 03. 11..
 */
public class GuardedTransactionTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();
    
    @Test
    public void test_if_exception_happens_inside_a_guarded_transaction_modifications_wont_be_visible() {
        int old_capacity = schema.getTableNormal().getCapacity();
        try{
            GuardedTransaction.Run(() -> {
                schema.getTableNormal().setCapacity(old_capacity+1);
                throw new RuntimeException("test exception");
            });
        }catch (Exception e){}
        Table table = (Table)schema.getEntityManager().createNamedQuery(Table.GET_TABLE_BY_NUMBER)
                .setParameter("number",schema.getTableNormal().getNumber())
                .getSingleResult();
        assertEquals(old_capacity,table.getCapacity());
    }

    @Test
    public void test_nested_guarded_transactions_can_be_executed_successfully(){
        int old_capacity = schema.getTableNormal().getCapacity();
        GuardedTransaction.Run(() -> {
            schema.getTableNormal().setCapacity(schema.getTableNormal().getCapacity()+1);
            GuardedTransaction.Run(()->{
                schema.getTableNormal().setCoordinateX(100);
            });
        });
        assertEquals(old_capacity+1,schema.getTableNormal().getCapacity());
        assertEquals(100,schema.getTableNormal().getCoordinateX());
    }

}
