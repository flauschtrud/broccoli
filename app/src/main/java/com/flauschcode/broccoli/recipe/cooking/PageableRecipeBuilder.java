package com.flauschcode.broccoli.recipe.cooking;

import android.app.Application;

import com.flauschcode.broccoli.recipe.directions.Direction;
import com.flauschcode.broccoli.recipe.directions.DirectionBuilder;
import com.flauschcode.broccoli.recipe.ingredients.Ingredient;
import com.flauschcode.broccoli.recipe.ingredients.IngredientBuilder;

import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.recipe.Recipe;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PageableRecipeBuilder {

    private Application application;

    @Inject
    PageableRecipeBuilder(Application application) {
        this.application = application;
    }

    public PageableRecipe from(Recipe recipe) {
        PageableRecipe pageableRecipe = new PageableRecipe();

        if ("".equals(recipe.getIngredients()) && "".equals(recipe.getDirections())) {
            pageableRecipe.addPage(new PageableRecipe.Page("", getNoDataString()));
            return pageableRecipe;
        }

        List<Ingredient> ingredients = IngredientBuilder.from(recipe.getIngredients());
        pageableRecipe.addPage(new PageableRecipe.Page(getIngredientsString(), ingredients.stream().map(ingredient -> ingredient.getQuantity() + ingredient.getText()).collect(Collectors.joining("\n"))));

        List<Direction> directions = DirectionBuilder.from(recipe.getDirections());
        directions.forEach(direction -> pageableRecipe.addPage(new PageableRecipe.Page(String.valueOf(direction.getPosition()), direction.getText())));

        pageableRecipe.addPage(new PageableRecipe.Page("", application.getString(R.string.enjoy_your_meal)));

        return pageableRecipe;
    }

    private String getIngredientsString() {
        return application.getString(R.string.ingredients);
    }

    private String getNoDataString() {
        return application.getString(R.string.no_ingredients_and_directions_yet);
    }
}
