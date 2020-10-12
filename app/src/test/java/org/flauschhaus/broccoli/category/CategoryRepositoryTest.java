package org.flauschhaus.broccoli.category;

import androidx.lifecycle.MutableLiveData;

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
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CategoryRepositoryTest {

    @Mock
    private CategoryDAO categoryDAO;

    private CategoryRepository categoryRepository;

    @Before
    public void setUp() {
        categoryRepository = new CategoryRepository(categoryDAO);
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
}