package org.flauschhaus.broccoli.ui.recipes;

import androidx.lifecycle.ViewModel;

import org.flauschhaus.broccoli.recipes.Recipe;

import javax.inject.Inject;

public class NewRecipeViewModel extends ViewModel {

    private Recipe recipe = new Recipe();
    private String imageName;

    @Inject
    NewRecipeViewModel() {
        // remove if DI is not needed in the future
    }

    public Recipe getRecipe() {
        return recipe;
    }

    void rememberImageName(String imageName) {
        this.imageName = imageName;
    }

    void applyImageName() {
        this.recipe.setImageName(imageName);
    }
}
