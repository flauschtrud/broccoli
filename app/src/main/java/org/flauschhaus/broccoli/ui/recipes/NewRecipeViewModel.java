package org.flauschhaus.broccoli.ui.recipes;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import org.flauschhaus.broccoli.BroccoliApplication;
import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.recipes.RecipeRepository;

import javax.inject.Inject;

public class NewRecipeViewModel extends AndroidViewModel {

    @Inject
    RecipeRepository recipeRepository;

    public NewRecipeViewModel(Application application) {
        super(application);
        ((BroccoliApplication) application).getComponent().inject(this);
    }

    public void insert(Recipe recipe) {
        recipeRepository.insert(recipe);
    }
}
