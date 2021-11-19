package com.flauschcode.broccoli.recipe.cooking;

import android.app.Application;

import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.recipe.Recipe;
import com.flauschcode.broccoli.recipe.directions.Direction;
import com.flauschcode.broccoli.recipe.directions.DirectionBuilder;
import com.flauschcode.broccoli.recipe.ingredients.Ingredient;
import com.flauschcode.broccoli.recipe.ingredients.IngredientBuilder;
import com.flauschcode.broccoli.recipe.ingredients.ScaledQuantityBuilder;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class PageableRecipeBuilder {

    private static final float MINUS_ONE = -1.0f;

    private final Application application;
    private final ScaledQuantityBuilder scaledQuantityBuilder;

    private float scaleFactor = MINUS_ONE;

    @Inject
    PageableRecipeBuilder(Application application, ScaledQuantityBuilder scaledQuantityBuilder) {
        this.application = application;
        this.scaledQuantityBuilder = scaledQuantityBuilder;
    }

    public PageableRecipeBuilder scale(float scaleFactor) {
        this.scaleFactor = scaleFactor;
        return this;
    }

    public PageableRecipe from(Recipe recipe) {
        PageableRecipe pageableRecipe = new PageableRecipe();

        if ("".equals(recipe.getIngredients()) && "".equals(recipe.getDirections())) {
            pageableRecipe.addPage(new PageableRecipe.Page("", getNoDataString()));
            return pageableRecipe;
        }

        List<Ingredient> ingredients = IngredientBuilder.from(recipe.getIngredients());

        if (scaleFactor != MINUS_ONE) {
            ingredients.forEach(ingredient -> ingredient.setQuantity(scaledQuantityBuilder.from(ingredient.getQuantity(), scaleFactor)));
        }

        if (!ingredients.isEmpty()) {
            pageableRecipe.addPage(new PageableRecipe.Page(getIngredientsString(), ingredients.stream().map(ingredient -> ingredient.getQuantity() + ingredient.getText()).collect(Collectors.joining("\n"))));
        }

        List<Direction> directions = DirectionBuilder.from(recipe.getDirections());
        directions.forEach(direction -> pageableRecipe.addPage(new PageableRecipe.Page(String.valueOf(direction.getPosition()), direction.getText())));

        return pageableRecipe;
    }

    private String getIngredientsString() {
        return application.getString(R.string.ingredients);
    }

    private String getNoDataString() {
        return application.getString(R.string.no_ingredients_and_directions_message);
    }

}
