package org.flauschhaus.broccoli.recipes;

import androidx.lifecycle.LiveData;

import org.flauschhaus.broccoli.BroccoliDatabase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RecipeRepository {

    private RecipeDAO recipeDAO;

    private LiveData<List<Recipe>> allRecipes;

    @Inject
    RecipeRepository(RecipeDAO recipeDAO) {
        this.recipeDAO = recipeDAO;
        allRecipes = recipeDAO.findAll();
    }

    public LiveData<List<Recipe>> findAll() {
        return allRecipes;
    }

    public void insert(Recipe recipe) {
        BroccoliDatabase.getExecutorService().execute(() -> recipeDAO.insert(recipe));
    }

    public void deleteAll() {
        BroccoliDatabase.getExecutorService().execute(() -> recipeDAO.deleteAll());
    }
}
