package org.flauschhaus.broccoli;

import androidx.lifecycle.ViewModel;

import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.recipes.RecipeRepository;

import javax.inject.Inject;

public class MainActivityViewModel extends ViewModel {

    private RecipeRepository recipeRepository;

    @Inject
    MainActivityViewModel(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    void insert(Recipe recipe) {
        recipeRepository.insert(recipe);
    }
}
