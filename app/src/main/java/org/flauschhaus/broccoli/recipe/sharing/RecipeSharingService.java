package org.flauschhaus.broccoli.recipe.sharing;

import android.app.Application;

import org.flauschhaus.broccoli.recipe.Recipe;
import org.flauschhaus.broccoli.recipe.directions.DirectionBuilder;
import org.flauschhaus.broccoli.recipe.ingredients.IngredientBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RecipeSharingService {

    private Application application;

    @Inject
    public RecipeSharingService(Application application) {
        this.application = application;
    }

    public String toHtml(Recipe recipe) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<h1>").append(recipe.getTitle()).append("</h1>");
        stringBuilder.append("<p>").append(getServingsString()).append(recipe.getServings()).append("<br/>").append(getPreparationTimeString()).append(recipe.getPreparationTime()).append("</p>");
        stringBuilder.append("<p>").append(recipe.getDescription()).append("</p>");

        stringBuilder.append("<p>").append(getIngredientsString()).append("</p>").append("<ul>");
        IngredientBuilder.from(recipe.getIngredients()).forEach(ingredient -> stringBuilder.append("<li>").append(ingredient.getQuantity()).append(ingredient.getText()).append("</li>"));
        stringBuilder.append("</ul>");

        stringBuilder.append("<p>").append(getDirectionsString()).append("</p>").append("<ol>");
        DirectionBuilder.from(recipe.getDirections()).forEach(direction -> stringBuilder.append("<li>").append(direction.getText()).append("</li>"));
        stringBuilder.append("</ol>");

        stringBuilder.append("<p>Shared via Broccoli (TODO insert link)</p>");

        return stringBuilder.toString();
    }

    // TODO use android strings
    private String getServingsString() {
        return "Servings: ";
    }

    private String getPreparationTimeString() {
        return "Preparation time: ";
    }

    private String getIngredientsString() {
        return "Ingredients:";
    }

    private String getDirectionsString() {
        return "Directions:";
    }
}
