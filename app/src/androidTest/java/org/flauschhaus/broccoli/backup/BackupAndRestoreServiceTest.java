package org.flauschhaus.broccoli.backup;

import android.app.Application;
import android.net.Uri;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.flauschhaus.broccoli.BroccoliApplication;
import org.flauschhaus.broccoli.DaggerMockApplicationComponent;
import org.flauschhaus.broccoli.MockApplicationComponent;
import org.flauschhaus.broccoli.category.CategoryRepository;
import org.flauschhaus.broccoli.recipe.Recipe;
import org.flauschhaus.broccoli.recipe.RecipeRepository;
import org.flauschhaus.broccoli.recipe.images.RecipeImageService;
import org.flauschhaus.broccoli.recipe.sharing.RecipeZipReader;
import org.flauschhaus.broccoli.recipe.sharing.RecipeZipWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.flauschhaus.broccoli.recipe.RecipeRepository.InsertionType.INSERT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class BackupAndRestoreServiceTest {

    private BackupService backupService ;
    private RestoreService restoreService;

    private ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);

    @Inject
    Application application;

    @Inject
    RecipeZipWriter recipeZipWriter;

    @Inject
    RecipeZipReader recipeZipReader;

    @Inject
    RecipeRepository recipeRepository;

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    RecipeImageService recipeImageService;

    @Before
    public void setUp() {
        MockApplicationComponent component = DaggerMockApplicationComponent.builder()
                .application(getApplication())
                .build();
        component.inject(this);
        component.inject(getApplication());

        backupService = new BackupService(application, recipeZipWriter, recipeRepository);
        restoreService = new RestoreService(application, recipeZipReader, recipeRepository, recipeImageService);
    }

    private BroccoliApplication getApplication() {
        return (BroccoliApplication) getInstrumentation()
                .getTargetContext().getApplicationContext();
    }

    @Test
    public void backup_and_restore_roundtrip() throws InterruptedException, ExecutionException, IOException {
        Recipe originalRecipe = new Recipe();
        originalRecipe.setTitle("Lauchkuchen");

        ArrayList<Recipe> recipes = new ArrayList<>();
        recipes.add(originalRecipe);
        when(recipeRepository.findAll()).thenReturn(CompletableFuture.completedFuture(recipes));

        when(categoryRepository.retainExisting(anyList())).thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));

        when(recipeRepository.insertOrUpdate(recipeCaptor.capture())).thenReturn(CompletableFuture.completedFuture(INSERT));

        Uri uri = backupService.backup((numberOfRecipes, i) -> {});
        restoreService.restore(uri, i -> {}, () -> {}, i -> {});

        Recipe recipe = recipeCaptor.getValue();
        assertThat(recipe.getTitle(), is("Lauchkuchen"));

        verifyNoInteractions(recipeImageService);
    }
}