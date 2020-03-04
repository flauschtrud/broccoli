package org.flauschhaus.broccoli.ui.recipes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsNull.nullValue;

@RunWith(JUnit4.class)
public class NewRecipeViewModelTest {

    @Test
    public void remember_image_name() {
        NewRecipeViewModel newRecipeViewModel = new NewRecipeViewModel();
        assertThat(newRecipeViewModel.getRecipe(), is(not(nullValue())));

        newRecipeViewModel.rememberImageName("blupp.jpg");
        newRecipeViewModel.applyImageName();
        assertThat(newRecipeViewModel.getRecipe().getImageName(), is("blupp.jpg"));
    }

}