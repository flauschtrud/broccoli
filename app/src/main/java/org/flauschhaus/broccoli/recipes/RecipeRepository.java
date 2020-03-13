package org.flauschhaus.broccoli.recipes;

import androidx.lifecycle.LiveData;

import org.flauschhaus.broccoli.recipes.images.RecipeImageService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RecipeRepository {

    private RecipeDAO recipeDAO;
    private RecipeImageService recipeImageService;

    private LiveData<List<Recipe>> allRecipes;

    @Inject
    RecipeRepository(RecipeDAO recipeDAO, RecipeImageService recipeImageService) {
        this.recipeDAO = recipeDAO;
        this.recipeImageService = recipeImageService;
        allRecipes = recipeDAO.findAll();
    }

    public LiveData<List<Recipe>> findAll() {
        return allRecipes;
    }

    // TODO return status to determine which toast to show
    public CompletableFuture<Void> insertOrUpdate(Recipe recipe) {
        return CompletableFuture.runAsync(() -> {
            if (recipe.getId() == 0) {
                recipeDAO.insert(recipe);
            } else {
                recipeDAO.update(recipe); // TODO test
            }
        });
    }

    public CompletableFuture<Void> delete(Recipe recipe) {
        return CompletableFuture.allOf(
                recipeImageService.deleteImage(recipe.getImageName()),
                CompletableFuture.runAsync(() -> recipeDAO.delete(recipe))
        );
    }

}
