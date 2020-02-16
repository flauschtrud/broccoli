package org.flauschhaus.broccoli.ui.recipes;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.flauschhaus.broccoli.BroccoliApplication;
import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.recipes.RecipeRepository;

import java.util.List;

import javax.inject.Inject;

public class RecipesViewModel extends AndroidViewModel {

    @Inject
    RecipeRepository recipeRepository;

    private LiveData<List<Recipe>> recipes;

    public RecipesViewModel(Application application) {
        super(application);
        ((BroccoliApplication) application).getComponent().inject(this);
        recipes = recipeRepository.findAll();
    }

    LiveData<List<Recipe>> getRecipes() {
        return recipes;
    }
}