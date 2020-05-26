package org.flauschhaus.broccoli.ui.recipes;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.recipes.RecipeRepository;
import org.flauschhaus.broccoli.recipes.images.RecipeImageService;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class CreateAndEditRecipeViewModel extends ViewModel {

    private RecipeRepository recipeRepository;
    private RecipeImageService recipeImageService;

    private Recipe recipe = new Recipe();
    private boolean isFinishedBySaving = false;
    private String newImageName;
    private String oldImageName;

    @Inject
    CreateAndEditRecipeViewModel(RecipeRepository recipeRepository, RecipeImageService recipeImageService) {
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

    public String getNewImageName() {
        return newImageName;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public void setNewImageName(String newImageName) {
        this.newImageName = newImageName;
    }

    public String getOldImageName() {
        return oldImageName;
    }

    public void setOldImageName(String oldImageName) {
        this.oldImageName = oldImageName;
    }

    Uri createAndRememberImage() throws IOException {
        removeOldImageAndCleanUpAnyTemporaryImages();

        Uri photoFile = recipeImageService.createTemporaryImage();
        newImageName = photoFile.getLastPathSegment();
        return photoFile;
    }

    void confirmImageIsTaken() {
        recipe.setImageName(newImageName);
        recipe.setDirty(true);
    }

    boolean imageHasBeenTaken() {
        return recipe.getImageName().length() > 0;
    }

    void removeOldImageAndCleanUpAnyTemporaryImages() {
        cleanUpTemporaryImage();
        if (oldImageName == null && imageHasBeenTaken()) {
            oldImageName = recipe.getImageName();
        }
        newImageName = null;
        recipe.setImageName("");
        recipe.setDirty(true);
    }

    CompletableFuture<RecipeRepository.InsertionType> save() {
        recipe.setDirty(false);
        return CompletableFuture.completedFuture(true)
                .thenCompose(result -> oldImageName != null? recipeImageService.deleteImage(oldImageName) : CompletableFuture.completedFuture(result))
                .thenCompose(result -> newImageName != null? recipeImageService.moveImage(newImageName) : CompletableFuture.completedFuture(null))
                .thenCompose(v -> recipeRepository.insertOrUpdate(recipe));
    }

    void confirmFinishedBySaving() {
        this.isFinishedBySaving = true;
    }

    private void cleanUpTemporaryImage() {
        if (!isFinishedBySaving && newImageName != null) {
            recipeImageService.deleteTemporaryImage(newImageName);
        }
    }

}
