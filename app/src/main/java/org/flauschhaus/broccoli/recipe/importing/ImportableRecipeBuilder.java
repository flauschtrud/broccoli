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

    private static final String TYPE = "@type";
    private static final String HOW_TO_SECTION = "HowToSection";
    private static final String ITEM_LIST_ELEMENT = "itemListElement";

    private static final String NAME = "name";
    private static final String RECIPE_DESCRIPTION = "description";
    private static final String RECIPE_INGREDIENT = "recipeIngredient";
    private static final String RECIPE_INSTRUCTIONS = "recipeInstructions";
    private static final String HOW_TO_TEXT = "text";
    private static final String RECIPE_YIELD = "recipeYield";
    private static final String TOTAL_TIME = "totalTime";
    private static final String COOK_TIME = "cookTime";
    private static final String RECIPE_IMAGE = "image";
    private static final String RECIPE_URL = "url";

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
        recipe.setTitle(recipeJson.optString(NAME));
    }

    private void contributeDescription() {
        recipe.setDescription(recipeJson.optString(RECIPE_DESCRIPTION));
    }

    private void contributeServings() {
        JSONArray yieldArray = recipeJson.optJSONArray(RECIPE_YIELD);
        if (yieldArray == null) {
            recipe.setServings(recipeJson.optString(RECIPE_YIELD));
        } else {
            recipe.setServings(yieldArray.optString(0));
        }
    }

    private void contributePreparationTime() {
        if (recipeJson.has(COOK_TIME)) {
            setParsedAndWordedPreparationTime(COOK_TIME);
        }

        if (recipeJson.has(TOTAL_TIME)) {
            setParsedAndWordedPreparationTime(TOTAL_TIME);
        }
    }

    private void setParsedAndWordedPreparationTime(String jsonLdRecipeCookTime) {
        recipe.setPreparationTime(Period.parse(recipeJson.optString(jsonLdRecipeCookTime)).toString(PeriodFormat.wordBased()));
    }

    private void contributeIngredients() {
        JSONArray ingredientArray = recipeJson.optJSONArray(RECIPE_INGREDIENT);
        if (ingredientArray !=  null) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < ingredientArray.length(); i++) {
                list.add(ingredientArray.optString(i));
            }
            recipe.setIngredients(list.stream().collect(Collectors.joining("\n")));
        }
    }

    private void contributeDirections() {
        JSONArray instructionsArray = recipeJson.optJSONArray(RECIPE_INSTRUCTIONS);
        if (instructionsArray == null) {
            recipe.setDirections(recipeJson.optString(RECIPE_INSTRUCTIONS));
        } else {
            List<String> directions = new ArrayList<>();
            for (int i = 0; i < instructionsArray.length(); i++) {

                JSONObject instructionObject = instructionsArray.optJSONObject(i);
                if (instructionObject.has(TYPE) && HOW_TO_SECTION.equals(instructionObject.optString(TYPE))) {
                    directions.add(contributeSection(instructionObject));
                } else {
                    directions.add(contributeStep(instructionsArray, i));
                }

            }
            recipe.setDirections(directions.stream().collect(Collectors.joining("\n")));
        }
    }

    private String contributeSection(JSONObject instructionObject) {
        List<String> steps = new ArrayList<>();
        steps.add(instructionObject.optString(NAME).toUpperCase());
        JSONArray sectionsArray = instructionObject.optJSONArray(ITEM_LIST_ELEMENT);
        if (sectionsArray != null) {
            for (int i = 0; i < sectionsArray.length(); i++) {
                steps.add(contributeStep(sectionsArray, i));
            }
        }
        return steps.stream().collect(Collectors.joining(" "));
    }

    private String contributeStep(JSONArray jsonArray, int position) {
        JSONObject instruction = jsonArray.optJSONObject(position);
        if (instruction == null) {
            return jsonArray.optString(position);
        } else {
            return instruction.optString(HOW_TO_TEXT);
        }
    }

    private void contributeImage() {
        if(!recipeJson.has(RECIPE_IMAGE)) {
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
        JSONArray imagesArray = recipeJson.optJSONArray(RECIPE_IMAGE);
        if (imagesArray != null) {
            return imagesArray.optString(0);
        }

        JSONObject imagesObject = recipeJson.optJSONObject(RECIPE_IMAGE);
        if (imagesObject != null) {
            return imagesObject.optString(RECIPE_URL);
        }

        return recipeJson.optString(RECIPE_IMAGE);
    }

}
