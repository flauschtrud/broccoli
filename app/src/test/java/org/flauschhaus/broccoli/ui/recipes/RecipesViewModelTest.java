package org.flauschhaus.broccoli.ui.recipes;

import androidx.lifecycle.LiveData;

import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.recipes.RecipeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RecipesViewModelTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private LiveData<List<Recipe>> liveData;

    private RecipesViewModel recipesViewModel;

    @Test
    public void test_get_all_recipes() {
        when(recipeRepository.findAll()).thenReturn(liveData);

        recipesViewModel = new RecipesViewModel(recipeRepository);
        assertThat(recipesViewModel.getRecipes(), is(liveData));
    }

}