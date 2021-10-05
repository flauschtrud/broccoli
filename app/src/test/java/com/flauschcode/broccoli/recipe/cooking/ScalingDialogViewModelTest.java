package com.flauschcode.broccoli.recipe.cooking;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.flauschcode.broccoli.recipe.Recipe;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ScalingDialogViewModelTest {

    @Test(expected = IllegalArgumentException.class)
    public void without_recipe() {
        ScalingDialogViewModel viewModel = new ScalingDialogViewModel();

        viewModel.computeScaleFactor();
    }

    @Test
    public void with_empty_servings() {
        Recipe recipe = new Recipe();
        recipe.setServings(" ");

        ScalingDialogViewModel viewModel = new ScalingDialogViewModel();

        viewModel.setRecipe(recipe);

        assertThat(viewModel.isSimpleMode().get(), is(true));
        assertThat(viewModel.computeScaleFactor().isPresent(), is(true));
        assertThat(viewModel.computeScaleFactor().get(), is(1.0f));
    }

    @Test
    public void with_broken_servings() {
        Recipe recipe = new Recipe();
        recipe.setServings("ein paar Kekse");

        ScalingDialogViewModel viewModel = new ScalingDialogViewModel();

        viewModel.setRecipe(recipe);

        assertThat(viewModel.isSimpleMode().get(), is(true));
        assertThat(viewModel.computeScaleFactor().isPresent(), is(true));
        assertThat(viewModel.computeScaleFactor().get(), is(1.0f));
    }

    @Test
    public void initial_scale_factor_simple_mode() {
        Recipe recipe = new Recipe();
        recipe.setServings("4 Teller");

        ScalingDialogViewModel viewModel = new ScalingDialogViewModel();

        viewModel.setRecipe(recipe);

        assertThat(viewModel.isSimpleMode().get(), is(true));
        assertThat(viewModel.computeScaleFactor().isPresent(), is(true));
        assertThat(viewModel.computeScaleFactor().get(), is(1.0f));
    }

    @Test
    public void change_number_of_servings_in_simple_mode() {
        Recipe recipe = new Recipe();
        recipe.setServings("4 Teller");

        ScalingDialogViewModel viewModel = new ScalingDialogViewModel();

        viewModel.setRecipe(recipe);

        viewModel.getNumberOfServings().set("6");

        assertThat(viewModel.isSimpleMode().get(), is(true));
        assertThat(viewModel.computeScaleFactor().isPresent(), is(true));
        assertThat(viewModel.computeScaleFactor().get(), is(1.5f));
    }

    @Test
    public void change_number_of_servings_by_incrementing_and_decrementing() {
        Recipe recipe = new Recipe();
        recipe.setServings("4 Teller");

        ScalingDialogViewModel viewModel = new ScalingDialogViewModel();

        viewModel.setRecipe(recipe);

        viewModel.incrementNumberOfServings();
        viewModel.incrementNumberOfServings();
        viewModel.decrementNumberOfServings();

        assertThat(viewModel.isSimpleMode().get(), is(true));
        assertThat(viewModel.computeScaleFactor().isPresent(), is(true));
        assertThat(viewModel.computeScaleFactor().get(), is(1.25f));
    }

    @Test
    public void change_number_of_servings_by_decrementing_below_zero() {
        Recipe recipe = new Recipe();
        recipe.setServings("1 Teller");

        ScalingDialogViewModel viewModel = new ScalingDialogViewModel();

        viewModel.setRecipe(recipe);

        viewModel.decrementNumberOfServings();
        viewModel.decrementNumberOfServings();

        assertThat(viewModel.isSimpleMode().get(), is(true));
        assertThat(viewModel.computeScaleFactor().isPresent(), is(true));
        assertThat(viewModel.computeScaleFactor().get(), is(0f));
    }

    @Test
    public void initial_scale_factor_pro_mode() {
        Recipe recipe = new Recipe();
        recipe.setServings("4 Teller");

        ScalingDialogViewModel viewModel = new ScalingDialogViewModel();

        viewModel.setRecipe(recipe);
        viewModel.disableSimpleMode();

        assertThat(viewModel.isSimpleMode().get(), is(false));
        assertThat(viewModel.computeScaleFactor().isPresent(), is(true));
        assertThat(viewModel.computeScaleFactor().get(), is(1.0f));
    }

    @Test
    public void change_scale_factor_in_pro_mode() {
        Recipe recipe = new Recipe();
        recipe.setServings("4 Teller");

        ScalingDialogViewModel viewModel = new ScalingDialogViewModel();

        viewModel.setRecipe(recipe);
        viewModel.disableSimpleMode();
        viewModel.getScaleFactor().set("1.12");

        assertThat(viewModel.isSimpleMode().get(), is(false));
        assertThat(viewModel.computeScaleFactor().isPresent(), is(true));
        assertThat(viewModel.computeScaleFactor().get(), is(1.12f));
    }

}