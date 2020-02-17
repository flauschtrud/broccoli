package org.flauschhaus.broccoli.ui.recipes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.recipes.RecipeRepository;

import java.util.List;

import javax.inject.Inject;

public class RecipesViewModel extends ViewModel {

    private LiveData<List<Recipe>> recipes;

    @Inject
    RecipesViewModel(RecipeRepository recipeRepository) {
        recipes = recipeRepository.findAll();
    }

    LiveData<List<Recipe>> getRecipes() {
        return recipes;
    }
}