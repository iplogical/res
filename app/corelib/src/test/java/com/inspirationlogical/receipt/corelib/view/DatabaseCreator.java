package com.inspirationlogical.receipt.corelib.view;

import static org.junit.Assert.assertFalse;

import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.EntityManagerFactoryHolder;
import com.inspirationlogical.receipt.corelib.model.EntityManagerFactoryRule;
import com.inspirationlogical.receipt.corelib.model.TestType;
import com.inspirationlogical.receipt.corelib.model.entity.Restaurant;

/**
 * Created by Bálint on 2017.03.13..
 */
public class DatabaseCreator {

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule(TestType.CREATE);

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule("ProductionPersistance");

    @Test
    public void buildTestDatabase() {
        assertFalse(schema.getEntityManager().createNamedQuery(Restaurant.GET_ACTIVE_RESTAURANT).getResultList().isEmpty());
    }
}
