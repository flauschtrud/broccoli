package org.flauschhaus.broccoli.recipe.importing;

import android.util.Log;

import org.flauschhaus.broccoli.recipe.Recipe;
import org.flauschhaus.broccoli.recipe.images.RecipeImageService;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class ImportableRecipeBuilder {

    private static final String JSON_LD_RECIPE_NAME = "name";
    private static final String JSON_LD_RECIPE_DESCRIPTION = "description";
    private static final String JSON_LD_RECIPE_INGREDIENT = "recipeIngredient";
    private static final String JSON_LD_RECIPE_INSTRUCTIONS = "recipeInstructions";
    private static final String JSON_LD_RECIPE_HOW_TO_TEXT = "text";
    private static final String JSON_LD_RECIPE_RECIPE_YIELD = "recipeYield";
    private static final String JSON_LD_RECIPE_TOTAL_TIME = "totalTime";
    private static final String JSON_LD_RECIPE_COOK_TIME = "cookTime";
    private static final String JSON_LD_RECIPE_IMAGE = "image";

    private JSONObject recipeJson;
    private Recipe recipe = new Recipe();

    private RecipeImageService recipeImageService;

    public ImportableRecipeBuilder(RecipeImageService recipeImageService) {
        this.recipeImageService = recipeImageService;
    }

    ImportableRecipeBuilder withRecipeJsonLd(JSONObject jsonObject) {
        recipeJson = jsonObject;
        return this;
    }

    public ImportableRecipeBuilder from(String url) {
        recipe.setSource(url);
        return this;
    }

    Optional<Recipe> build() {
        if (recipeJson == null) {
            return Optional.empty();
        }

        contributeTitle();
        contributeDescription();
        contributeServings();
        contributePreparationTime();
        contributeIngredients();
        contributeDirections();
        contributeImage();

        return Optional.of(recipe);
    }

    private void contributeTitle() {
        recipe.setTitle(recipeJson.optString(JSON_LD_RECIPE_NAME));
    }

    private void contributeDescription() {
        recipe.setDescription(recipeJson.optString(JSON_LD_RECIPE_DESCRIPTION));
    }

    private void contributeServings() {
        JSONArray yieldArray = recipeJson.optJSONArray(JSON_LD_RECIPE_RECIPE_YIELD);
        if (yieldArray == null) {
            recipe.setServings(recipeJson.optString(JSON_LD_RECIPE_RECIPE_YIELD));
        } else {
            recipe.setServings(yieldArray.optString(0));
        }
    }

    private void contributePreparationTime() {
        if (recipeJson.has(JSON_LD_RECIPE_COOK_TIME)) {
            setParsedAndWordedPreparationTime(JSON_LD_RECIPE_COOK_TIME);
        }

        if (recipeJson.has(JSON_LD_RECIPE_TOTAL_TIME)) {
            setParsedAndWordedPreparationTime(JSON_LD_RECIPE_TOTAL_TIME);
        }
    }

    private void setParsedAndWordedPreparationTime(String jsonLdRecipeCookTime) {
        recipe.setPreparationTime(Period.parse(recipeJson.optString(jsonLdRecipeCookTime)).toString(PeriodFormat.wordBased()));
    }

    private void contributeIngredients() {
        JSONArray ingredientArray = recipeJson.optJSONArray(JSON_LD_RECIPE_INGREDIENT);
        if (ingredientArray !=  null) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < ingredientArray.length(); i++) {
                list.add(ingredientArray.optString(i));
            }
            recipe.setIngredients(list.stream().collect(Collectors.joining("\n")));
        }
    }

    private void contributeDirections() {
        JSONArray instructionsArray = recipeJson.optJSONArray(JSON_LD_RECIPE_INSTRUCTIONS);
        if (instructionsArray == null) {
            recipe.setDirections(recipeJson.optString(JSON_LD_RECIPE_INSTRUCTIONS));
        } else {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < instructionsArray.length(); i++) {
                JSONObject instruction = instructionsArray.optJSONObject(i);
                if (instruction == null) {
                    list.add(instructionsArray.optString(i));
                } else {
                    list.add(instruction.optString(JSON_LD_RECIPE_HOW_TO_TEXT));
                }
            }
            recipe.setDirections(list.stream().collect(Collectors.joining("\n")));
        }
    }

    private void contributeImage() {
        if(!recipeJson.has(JSON_LD_RECIPE_IMAGE)) {
            return;
        }

        URL url;
        try {
            String imageURL = getImageUrl();
            url = new URL(imageURL);
        } catch (MalformedURLException e) {
            Log.e(getClass().getName(), e.getMessage());
            return;
        }


        try {
            File tempFile = recipeImageService.createTemporaryImageFileInCache();
            recipe.setImageName(tempFile.getName());
            recipeImageService.downloadToCache(url, tempFile)
                    .exceptionally(e -> {
                        Log.e(getClass().getName(), e.getMessage());
                        return null;
                    });
        } catch (IOException e) {
            Log.e(getClass().getName(), e.getMessage());
        }

    }

    private String getImageUrl() {
        JSONArray imagesArray = recipeJson.optJSONArray(JSON_LD_RECIPE_IMAGE);
        if (imagesArray != null) {
            return imagesArray.optString(0);
        }

        JSONObject imagesObject = recipeJson.optJSONObject(JSON_LD_RECIPE_IMAGE);
        if (imagesObject != null) {
            return imagesObject.optString("url");
        }

        return recipeJson.optString(JSON_LD_RECIPE_IMAGE);
    }

}
