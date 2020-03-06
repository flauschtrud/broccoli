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
    public void create_image_file_and_save() throws IOException {
        when(recipeImageService.createTemporaryImage()).thenReturn(imageUri);
        when(imageUri.getLastPathSegment()).thenReturn("blupp.jpg");

        newRecipeViewModel.createAndRememberImage();
        newRecipeViewModel.applyImageToRecipe();

        assertThat(newRecipeViewModel.getRecipe().getImageName(), is("blupp.jpg"));

        newRecipeViewModel.save();
        verify(recipeRepository).insert(newRecipeViewModel.getRecipe());
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
}