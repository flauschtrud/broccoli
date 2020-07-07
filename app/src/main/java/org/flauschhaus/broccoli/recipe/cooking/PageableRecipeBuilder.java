package org.flauschhaus.broccoli.recipe.cooking;

import android.app.Application;

import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.recipe.Recipe;
import org.flauschhaus.broccoli.recipe.cooking.PageableRecipe.Page;
import org.flauschhaus.broccoli.recipe.directions.Direction;
import org.flauschhaus.broccoli.recipe.directions.DirectionBuilder;
import org.flauschhaus.broccoli.recipe.ingredients.Ingredient;
import org.flauschhaus.broccoli.recipe.ingredients.IngredientBuilder;

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
            pageableRecipe.addPage(new Page("", getNoDataString()));
            return pageableRecipe;
        }

        List<Ingredient> ingredients = IngredientBuilder.from(recipe.getIngredients());
        pageableRecipe.addPage(new Page(getIngredientsString(), ingredients.stream().map(ingredient -> ingredient.getQuantity() + ingredient.getText()).collect(Collectors.joining("\n"))));

        List<Direction> directions = DirectionBuilder.from(recipe.getDirections());
        directions.forEach(direction -> pageableRecipe.addPage(new Page(String.valueOf(direction.getPosition()), direction.getText())));

        return pageableRecipe;
    }

    private String getIngredientsString() {
        return application.getString(R.string.ingredients);
    }

    private String getNoDataString() {
        return application.getString(R.string.no_ingredients_and_directions_yet);
    }
}
