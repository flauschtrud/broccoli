package com.flauschcode.broccoli.recipe.cooking;

import android.app.Application;

import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.recipe.Recipe;
import com.flauschcode.broccoli.recipe.directions.Direction;
import com.flauschcode.broccoli.recipe.directions.DirectionBuilder;
import com.flauschcode.broccoli.recipe.ingredients.Ingredient;
import com.flauschcode.broccoli.recipe.ingredients.IngredientBuilder;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class PageableRecipeBuilder {

    private static final float MINUS_ONE = -1.0f;

    private final Application application;

    private float scaleFactor = MINUS_ONE;

    @Inject
    PageableRecipeBuilder(Application application) {
        this.application = application;
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
            ingredients.forEach(this::scale);
        }

        pageableRecipe.addPage(new PageableRecipe.Page(getIngredientsString(), ingredients.stream().map(ingredient -> ingredient.getQuantity() + ingredient.getText()).collect(Collectors.joining("\n"))));

        List<Direction> directions = DirectionBuilder.from(recipe.getDirections());
        directions.forEach(direction -> pageableRecipe.addPage(new PageableRecipe.Page(String.valueOf(direction.getPosition()), direction.getText())));

        return pageableRecipe;
    }

    private void scale(Ingredient ingredient) {
        try {
            float quantity;
            if (ingredient.getQuantity().contains("/")) {
                String[] rat = ingredient.getQuantity().split("/");
                quantity = Float.parseFloat(rat[0]) / Float.parseFloat(rat[1]);
            } else {
                quantity = Float.parseFloat(ingredient.getQuantity());
            }
            ingredient.setQuantity(prettyPrint(quantity * scaleFactor));
        } catch (NumberFormatException e) {
            ingredient.setText(new StringBuilder(ingredient.getText()).append(" (").append(getNotScaledString()).append(")").toString());
        }
    }

    private static String prettyPrint(float f) {
        int i = (int) f;
        return f == i ? String.valueOf(i) : String.valueOf(f);
    }

    private String getIngredientsString() {
        return application.getString(R.string.ingredients);
    }

    private String getNoDataString() {
        return application.getString(R.string.no_ingredients_and_directions_yet);
    }

    private String getNotScaledString() {
        return application.getString(R.string.not_scaled);
    }

}
