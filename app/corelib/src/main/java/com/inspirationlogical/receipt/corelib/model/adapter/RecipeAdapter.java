package com.inspirationlogical.receipt.corelib.model.adapter;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.Recipe;

import lombok.NonNull;

public class RecipeAdapter extends AbstractAdapter<Recipe> {

    public RecipeAdapter(@NonNull Recipe adaptee) {
        super(adaptee);
    }

    public static RecipeAdapter getRecipeOfProduct(EntityManager manager, Product owner) {
        Query query = manager.createNamedQuery(Recipe.GET_RECIPE_OF_PRODUCT);
        query.setParameter("owner", owner);
        List<Recipe> recipes = (List<Recipe>) query.getResultList();
        if (recipes.isEmpty()) {
            return null;
        }
        return new RecipeAdapter(recipes.get(0));
    }

    public static List<RecipeAdapter> getRecipesOfProduct(EntityManager manager, Product owner) {
        Query query = manager.createNamedQuery(Recipe.GET_RECIPES_OF_PRODUCT);
        query.setParameter("owner", owner);
        return ((List<Recipe>) query.getResultList()).stream().map(RecipeAdapter::new).collect(Collectors.toList());
    }
}
