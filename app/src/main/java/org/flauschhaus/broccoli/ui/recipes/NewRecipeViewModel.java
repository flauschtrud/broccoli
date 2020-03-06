package org.flauschhaus.broccoli.ui.recipes;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.recipes.RecipeRepository;
import org.flauschhaus.broccoli.recipes.images.RecipeImageService;

import java.io.IOException;

import javax.inject.Inject;

public class NewRecipeViewModel extends ViewModel {

    private RecipeRepository recipeRepository;
    private RecipeImageService recipeImageService;

    private Recipe recipe = new Recipe();
    private String imageName;

    @Inject
    NewRecipeViewModel(RecipeRepository recipeRepository, RecipeImageService recipeImageService) {
        this.recipeRepository = recipeRepository;
        this.recipeImageService = recipeImageService;
    }

    @Override
    public void onCleared() {
        cleanUpTemporaryImage();
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public String getImageName() {
        return imageName;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    Uri createAndRememberImage() throws IOException {
        cleanUpTemporaryImage();

        Uri photoFile = recipeImageService.createTemporaryImage();
        imageName = photoFile.getLastPathSegment();
        return photoFile;
    }

    void applyImageToRecipe() {
        recipe.setImageName(imageName);
    }

    void save() throws IOException {
        recipeRepository.insert(recipe);
        recipeImageService.moveImage(imageName);
        imageName = null;
    }

    private void cleanUpTemporaryImage() {
        if (imageName != null) {
            recipeImageService.deleteTemporaryImage(imageName);
        }
    }

}
