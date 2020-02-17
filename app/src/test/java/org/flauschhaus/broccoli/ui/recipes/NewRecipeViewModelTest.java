package org.flauschhaus.broccoli.ui.recipes;

import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.recipes.RecipeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class NewRecipeViewModelTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private Recipe recipe;

    @InjectMocks
    private NewRecipeViewModel newRecipeViewModel;

    @Test
    public void test_insert() {
        newRecipeViewModel.insert(recipe);
        verify(recipeRepository).insert(recipe);
    }

}