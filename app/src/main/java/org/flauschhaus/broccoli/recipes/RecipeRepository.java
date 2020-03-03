package org.flauschhaus.broccoli.recipes;

import androidx.lifecycle.LiveData;

import org.flauschhaus.broccoli.BroccoliDatabase;
import org.flauschhaus.broccoli.recipes.images.RecipeImageService;

import java.util.List;

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

    public void insert(Recipe recipe) {
        BroccoliDatabase.getExecutorService().execute(() -> recipeDAO.insert(recipe));
    }

    public void delete(Recipe recipe) {
        recipeImageService.deleteImage(recipe.getImageName());
        BroccoliDatabase.getExecutorService().execute(() -> recipeDAO.delete(recipe));
    }

}
