package org.flauschhaus.broccoli.recipe.sharing;

import android.app.Application;

import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.recipe.Recipe;
import org.flauschhaus.broccoli.recipe.directions.DirectionBuilder;
import org.flauschhaus.broccoli.recipe.ingredients.IngredientBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RecipeSharingService {

    private Application application;

    @Inject
    RecipeSharingService(Application application) {
        this.application = application;
    }

    public String toPlainText(Recipe recipe) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(recipe.getTitle().toUpperCase()).append("\n\n");

        if (!"".equals(recipe.getServings())) {
            stringBuilder.append(getServingsString()).append(": ").append(recipe.getServings()).append("\n");
        }

        if (!"".equals(recipe.getPreparationTime())) {
            stringBuilder.append(getPreparationTimeString()).append(": ").append(recipe.getPreparationTime()).append("\n\n");
        }

        if(!"".equals(recipe.getDescription())) {
            stringBuilder.append(recipe.getDescription()).append("\n\n");
        }

        if(!"".equals(recipe.getIngredients())) {
            stringBuilder.append(getIngredientsString()).append(":\n");
            IngredientBuilder.from(recipe.getIngredients()).forEach(ingredient -> stringBuilder.append("- ").append(ingredient.getQuantity()).append(ingredient.getText()).append("\n"));
            stringBuilder.append("\n");
        }

        if(!"".equals(recipe.getDirections())) {
            stringBuilder.append(getDirectionsString()).append(":\n");
            DirectionBuilder.from(recipe.getDirections()).forEach(direction -> stringBuilder.append(direction.getPosition()).append(". ").append(direction.getText()).append("\n"));
            stringBuilder.append("\n");
        }

        stringBuilder.append("Shared via Broccoli"); // TODO insert link here and localize

        return stringBuilder.toString();
    }

    private String getServingsString() {
        return application.getString(R.string.hint_new_recipe_servings);
    }

    private String getPreparationTimeString() {
        return application.getString(R.string.hint_new_recipe_preparation_time);
    }

    private String getIngredientsString() {
        return application.getString(R.string.ingredients);
    }

    private String getDirectionsString() {
        return application.getString(R.string.directions);
    }
}
