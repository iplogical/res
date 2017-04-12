package com.inspirationlogical.receipt.corelib.model.entity;


import static com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule.NUMBER_OF_RECIPES;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.RollbackException;

import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

public class RecipeTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testRecipeCreation() {
        assertEquals(NUMBER_OF_RECIPES, getRecipes().size());
    }

    @Test
    public void testRecipeOwner() {
        assertEquals("productTwo", getRecipes().stream()
                .filter(recipe -> recipe.getOwner().getLongName().equals("productTwo"))
                .collect(Collectors.toList()).get(0).getComponent().getLongName());
    }

    @Test(expected = RollbackException.class)
    public void recipeWithoutOwner() {
        GuardedTransaction.Run(()->schema.getProductFourPartThree().setOwner(null));
    }

    @Test(expected = RollbackException.class)
    public void recipeWithoutElement() {
        GuardedTransaction.Run(()->schema.getProductFourPartThree().setComponent(null));
    }

    private List<Recipe> getRecipes() {
        @SuppressWarnings("unchecked")
        List<Recipe> entries = schema.getEntityManager().createNamedQuery(Recipe.GET_TEST_RECIPES).getResultList();
        return entries;
    }

}
