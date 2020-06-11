package org.flauschhaus.broccoli.recipe;

import androidx.lifecycle.LiveData;
import androidx.room.Transaction;

import org.flauschhaus.broccoli.category.Category;
import org.flauschhaus.broccoli.recipe.images.RecipeImageService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RecipeRepository {

    private RecipeDAO recipeDAO;
    private RecipeImageService recipeImageService;

    private LiveData<List<Recipe>> allRecipes;

    public enum InsertionType {
        INSERT, UPDATE
    }

    @Inject
    RecipeRepository(RecipeDAO recipeDAO, RecipeImageService recipeImageService) {
        this.recipeDAO = recipeDAO;
        this.recipeImageService = recipeImageService;
        allRecipes = recipeDAO.findAll();
    }

    public LiveData<List<Recipe>> findAll() {
        return allRecipes;
    }

    public LiveData<List<Recipe>> filterBy(Category category) {
        return recipeDAO.filterBy(category.getCategoryId());
    }

    @Transaction
    public CompletableFuture<InsertionType> insertOrUpdate(Recipe recipe) {
        return CompletableFuture.supplyAsync(() -> {
            if (recipe.getRecipeId() == 0) {
                long recipeId = recipeDAO.insert(recipe.getCoreRecipe());
                recipe.getCategories().forEach(category -> recipeDAO.insert(new RecipeCategoryAssociation(recipeId, category.getCategoryId())));
                return InsertionType.INSERT;
            } else {
                recipeDAO.update(recipe.getCoreRecipe());
                List<RecipeCategoryAssociation> recipeCategoryAssociations = recipeDAO.getCategoriesFor(recipe.getRecipeId());
                recipeCategoryAssociations.forEach(recipeCategoryAssociation -> recipeDAO.delete(recipeCategoryAssociation));
                recipe.getCategories().forEach(category -> recipeDAO.insert(new RecipeCategoryAssociation(recipe.getRecipeId(), category.getCategoryId())));
                return InsertionType.UPDATE;
            }
        });
    }

    public CompletableFuture<Void> delete(Recipe recipe) {
        return CompletableFuture.allOf(
                recipeImageService.deleteImage(recipe.getImageName()),
                CompletableFuture.runAsync(() -> recipeDAO.delete(recipe.getCoreRecipe()))
        );
    }

}
