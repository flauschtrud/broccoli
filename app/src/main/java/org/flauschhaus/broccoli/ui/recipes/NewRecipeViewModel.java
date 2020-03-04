package org.flauschhaus.broccoli.ui.recipes;

import androidx.lifecycle.ViewModel;

import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.recipes.RecipeRepository;
import org.flauschhaus.broccoli.recipes.images.RecipeImageService;

import java.io.File;
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
        cleanUpUnusedImageFile();
    }

    public Recipe getRecipe() {
        return recipe;
    }

    File createAndRememberImageFile() throws IOException {
        cleanUpUnusedImageFile();

        File photoFile = recipeImageService.createImage();
        imageName = photoFile.getName();
        return photoFile;
    }

    void applyImageFile() {
        recipe.setImageName(imageName);
    }

    void save() {
        recipeRepository.insert(recipe);
        imageName = null;
    }

    private void cleanUpUnusedImageFile() {
        if (imageName != null) {
            recipeImageService.deleteImage(imageName);
        }
    }

}
