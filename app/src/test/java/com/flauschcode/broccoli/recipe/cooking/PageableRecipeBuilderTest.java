package com.flauschcode.broccoli.recipe.cooking;

import android.app.Application;

import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.recipe.Recipe;
import com.flauschcode.broccoli.recipe.cooking.PageableRecipe;
import com.flauschcode.broccoli.recipe.cooking.PageableRecipeBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PageableRecipeBuilderTest {

    @Mock
    private Application application;

    @InjectMocks
    private PageableRecipeBuilder pageableRecipeBuilder;

    @Before
    public void setUp() {
        when(application.getString(R.string.ingredients)).thenReturn("Ingredients");
        when(application.getString(R.string.no_ingredients_and_directions_yet)).thenReturn("Nothing there...");
        when(application.getString(R.string.not_scaled)).thenReturn("not scaled");
    }

    @Test
    public void to_pageable_recipe() {
        Recipe recipe = new Recipe();
        recipe.setTitle("Lauchkuchen");
        recipe.setIngredients("- 500g Mehl\n - 100g Margarine  ");
        recipe.setDirections(" 1. Erst dies. \n 2. Dann das. ");

        PageableRecipe pageableRecipe = pageableRecipeBuilder.from(recipe);

        assertThat(pageableRecipe.getPages().size(), is(3));

        assertPage(pageableRecipe.getPages().get(0), "Ingredients", "500g Mehl\n100g Margarine");
        assertPage(pageableRecipe.getPages().get(1), "1", "Erst dies.");
        assertPage(pageableRecipe.getPages().get(2), "2", "Dann das.");
    }

    @Test
    public void to_scaled_pageable_recipe() {
        Recipe recipe = new Recipe();
        recipe.setTitle("Lauchkuchen");
        recipe.setIngredients("- 500g Mehl\n - 1/2TL Salz\n  - je 1TL Gewürz");
        recipe.setDirections(" 1. Erst dies. \n 2. Dann das. ");

        PageableRecipe pageableRecipe = pageableRecipeBuilder.scale(2f).from(recipe);

        assertThat(pageableRecipe.getPages().size(), is(3));

        assertPage(pageableRecipe.getPages().get(0), "Ingredients", "1000g Mehl\n1TL Salz\nje 1TL Gewürz (not scaled)");
        assertPage(pageableRecipe.getPages().get(1), "1", "Erst dies.");
        assertPage(pageableRecipe.getPages().get(2), "2", "Dann das.");
    }

    @Test
    public void to_pageable_recipe_when_there_is_no_data() {
        Recipe recipe = new Recipe();
        recipe.setTitle("Lauchkuchen");

        PageableRecipe pageableRecipe = pageableRecipeBuilder.from(recipe);

        assertThat(pageableRecipe.getPages().size(), is(1));
        assertPage(pageableRecipe.getPages().get(0), "", "Nothing there...");
    }

    private void assertPage(PageableRecipe.Page page, String title, String text) {
        assertThat(page.getTitle(), is(title));
        assertThat(page.getText(), is(text));
    }
}