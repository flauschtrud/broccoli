package org.flauschhaus.broccoli.ui.recipes;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.recipes.RecipeRepository;
import org.flauschhaus.broccoli.recipes.images.RecipeImageService;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class NewRecipeViewModel extends ViewModel {

    private RecipeRepository recipeRepository;
    private RecipeImageService recipeImageService;

    private Recipe recipe = new Recipe();
    private boolean isFinishedBySaving = false;
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

    CompletableFuture<RecipeRepository.InsertionType> save() {
        if (imageName != null) {
            return recipeImageService.moveImage(imageName)
                    .thenCompose(v -> recipeRepository.insertOrUpdate(recipe));
        }
        return recipeRepository.insertOrUpdate(recipe);
    }

    void confirmFinishBySaving() {
        this.isFinishedBySaving = true;
    }

    private void cleanUpTemporaryImage() {
        if (!isFinishedBySaving && imageName != null) {
            recipeImageService.deleteTemporaryImage(imageName);
        }
    }

}
