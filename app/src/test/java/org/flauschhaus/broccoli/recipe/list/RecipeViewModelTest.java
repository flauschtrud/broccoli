package org.flauschhaus.broccoli.recipe.list;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import org.flauschhaus.broccoli.category.Category;
import org.flauschhaus.broccoli.category.CategoryRepository;
import org.flauschhaus.broccoli.recipe.Recipe;
import org.flauschhaus.broccoli.recipe.RecipeRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

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

    @InjectMocks
    private RecipeViewModel recipeViewModel;

    private ArgumentCaptor<RecipeRepository.SearchCriteria> criteriaArgumentCaptor = ArgumentCaptor.forClass(RecipeRepository.SearchCriteria.class);
    private Observer<List<Recipe>> observer = recipes -> {};

    @Test
    public void test_get_recipes_for_criteria() {
        when(recipeRepository.find(criteriaArgumentCaptor.capture())).thenReturn(recipes);

        try {
            Category filter = new Category(5L, "Bla");
            recipeViewModel.setFilter(filter);
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
            assertThat(criteria.getCategory(), is(Category.ALL));
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