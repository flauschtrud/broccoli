package org.flauschhaus.broccoli.recipe.list;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.flauschhaus.broccoli.category.Category;
import org.flauschhaus.broccoli.category.CategoryRepository;
import org.flauschhaus.broccoli.recipe.Recipe;
import org.flauschhaus.broccoli.recipe.RecipeRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
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

    private Recipe firstRecipe = new Recipe();
    private Recipe secondRecipe = new Recipe();

    private LiveData<List<Recipe>> allRecipes = givenRecipesAsLiveData(firstRecipe, secondRecipe);
    private LiveData<List<Recipe>> filteredRecipes = givenRecipesAsLiveData(firstRecipe);

    private Observer<List<Recipe>> observer = recipes -> {};

    @InjectMocks
    private RecipeViewModel recipeViewModel;

    @Test
    public void test_get_all_recipes() {
        when(recipeRepository.findAll()).thenReturn(allRecipes);

        try {
            recipeViewModel.setFilter(Category.ALL);
            recipeViewModel.getRecipes().observeForever(observer);
            assertThat(recipeViewModel.getRecipes().getValue(), is(allRecipes.getValue()));
        } finally {
            recipeViewModel.getRecipes().removeObserver(observer);
        }
    }

    @Test
    public void test_get_all_recipes_implicitly() {
        when(recipeRepository.findAll()).thenReturn(allRecipes);

        try {
            recipeViewModel.getRecipes().observeForever(observer);
            assertThat(recipeViewModel.getRecipes().getValue(), is(allRecipes.getValue()));
        } finally {
            recipeViewModel.getRecipes().removeObserver(observer);
        }
    }

    @Test
    public void test_get_filtered_recipes() {
        Category filter = new Category("bla");
        when(recipeRepository.filterBy(filter)).thenReturn(filteredRecipes);

        try {
            recipeViewModel.setFilter(filter);
            recipeViewModel.getRecipes().observeForever(observer);
            assertThat(recipeViewModel.getRecipes().getValue(), is(filteredRecipes.getValue()));
        } finally {
            recipeViewModel.getRecipes().removeObserver(observer);
        }
    }

    @Test
    public void get_categories() {
        when(categoryRepository.findAll()).thenReturn(categories);
        assertThat(recipeViewModel.getCategories(), is(categories));
    }

    private static MutableLiveData<List<Recipe>> givenRecipesAsLiveData(Recipe... recipes) {
        List<Recipe> recipeList = new ArrayList<>(Arrays.asList(recipes));
        return new MutableLiveData<>(recipeList);
    }

}