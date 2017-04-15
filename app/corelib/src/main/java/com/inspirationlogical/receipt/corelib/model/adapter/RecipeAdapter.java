package com.inspirationlogical.receipt.corelib.model.adapter;

import java.util.List;

import com.inspirationlogical.receipt.corelib.model.entity.Recipe;

import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import lombok.NonNull;

import static java.util.stream.Collectors.toList;

public class RecipeAdapter extends AbstractAdapter<Recipe> {

    public RecipeAdapter(@NonNull Recipe adaptee) {
        super(adaptee);
    }


    public static List<RecipeAdapter> getRecipesOfProduct(ProductAdapter owner) {
        List<Recipe> recipes = GuardedTransaction.RunNamedQuery(Recipe.GET_RECIPES_OF_PRODUCT, query -> {
            query.setParameter("owner", owner.getAdaptee());
            return query;
        });
        return recipes.stream().map(RecipeAdapter::new).collect(toList());
    }
}
