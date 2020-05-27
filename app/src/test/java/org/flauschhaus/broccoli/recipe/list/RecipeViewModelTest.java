package org.flauschhaus.broccoli.recipe.list;

import androidx.lifecycle.LiveData;

import org.flauschhaus.broccoli.recipe.Recipe;
import org.flauschhaus.broccoli.recipe.RecipeRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RecipeViewModelTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private LiveData<List<Recipe>> liveData;

    private RecipeViewModel recipeViewModel;

    @Before
    public void setUp() {
        when(recipeRepository.findAll()).thenReturn(liveData);
        recipeViewModel = new RecipeViewModel(recipeRepository);
    }

    @Test
    public void test_get_all_recipes() {
        assertThat(recipeViewModel.getRecipes(), is(liveData));
    }

}