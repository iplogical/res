package com.inspirationlogical.receipt.corelib.model.entity;


import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.RollbackException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RecipeTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testRecipeCreation() {
        assertEquals(3, getRecipes().size());
    }

    @Test
    public void testRecipeOwner() {
        assertEquals("productFour", getRecipes().get(0).getOwner().getLongName());
    }

    @Test(expected = RollbackException.class)
    public void recipeWithoutOwner() {
        GuardedTransaction.Run(()->schema.getElementThree().setOwner(null));
    }

    @Test(expected = RollbackException.class)
    public void recipeWithoutElement() {
        GuardedTransaction.Run(()->schema.getElementThree().setElement(null));
    }

    @Test(expected = RollbackException.class)
    public void recipeWithoutQuantityUnit() {
        GuardedTransaction.Run(()->schema.getElementThree().setQuantityUnit(null));
    }

    private List<Recipe> getRecipes() {
        @SuppressWarnings("unchecked")
        List<Recipe> entries = schema.getEntityManager().createNamedQuery(Recipe.GET_TEST_RECIPES).getResultList();
        return entries;
    }

}
