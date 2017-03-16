package com.inspirationlogical.receipt.model.entity;


import com.inspirationlogical.receipt.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.model.EntityManagerFactoryRule;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RecipeTest {

    private EntityManager manager;

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testRecipeCreation() {
        assertListSize();
    }

    @Test
    public void testRecipeOwner() {
        assertEquals("productFour", persistRecipeAndGetList().get(0).getOwner().getLongName());
    }

    @Test(expected = RollbackException.class)
    public void recipeWithoutOwner() {
        schema.getElementThree().setOwner(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void recipeWithoutElement() {
        schema.getElementThree().setElement(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void recipeWithoutQuantityUnit() {
        schema.getElementThree().setQuantityUnit(null);
        assertListSize();
    }

    private void assertListSize() {
        assertEquals(3, persistRecipeAndGetList().size());
    }

    private List<Recipe> persistRecipeAndGetList() {
        persistRecipe();
        @SuppressWarnings("unchecked")
        List<Recipe> entries = manager.createNamedQuery(Recipe.GET_TEST_RECIPES).getResultList();
        return entries;
    }

    private void persistRecipe() {
        manager = schema.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getElementOne());
        manager.persist(schema.getElementTwo());
        manager.persist(schema.getElementThree());
        manager.getTransaction().commit();
    }
}
