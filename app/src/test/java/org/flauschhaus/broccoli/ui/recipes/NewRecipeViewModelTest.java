package org.flauschhaus.broccoli.ui.recipes;

import android.net.Uri;

import org.flauschhaus.broccoli.recipes.RecipeRepository;
import org.flauschhaus.broccoli.recipes.images.RecipeImageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NewRecipeViewModelTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeImageService recipeImageService;

    @Mock
    private Uri imageUri;

    @InjectMocks
    private NewRecipeViewModel newRecipeViewModel;

    @Test
    public void initialize_recipe() {
        assertThat(newRecipeViewModel.getRecipe(), is(not(nullValue())));
    }

    @Test
    public void just_save() throws ExecutionException, InterruptedException {
        when(recipeRepository.insertOrUpdate(newRecipeViewModel.getRecipe())).thenReturn(CompletableFuture.completedFuture(RecipeRepository.InsertionType.INSERT));

        CompletableFuture<RecipeRepository.InsertionType> completableFuture = newRecipeViewModel.save();

        RecipeRepository.InsertionType insertionType = completableFuture.get();
        assertThat(insertionType, is(RecipeRepository.InsertionType.INSERT));

        verify(recipeRepository).insertOrUpdate(newRecipeViewModel.getRecipe());
        verify(recipeImageService, never()).deleteTemporaryImage(any());
    }

    @Test
    public void create_image_file_and_save() throws IOException, ExecutionException, InterruptedException {
        when(recipeImageService.createTemporaryImage()).thenReturn(imageUri);
        when(imageUri.getLastPathSegment()).thenReturn("blupp.jpg");

        newRecipeViewModel.createAndRememberImage();
        newRecipeViewModel.confirmImageIsTaken();

        assertThat(newRecipeViewModel.getRecipe().getImageName(), is("blupp.jpg"));

        when(recipeImageService.moveImage("blupp.jpg")).thenReturn(CompletableFuture.completedFuture(null));
        when(recipeRepository.insertOrUpdate(newRecipeViewModel.getRecipe())).thenReturn(CompletableFuture.completedFuture(RecipeRepository.InsertionType.INSERT));

        newRecipeViewModel.confirmFinishedBySaving();
        CompletableFuture<RecipeRepository.InsertionType> completableFuture = newRecipeViewModel.save();
        completableFuture.get();

        newRecipeViewModel.onCleared();

        verify(recipeRepository).insertOrUpdate(newRecipeViewModel.getRecipe());
        verify(recipeImageService).moveImage("blupp.jpg");
        verify(recipeImageService, never()).deleteTemporaryImage(any());
    }

    @Test
    public void clean_up_file_on_cancel() throws IOException {
        when(recipeImageService.createTemporaryImage()).thenReturn(imageUri);
        when(imageUri.getLastPathSegment()).thenReturn("blupp.jpg");

        newRecipeViewModel.createAndRememberImage();
        newRecipeViewModel.onCleared();

        verify(recipeImageService).deleteTemporaryImage("blupp.jpg");
    }

    @Test
    public void clean_up_file_if_replaced() throws IOException {
        when(recipeImageService.createTemporaryImage()).thenReturn(imageUri);
        when(imageUri.getLastPathSegment()).thenReturn("blupp.jpg");

        newRecipeViewModel.createAndRememberImage();
        newRecipeViewModel.createAndRememberImage();

        verify(recipeImageService).deleteTemporaryImage("blupp.jpg");
    }

    @Test
    public void remove_old_image_even_multiple_changes_are_made() throws IOException, ExecutionException, InterruptedException {
        newRecipeViewModel.getRecipe().setImageName("old.jpg");

        when(recipeImageService.createTemporaryImage()).thenReturn(imageUri);
        when(imageUri.getLastPathSegment()).thenReturn("blupp.jpg");

        newRecipeViewModel.createAndRememberImage();
        newRecipeViewModel.confirmImageIsTaken();
        newRecipeViewModel.createAndRememberImage();
        newRecipeViewModel.confirmImageIsTaken();
        newRecipeViewModel.removeOldImageAndCleanUpAnyTemporaryImages();

        when(recipeImageService.deleteImage("old.jpg")).thenReturn(CompletableFuture.completedFuture(null));
        when(recipeRepository.insertOrUpdate(newRecipeViewModel.getRecipe())).thenReturn(CompletableFuture.completedFuture(RecipeRepository.InsertionType.UPDATE));

        newRecipeViewModel.confirmFinishedBySaving();
        CompletableFuture<RecipeRepository.InsertionType> completableFuture = newRecipeViewModel.save();
        completableFuture.get();

        newRecipeViewModel.onCleared();

        verify(recipeRepository).insertOrUpdate(newRecipeViewModel.getRecipe());
        verify(recipeImageService).deleteImage("old.jpg");
    }

}