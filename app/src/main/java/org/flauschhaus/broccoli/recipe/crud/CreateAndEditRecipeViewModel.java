package org.flauschhaus.broccoli.recipe.crud;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import org.flauschhaus.broccoli.category.Category;
import org.flauschhaus.broccoli.category.CategoryRepository;
import org.flauschhaus.broccoli.recipe.Recipe;
import org.flauschhaus.broccoli.recipe.RecipeRepository;
import org.flauschhaus.broccoli.recipe.images.RecipeImageService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class CreateAndEditRecipeViewModel extends ViewModel {

    private RecipeRepository recipeRepository;
    private RecipeImageService recipeImageService;
    private CategoryRepository categoryRepository;

    private Recipe recipe = new Recipe();
    private boolean isFinishedBySaving = false;
    private String newImageName;
    private String oldImageName;

    @Inject
    CreateAndEditRecipeViewModel(RecipeRepository recipeRepository, RecipeImageService recipeImageService, CategoryRepository categoryRepository) {
        this.recipeRepository = recipeRepository;
        this.recipeImageService = recipeImageService;
        this.categoryRepository = categoryRepository;
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

    void confirmImageHasBeenTaken() {
        setImageNameAndMarkDirty(newImageName);
    }

    void confirmImageHasBeenPicked(Uri uri) {
        removeOldImageAndCleanUpAnyTemporaryImages();
        recipeImageService.copyImage(uri).thenAccept(imageName -> {
            newImageName = imageName;
            setImageNameAndMarkDirty(imageName);
        });
    }

    void confirmImageHasBeenRemoved() {
        removeOldImageAndCleanUpAnyTemporaryImages();
        newImageName = null;
        setImageNameAndMarkDirty("");
    }

    boolean imageHasBeenSet() {
        return recipe.getImageName().length() > 0;
    }

    private void removeOldImageAndCleanUpAnyTemporaryImages() {
        cleanUpTemporaryImage();
        if (oldImageName == null && imageHasBeenSet()) {
            oldImageName = recipe.getImageName();
        }
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

    private void setImageNameAndMarkDirty(String imageName) {
        recipe.setImageName(imageName);
        recipe.setDirty(true);
    }

    LiveData<List<Category>> getCategories() {
        return categoryRepository.findAll();
    }

}
