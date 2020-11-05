package com.flauschcode.broccoli.recipe.ingredients;

import com.flauschcode.broccoli.recipe.ingredients.Ingredient;
import com.flauschcode.broccoli.recipe.ingredients.IngredientBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;

@RunWith(JUnit4.class)
public class IngredientBuilderTest {

    private List<Ingredient> ingredients;

    @Test
    public void empty_string_should_yield_empty_list() {
        ingredients = IngredientBuilder.from("");
        assertThat(ingredients, is(empty()));
    }

    @Test
    public void null_should_yield_empty_list() {
        ingredients = IngredientBuilder.from(null);
        assertThat(ingredients, is(empty()));
    }

    @Test
    public void single_paragraph_should_yield_one_ingredient() {
        ingredients = IngredientBuilder.from("500g Mehl");
        assertThat(ingredients, hasSize(1));
        assertThat(ingredients, hasItem(new Ingredient("500", "g Mehl")));
    }

    @Test
    public void ingredients_should_be_split_by_new_lines() {
        ingredients = IngredientBuilder.from("500g Mehl Typ 405\n 8g Hefe\n 2 Stangen Lauch");
        assertThat(ingredients, hasSize(3));
        assertThat(ingredients, hasItem(new Ingredient("500", "g Mehl Typ 405")));
        assertThat(ingredients, hasItem(new Ingredient("8", "g Hefe")));
        assertThat(ingredients, hasItem(new Ingredient("2", " Stangen Lauch")));
    }

    @Test
    public void empty_lines_and_tabs_should_be_skipped() {
        ingredients = IngredientBuilder.from(" 500g Mehl    \n \n   8g Hefe \n \t 2 Stangen Lauch   ");
        assertThat(ingredients, hasSize(3));
        assertThat(ingredients, hasItem(new Ingredient("500", "g Mehl")));
        assertThat(ingredients, hasItem(new Ingredient("8", "g Hefe")));
        assertThat(ingredients, hasItem(new Ingredient("2", " Stangen Lauch")));
    }

    @Test
    public void fractions_should_be_detected() {
        ingredients = IngredientBuilder.from("0,5 Packungen Mehl\n 3/4 Cup Wasser\n 0.5 TL Salz");
        assertThat(ingredients, hasSize(3));
        assertThat(ingredients, hasItem(new Ingredient("0,5", " Packungen Mehl")));
        assertThat(ingredients, hasItem(new Ingredient("3/4", " Cup Wasser")));
        assertThat(ingredients, hasItem(new Ingredient("0.5", " TL Salz")));
    }

    @Test
    public void missing_quantity_should_be_tolerated() {
        ingredients = IngredientBuilder.from("Ganz viel Liebe.");
        assertThat(ingredients, hasSize(1));
        assertThat(ingredients, hasItem(new Ingredient("", "Ganz viel Liebe.")));
    }

    @Test
    public void leading_dashes_should_be_omitted() {
        ingredients = IngredientBuilder.from("-500g Mehl Typ 405\n - 8g Hefe\n – 2 Stangen Lauch");
        assertThat(ingredients, hasSize(3));
        assertThat(ingredients, hasItem(new Ingredient("500", "g Mehl Typ 405")));
        assertThat(ingredients, hasItem(new Ingredient("8", "g Hefe")));
        assertThat(ingredients, hasItem(new Ingredient("2", " Stangen Lauch")));
    }

    @Test
    public void range_quantities_should_be_detected() {
        ingredients = IngredientBuilder.from("2-3 Zwiebeln");
        assertThat(ingredients, hasSize(1));
        assertThat(ingredients, hasItem(new Ingredient("2-3", " Zwiebeln")));
    }
}