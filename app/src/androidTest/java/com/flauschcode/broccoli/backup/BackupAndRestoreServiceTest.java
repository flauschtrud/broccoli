package com.flauschcode.broccoli.backup;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.flauschcode.broccoli.BroccoliApplication;
import com.flauschcode.broccoli.DaggerMockApplicationComponent;
import com.flauschcode.broccoli.MockApplicationComponent;

import com.flauschcode.broccoli.category.Category;
import com.flauschcode.broccoli.category.CategoryRepository;
import com.flauschcode.broccoli.recipe.Recipe;
import com.flauschcode.broccoli.recipe.RecipeRepository;
import com.flauschcode.broccoli.recipe.images.RecipeImageService;
import com.flauschcode.broccoli.recipe.sharing.RecipeZipReader;
import com.flauschcode.broccoli.recipe.sharing.RecipeZipWriter;
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
import static com.flauschcode.broccoli.recipe.RecipeRepository.InsertionType.INSERT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class BackupAndRestoreServiceTest {

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

    private BackupService backupService ;
    private RestoreService restoreService;

    private ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);
    private ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);

    @Before
    public void setUp() {
        MockApplicationComponent component = DaggerMockApplicationComponent.builder()
                .application(getApplication())
                .build();
        component.inject(this);
        component.inject(getApplication());

        backupService = new BackupService(application, recipeZipWriter, recipeRepository, categoryRepository);
        restoreService = new RestoreService(application, recipeZipReader, recipeRepository, recipeImageService, categoryRepository);
    }

    private BroccoliApplication getApplication() {
        return (BroccoliApplication) getInstrumentation()
                .getTargetContext().getApplicationContext();
    }

    @Test
    public void backup_and_restore_roundtrip() throws InterruptedException, ExecutionException, IOException {
        Category originalCategory = new Category(5, "Hauptgerichte");

        Recipe originalRecipe = new Recipe();
        originalRecipe.setTitle("Lauchkuchen");

        ArrayList<Recipe> recipes = new ArrayList<>();
        recipes.add(originalRecipe);
        when(recipeRepository.findAll()).thenReturn(CompletableFuture.completedFuture(recipes));

        ArrayList<Category> categories = new ArrayList<>();
        categories.add(originalCategory);
        when(categoryRepository.findAll()).thenReturn(new MutableLiveData<>(categories));

        when(categoryRepository.retainNonExisting(anyList())).thenAnswer(i -> CompletableFuture.completedFuture(i.getArguments()[0]));
        when(categoryRepository.retainExisting(anyList())).thenAnswer(i -> CompletableFuture.completedFuture(i.getArguments()[0]));

        when(recipeRepository.insertOrUpdate(recipeCaptor.capture())).thenReturn(CompletableFuture.completedFuture(INSERT));
        when(categoryRepository.insertOrUpdate(categoryCaptor.capture())).thenReturn(CompletableFuture.completedFuture(null));

        Uri uri = backupService.backup();
        int numberOfRecipes = restoreService.restore(uri);

        assertThat(numberOfRecipes, is(1));

        Category category = categoryCaptor.getValue();
        assertThat(category.getName(), is("Hauptgerichte"));

        Recipe recipe = recipeCaptor.getValue();
        assertThat(recipe.getTitle(), is("Lauchkuchen"));

        verifyNoInteractions(recipeImageService);
    }
}