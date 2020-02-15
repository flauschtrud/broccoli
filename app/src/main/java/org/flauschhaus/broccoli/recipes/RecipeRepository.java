package org.flauschhaus.broccoli.recipes;

import android.app.Application;

import androidx.lifecycle.LiveData;

import org.flauschhaus.broccoli.BroccoliDatabase;

import java.util.List;

public class RecipeRepository {

    private RecipeDAO recipeDAO;
    private LiveData<List<Recipe>> allRecipes;

    public RecipeRepository(Application application) {
        BroccoliDatabase db = BroccoliDatabase.get(application);
        recipeDAO = db.getRecipeDAO();
        allRecipes = recipeDAO.findAll();
    }

    public LiveData<List<Recipe>> findAll() {
        return allRecipes;
    }

    public void insert(Recipe recipe) {
        BroccoliDatabase.getExecutorService().execute(() -> recipeDAO.insert(recipe));
    }
}
