package com.flauschcode.broccoli.backup;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsIterableContaining.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.flauschcode.broccoli.category.Category;
import com.flauschcode.broccoli.category.CategoryRepository;
import com.flauschcode.broccoli.recipe.Recipe;
import com.flauschcode.broccoli.recipe.RecipeRepository;
import com.flauschcode.broccoli.recipe.sharing.RecipeZipWriter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RunWith(MockitoJUnitRunner.class)
public class BackupServiceTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private Application application;

    @Mock
    private RecipeZipWriter recipeZipWriter;

    @Mock
    private RecipeZipWriter.RecipeZipWriterBuilder recipeZipWriterBuilder;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private LiveData<List<Category>> categoriesLiveData;

    private BackupService backupService;

    @Before
    public void setUp() {
        backupService = new BackupService(application, recipeZipWriter, recipeRepository, categoryRepository);
        when(recipeZipWriter.write(any(Recipe.class))).thenReturn(recipeZipWriterBuilder);
        when(categoryRepository.findAll()).thenReturn(categoriesLiveData);
    }

    @Test
    public void writes_categories_and_one_entry_per_recipe() throws Exception {
        when(categoriesLiveData.getValue()).thenReturn(Collections.singletonList(new Category(1L, "Cakes")));

        Recipe recipe = new Recipe();
        recipe.setRecipeId(7L);
        recipe.setTitle("Apple Pie");
        when(recipeRepository.findAll()).thenReturn(CompletableFuture.completedFuture(Collections.singletonList(recipe)));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        backupService.writeArchive(outputStream);

        List<String> entryNames = zipEntryNames(outputStream);
        assertThat(entryNames, hasItem("categories.json"));
        assertThat(entryNames, hasItem("7_Apple_Pie.broccoli"));
        verify(recipeZipWriterBuilder).to(any());
    }

    @Test
    public void reports_progress_while_exporting() throws Exception {
        when(categoriesLiveData.getValue()).thenReturn(Collections.emptyList());

        Recipe first = new Recipe();
        first.setTitle("First");
        Recipe second = new Recipe();
        second.setTitle("Second");
        when(recipeRepository.findAll()).thenReturn(CompletableFuture.completedFuture(Arrays.asList(first, second)));

        backupService.writeArchive(new ByteArrayOutputStream());

        assertThat(backupService.getMaxRecipes().getValue(), is(2));
        assertThat(backupService.getCount().getValue(), is(2));
    }

    private List<String> zipEntryNames(ByteArrayOutputStream outputStream) throws Exception {
        List<String> names = new ArrayList<>();
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(outputStream.toByteArray()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                names.add(entry.getName());
            }
        }
        return names;
    }
}
