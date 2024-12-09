package com.flauschcode.broccoli.recipe.sharing;

import android.app.Application;
import android.net.Uri;

import com.flauschcode.broccoli.recipe.directions.DirectionBuilder;
import com.flauschcode.broccoli.recipe.ingredients.IngredientBuilder;

import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.recipe.Recipe;
import com.flauschcode.broccoli.recipe.images.RecipeImageService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ShareableRecipeBuilder {

    private Application application;
    private RecipeImageService recipeImageService;

    @Inject
    ShareableRecipeBuilder(Application application, RecipeImageService recipeImageService) {
        this.application = application;
        this.recipeImageService = recipeImageService;
    }

    public ShareableRecipe from(Recipe recipe) {
        String plainText = toPlainText(recipe);
        Uri imageUri = "".equals(recipe.getImageName())? Uri.EMPTY : recipeImageService.getUri(recipe.getImageName());

        return new ShareableRecipe(plainText, imageUri);
    }

    private String toPlainText(Recipe recipe) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(recipe.getTitle().toUpperCase()).append("\n\n");

        if (!"".equals(recipe.getServings())) {
            stringBuilder.append(getServingsString()).append(": ").append(recipe.getServings()).append("\n");
        }

        if (!"".equals(recipe.getPreparationTime())) {
            stringBuilder.append(getPreparationTimeString()).append(": ").append(recipe.getPreparationTime()).append("\n");
        }

        if (!"".equals(recipe.getSource())) {
            stringBuilder.append(getSourceString()).append(": ").append(recipe.getSource()).append("\n");
        }

        if (!"".equals(recipe.getServings()) || !"".equals(recipe.getPreparationTime()) || !"".equals(recipe.getSource())) {
            stringBuilder.append("\n");
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

        stringBuilder.append(getSharedWithString());

        return stringBuilder.toString();
    }

    private String getServingsString() {
        return application.getString(R.string.servings);
    }

    private String getPreparationTimeString() {
        return application.getString(R.string.preparation_time);
    }

    private String getSourceString() {
        return application.getString(R.string.source);
    }

    private String getIngredientsString() {
        return application.getString(R.string.ingredients);
    }

    private String getDirectionsString() {
        return application.getString(R.string.directions);
    }

    private String getSharedWithString() {
        return application.getString(R.string.shared_with, application.getString(R.string.store_url));
    }

}
