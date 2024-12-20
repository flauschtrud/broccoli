package com.flauschcode.broccoli.recipe.crud;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.net.Uri;

import androidx.lifecycle.LiveData;

import com.flauschcode.broccoli.category.Category;
import com.flauschcode.broccoli.category.CategoryRepository;
import com.flauschcode.broccoli.recipe.RecipeRepository;
import com.flauschcode.broccoli.recipe.images.RecipeImageService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RunWith(MockitoJUnitRunner.class)
public class CreateAndEditRecipeViewModelTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeImageService recipeImageService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private Uri imageUri;

    @Mock
    private LiveData<List<Category>> categories;

    @InjectMocks
    private CreateAndEditRecipeViewModel createAndEditRecipeViewModel;

    @Test
    public void initialize_recipe() {
        assertThat(createAndEditRecipeViewModel.getRecipe(), is(not(nullValue())));
    }

    @Test
    public void just_save() throws ExecutionException, InterruptedException {
        when(recipeRepository.insertOrUpdate(createAndEditRecipeViewModel.getRecipe())).thenReturn(CompletableFuture.completedFuture(5L));

        CompletableFuture<Long> completableFuture = createAndEditRecipeViewModel.save();

        Long recipeId = completableFuture.get();
        assertThat(recipeId, is(5L));

        verify(recipeRepository).insertOrUpdate(createAndEditRecipeViewModel.getRecipe());
        verify(recipeImageService, never()).deleteTemporaryImage(any());
    }

    @Test
    public void take_image_file_and_save() throws IOException, ExecutionException, InterruptedException {
        when(recipeImageService.createTemporaryImage()).thenReturn(imageUri);
        when(imageUri.getLastPathSegment()).thenReturn("blupp.jpg");

        createAndEditRecipeViewModel.createAndRememberImage();
        createAndEditRecipeViewModel.confirmImageHasBeenTaken();

        assertThat(createAndEditRecipeViewModel.getRecipe().getImageName(), is("blupp.jpg"));
        assertThat(createAndEditRecipeViewModel.getRecipe().isDirty(), is(true));

        when(recipeImageService.moveImage("blupp.jpg")).thenReturn(CompletableFuture.completedFuture(null));
        when(recipeRepository.insertOrUpdate(createAndEditRecipeViewModel.getRecipe())).thenReturn(CompletableFuture.completedFuture(5L));

        createAndEditRecipeViewModel.confirmFinishedBySaving();
        CompletableFuture<Long> completableFuture = createAndEditRecipeViewModel.save();
        completableFuture.get();

        createAndEditRecipeViewModel.onCleared();

        verify(recipeRepository).insertOrUpdate(createAndEditRecipeViewModel.getRecipe());
        verify(recipeImageService).moveImage("blupp.jpg");
        verify(recipeImageService, never()).deleteTemporaryImage(any());
    }

    @Test
    public void pick_image_file_and_save() throws ExecutionException, InterruptedException {
        when(recipeImageService.copyImage(imageUri)).thenReturn(CompletableFuture.completedFuture("blupp.jpg"));

        createAndEditRecipeViewModel.confirmImageHasBeenPicked(imageUri);

        assertThat(createAndEditRecipeViewModel.getRecipe().getImageName(), is("blupp.jpg"));
        assertThat(createAndEditRecipeViewModel.getRecipe().isDirty(), is(true));

        when(recipeImageService.moveImage("blupp.jpg")).thenReturn(CompletableFuture.completedFuture(null));
        when(recipeRepository.insertOrUpdate(createAndEditRecipeViewModel.getRecipe())).thenReturn(CompletableFuture.completedFuture(5L));

        createAndEditRecipeViewModel.confirmFinishedBySaving();
        CompletableFuture<Long> completableFuture = createAndEditRecipeViewModel.save();
        completableFuture.get();

        createAndEditRecipeViewModel.onCleared();

        verify(recipeRepository).insertOrUpdate(createAndEditRecipeViewModel.getRecipe());
        verify(recipeImageService).moveImage("blupp.jpg");
        verify(recipeImageService, never()).deleteTemporaryImage(any());
    }

    @Test
    public void duplicate_image() {
        when(recipeImageService.getUri("blupp.jpg")).thenReturn(imageUri);
        when(recipeImageService.copyImage(imageUri)).thenReturn(CompletableFuture.completedFuture("lala.jpg"));

        createAndEditRecipeViewModel.duplicateImage("blupp.jpg");

        assertThat(createAndEditRecipeViewModel.getRecipe().getImageName(), is("lala.jpg"));
        assertThat(createAndEditRecipeViewModel.getRecipe().isDirty(), is(true));
    }

    @Test
    public void clean_up_file_on_cancel() throws IOException {
        when(recipeImageService.createTemporaryImage()).thenReturn(imageUri);
        when(imageUri.getLastPathSegment()).thenReturn("blupp.jpg");

        createAndEditRecipeViewModel.createAndRememberImage();
        createAndEditRecipeViewModel.onCleared();

        verify(recipeImageService).deleteTemporaryImage("blupp.jpg");
    }

    @Test
    public void clean_up_file_if_replaced() throws IOException {
        when(recipeImageService.createTemporaryImage()).thenReturn(imageUri);
        when(imageUri.getLastPathSegment()).thenReturn("blupp.jpg");

        createAndEditRecipeViewModel.createAndRememberImage();
        createAndEditRecipeViewModel.createAndRememberImage();

        verify(recipeImageService).deleteTemporaryImage("blupp.jpg");
    }

    @Test
    public void remove_old_image_even_multiple_changes_are_made() throws IOException, ExecutionException, InterruptedException {
        createAndEditRecipeViewModel.getRecipe().setImageName("old.jpg");

        when(recipeImageService.createTemporaryImage()).thenReturn(imageUri);
        when(imageUri.getLastPathSegment()).thenReturn("blupp.jpg");

        createAndEditRecipeViewModel.createAndRememberImage();
        createAndEditRecipeViewModel.confirmImageHasBeenTaken();
        createAndEditRecipeViewModel.createAndRememberImage();
        createAndEditRecipeViewModel.confirmImageHasBeenTaken();
        createAndEditRecipeViewModel.confirmImageHasBeenRemoved();

        when(recipeImageService.deleteImage("old.jpg")).thenReturn(CompletableFuture.completedFuture(null));
        when(recipeRepository.insertOrUpdate(createAndEditRecipeViewModel.getRecipe())).thenReturn(CompletableFuture.completedFuture(5L));

        createAndEditRecipeViewModel.confirmFinishedBySaving();
        CompletableFuture<Long> completableFuture = createAndEditRecipeViewModel.save();
        completableFuture.get();

        createAndEditRecipeViewModel.onCleared();

        verify(recipeRepository).insertOrUpdate(createAndEditRecipeViewModel.getRecipe());
        verify(recipeImageService).deleteImage("old.jpg");
    }

    @Test
    public void get_categories() {
        when(categoryRepository.findAll()).thenReturn(categories);
        assertThat(createAndEditRecipeViewModel.getCategories(), is(categories));
    }

}