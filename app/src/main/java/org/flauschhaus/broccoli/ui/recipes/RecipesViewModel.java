package org.flauschhaus.broccoli.ui.recipes;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.recipes.RecipeRepository;

import java.util.List;

public class RecipesViewModel extends AndroidViewModel {

    private LiveData<List<Recipe>> allRecipes;

    public RecipesViewModel(Application application) {
        super(application);
        RecipeRepository recipeRepository = new RecipeRepository(application);
        allRecipes = recipeRepository.findAll();
    }

    LiveData<List<Recipe>> getAllRecipes() {
        return allRecipes;
    }
}