package com.flauschcode.broccoli.category;

import com.flauschcode.broccoli.category.Category;
import com.flauschcode.broccoli.category.CategoryDAO;
import com.flauschcode.broccoli.category.CategoryRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Application;

import com.flauschcode.broccoli.backup.autoexport.AutoExportScheduler;

@RunWith(MockitoJUnitRunner.class)
public class CategoryRepositoryTest {

    @Mock
    private Application application;

    @Mock
    private CategoryDAO categoryDAO;

    @Mock
    private AutoExportScheduler autoExportScheduler;

    private CategoryRepository categoryRepository;

    @Before
    public void setUp() {
        categoryRepository = new CategoryRepository(application, categoryDAO, autoExportScheduler);
    }

    @Test
    public void insert_schedules_an_auto_export() throws ExecutionException, InterruptedException {
        Category category = new Category("Suppen");

        categoryRepository.insertOrUpdate(category).get();

        verify(autoExportScheduler).scheduleIfEnabled();
    }

    @Test
    public void update_schedules_an_auto_export() throws ExecutionException, InterruptedException {
        Category category = new Category(5, "Suppen");

        categoryRepository.insertOrUpdate(category).get();

        verify(autoExportScheduler).scheduleIfEnabled();
    }

    @Test
    public void delete_schedules_an_auto_export() throws ExecutionException, InterruptedException {
        Category category = new Category(5, "Suppen");

        categoryRepository.delete(category).get();

        verify(autoExportScheduler).scheduleIfEnabled();
    }

    @Test
    public void retain_existing() throws ExecutionException, InterruptedException {
        Category hauptgerichte = new Category(1, "Hauptgerichte");
        Category kuchen = new Category(12, "Kuchen");

        when(categoryDAO.searchByName("Hauptgerichte")).thenReturn(hauptgerichte);
        when(categoryDAO.searchByName("Suppen")).thenReturn(null);
        when(categoryDAO.searchByName("Kuchen")).thenReturn(kuchen);

        List<Category> categories = new ArrayList<>();
        categories.add(new Category(5, "Hauptgerichte"));
        categories.add(new Category(7, "Suppen"));
        categories.add(new Category(12, "Kuchen"));

        List<Category> retainedCategories = categoryRepository.retainExisting(categories).get();
        assertThat(retainedCategories, hasSize(2));
        assertThat(retainedCategories, hasItem(hauptgerichte));
        assertThat(retainedCategories, hasItem(kuchen));
    }

    @Test
    public void retain_non_existing() throws ExecutionException, InterruptedException {
        when(categoryDAO.searchByName("Hauptgerichte")).thenReturn(new Category(1, "Hauptgerichte"));
        when(categoryDAO.searchByName("Suppen")).thenReturn(null);
        when(categoryDAO.searchByName("Kuchen")).thenReturn(new Category(12, "Kuchen"));

        List<Category> categories = new ArrayList<>();
        Category suppen = new Category(7, "Suppen");
        categories.add(suppen);
        categories.add(new Category(5, "Hauptgerichte"));
        categories.add(new Category(12, "Kuchen"));

        List<Category> retainedCategories = categoryRepository.retainNonExisting(categories).get();
        assertThat(retainedCategories, hasSize(1));
        assertThat(retainedCategories, hasItem(suppen));
    }
}