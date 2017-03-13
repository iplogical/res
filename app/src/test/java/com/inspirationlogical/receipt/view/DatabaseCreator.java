package com.inspirationlogical.receipt.view;

import com.inspirationlogical.receipt.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.model.EntityManagerFactoryRule;
import com.inspirationlogical.receipt.model.TestType;
import com.inspirationlogical.receipt.model.entity.Restaurant;
import com.inspirationlogical.receipt.service.RestaurantServices;
import com.inspirationlogical.receipt.service.RestaurantServicesImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Bálint on 2017.03.13..
 */
public class DatabaseCreator {
    private EntityManager manager;

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule(TestType.CREATE);

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void persistObjects() {
        manager = factory.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getRestaurant());
        manager.getTransaction().commit();
    }

    @Test
    public void buildTestDatabase() {
        assertNotNull(manager.createNamedQuery(Restaurant.GET_ACTIVE_RESTAURANT).getResultList());
    }
}
