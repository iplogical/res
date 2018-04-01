package com.inspirationlogical.receipt.corelib.service.product;

import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.Recipe;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.params.RecipeParams;
import com.inspirationlogical.receipt.corelib.repository.ProductRepository;
import com.inspirationlogical.receipt.corelib.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class RecipeServiceImpl {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private ProductRepository productRepository;

    public void updateRecipes(ProductView ownerView, List<RecipeParams> recipeParamsList) {
        Product owner = productRepository.getOne(ownerView.getId());
        List<Recipe> components = recipeRepository.findAllByOwner(owner);
        Map<String, Double> recipeParamsMap = getRecipesMap(recipeParamsList);
        List<Recipe> recipesToDelete = getRecipesToDelete(components, recipeParamsMap);
        components.removeAll(recipesToDelete);

        updateComponents(components, recipeParamsMap);
    }

    public void addRecipes(ProductView ownerView, List<RecipeParams> recipeParamsList) {
        Product owner = productRepository.getOne(ownerView.getId());
        List<Recipe> components = recipeRepository.findAllByOwner(owner);
        Map<String, Double> componentsMap = getComponentsMap(components);
        Map<String, Double> recipesToAdd = getRecipesToAdd(recipeParamsList, componentsMap);

        addComponents(owner, recipesToAdd);
    }

    public void deleteRecipes(ProductView ownerView, List<RecipeParams> recipeParamsList) {
        Product owner = productRepository.getOne(ownerView.getId());
        List<Recipe> components = recipeRepository.findAllByOwner(owner);
        Map<String, Double> recipeParamsMap = getRecipesMap(recipeParamsList);
        List<Recipe> recipesToDelete = getRecipesToDelete(components, recipeParamsMap);

        deleteComponents(owner, recipesToDelete);
    }

    private Map<String, Double> getComponentsMap(List<Recipe> components) {
        return components.stream().collect(toMap(component -> component.getComponent().getLongName(), Recipe::getQuantityMultiplier));
    }

    private Map<String, Double> getRecipesMap(List<RecipeParams> recipeParamsList) {
        return recipeParamsList.stream().collect(toMap(RecipeParams::getComponentName, RecipeParams::getQuantity));
    }

    private Map<String, Double> getRecipesToAdd(List<RecipeParams> recipeParamsList, Map<String, Double> componentsMap) {
        return recipeParamsList.stream()
                .filter(recipeParams -> !componentsMap.containsKey(recipeParams.getComponentName()))
                .collect(toMap(RecipeParams::getComponentName, RecipeParams::getQuantity));
    }

    private List<Recipe> getRecipesToDelete(List<Recipe> components, Map<String, Double> recipeParamsMap) {
        return components.stream()
                .filter(component -> !recipeParamsMap.containsKey(component.getComponent().getLongName()))
                .collect(toList());
    }

    private void updateComponents(List<Recipe> components, Map<String, Double> recipeParamsMap) {
        components.forEach(recipeAdapter -> {
            String componentName = recipeAdapter.getComponent().getLongName();
            recipeAdapter.setQuantityMultiplier(recipeParamsMap.get(componentName));
            GuardedTransaction.persist(recipeAdapter);
        });
    }

    private void deleteComponents(Product owner, List<Recipe> recipesToDelete) {
        recipesToDelete.forEach(toDelete -> {
            owner.getRecipes().remove(toDelete);
            recipeRepository.delete(toDelete);
        });
    }

    private void addComponents(Product owner, Map<String, Double> recipesToAdd) {
        recipesToAdd.forEach((key, value) -> {
            Product component = productRepository.findByLongName(key);
            addNewComponent(owner, component, value);
        });
    }

    private void addNewComponent(Product owner, Product component, Double value) {
        Recipe newComponent = Recipe.builder().component(component).quantityMultiplier(value).owner(owner).build();
        owner.getRecipes().add(newComponent);
        GuardedTransaction.persist(newComponent);
    }

}
