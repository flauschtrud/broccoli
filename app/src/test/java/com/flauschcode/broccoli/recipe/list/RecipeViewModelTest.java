package com.flauschcode.broccoli.recipe.list;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.flauschcode.broccoli.category.Category;
import com.flauschcode.broccoli.category.CategoryRepository;
import com.flauschcode.broccoli.recipe.Recipe;
import com.flauschcode.broccoli.recipe.RecipeRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class RecipeViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private LiveData<List<Category>> categories;

    @Mock
    private LiveData<List<Recipe>> recipes;

    private RecipeViewModel recipeViewModel;

    private ArgumentCaptor<RecipeRepository.SearchCriteria> criteriaArgumentCaptor = ArgumentCaptor.forClass(RecipeRepository.SearchCriteria.class);
    private Observer<List<Recipe>> observer = recipes -> {};

    private final Category CATEGORY_ALL = new Category(-1, "All recipes");

    @Before
    public void setUp() {
        when(recipeRepository.getCategoryAll()).thenReturn(CATEGORY_ALL);
        recipeViewModel = new RecipeViewModel(recipeRepository, categoryRepository);
    }

    @Test
    public void test_get_recipes_for_criteria() {
        when(recipeRepository.find(criteriaArgumentCaptor.capture())).thenReturn(recipes);

        try {
            Category filter = new Category(5L, "Bla");
            recipeViewModel.setFilterCategory(filter);
            recipeViewModel.setSearchTerm("blupp");

            recipeViewModel.getRecipes().observeForever(observer);

            RecipeRepository.SearchCriteria criteria = criteriaArgumentCaptor.getValue();
            assertThat(criteria.getCategory(), is(filter));
            assertThat(criteria.getSearchTerm(), is("blupp"));
            assertThat(recipeViewModel.getRecipes().getValue(), is(recipes.getValue()));
        } finally {
            recipeViewModel.getRecipes().removeObserver(observer);
        }
    }

    @Test
    public void test_get_recipes_for_criteria_implicitly() {
        when(recipeRepository.find(criteriaArgumentCaptor.capture())).thenReturn(recipes);

        try {
            recipeViewModel.getRecipes().observeForever(observer);

            RecipeRepository.SearchCriteria criteria = criteriaArgumentCaptor.getValue();
            assertThat(criteria.getCategory(), is(CATEGORY_ALL));
            assertThat(criteria.getSearchTerm(), is(""));
            assertThat(recipeViewModel.getRecipes().getValue(), is(recipes.getValue()));
        } finally {
            recipeViewModel.getRecipes().removeObserver(observer);
        }
    }

    @Test
    public void get_categories() {
        when(categoryRepository.findAll()).thenReturn(categories);
        assertThat(recipeViewModel.getCategories(), is(categories));
    }

}