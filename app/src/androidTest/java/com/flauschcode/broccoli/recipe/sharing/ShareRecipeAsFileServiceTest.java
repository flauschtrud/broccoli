package com.flauschcode.broccoli.recipe.sharing;

import android.app.Application;
import android.net.Uri;

import androidx.test.espresso.accessibility.AccessibilityChecks;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.flauschcode.broccoli.BroccoliApplication;
import com.flauschcode.broccoli.DaggerMockApplicationComponent;
import com.flauschcode.broccoli.MockApplicationComponent;
import com.flauschcode.broccoli.category.Category;
import com.flauschcode.broccoli.category.CategoryRepository;
import com.flauschcode.broccoli.recipe.Recipe;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class ShareRecipeAsFileServiceTest {

    @Inject
    Application application;

    @Inject
    RecipeZipWriter recipeZipWriter;

    @Inject
    RecipeZipReader recipeZipReader;

    @Inject
    CategoryRepository categoryRepository;

    private ShareRecipeAsFileService shareRecipeAsFileService;

    @Before
    public void setUp() {
        AccessibilityChecks.enable();

        MockApplicationComponent component = DaggerMockApplicationComponent.builder()
                .application(getApplication())
                .build();
        component.inject(this);
        component.inject(getApplication());

        shareRecipeAsFileService = new ShareRecipeAsFileService(application, recipeZipWriter, recipeZipReader);
    }

    private BroccoliApplication getApplication() {
        return (BroccoliApplication) getInstrumentation()
                .getTargetContext().getApplicationContext();
    }

    @Test
    public void share_as_file_roundtrip() throws IOException {
        Recipe originalRecipe = new Recipe();
        originalRecipe.setTitle("Lauchkuchen");
        originalRecipe.setFavorite(true);
        originalRecipe.setDirty(true);
        originalRecipe.addCategory(new Category(5, "Bla"));

        when(categoryRepository.retainExisting(anyList())).thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));

        Uri uri = shareRecipeAsFileService.shareAsFile(originalRecipe);
        Optional<Recipe> optionalRecipe = shareRecipeAsFileService.loadFromFile(uri);

        assertThat(optionalRecipe.isPresent(), is(true));
        Recipe recipeLoadedFromFile = optionalRecipe.get();
        assertThat(recipeLoadedFromFile.getTitle(), is("Lauchkuchen"));
        assertThat(recipeLoadedFromFile.isFavorite(), is(false));
        assertThat(recipeLoadedFromFile.isDirty(), is(false));
        assertThat(recipeLoadedFromFile.getCategories(), is(empty()));
    }

}