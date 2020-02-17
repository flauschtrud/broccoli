package org.flauschhaus.broccoli.ui.recipes;

import androidx.lifecycle.ViewModel;

import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.recipes.RecipeRepository;

import javax.inject.Inject;

public class NewRecipeViewModel extends ViewModel {

    private RecipeRepository recipeRepository;

    @Inject
    NewRecipeViewModel(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    void insert(Recipe recipe) {
        recipeRepository.insert(recipe);
    }
}
